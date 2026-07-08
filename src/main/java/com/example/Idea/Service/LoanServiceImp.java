package com.example.Idea.Service;

import com.example.Idea.DTO.LoanDto;
import com.example.Idea.ExceptionHandle.InsufficientBalanceException;
import com.example.Idea.ExceptionHandle.InvalidLoanStatusException;
import com.example.Idea.ExceptionHandle.ResourceNotFoundException;
import com.example.Idea.Model.Bank;
import com.example.Idea.Model.Loan;
import com.example.Idea.Model.Transaction;
import com.example.Idea.Model.User;
import com.example.Idea.Randomgenerated.NumberGenerator;
import com.example.Idea.Repository.BankRepository;
import com.example.Idea.Repository.LoanRepository;
import com.example.Idea.Repository.TransactionRepository;
import com.example.Idea.Repository.UserRepository;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class LoanServiceImp implements LoanService {
@Autowired
    private LoanRepository loanRepository;
@Autowired
    private UserRepository userRepository;
@Autowired
    private BankRepository bankRepository;
@Autowired
    private TransactionRepository transactionRepository;

    private static final BigDecimal HOME_RATE=new BigDecimal("8.50");
    private static final BigDecimal PERSONAL_RATE  = new BigDecimal("12.00");
    private static final BigDecimal VEHICLE_RATE   = new BigDecimal("9.00");
    private static final BigDecimal EDUCATION_RATE = new BigDecimal("7.00");
    private static final BigDecimal BUSINESS_RATE  = new BigDecimal("10.50");

    @Override
    public LoanDto.LoanResponse applyForLoan(Long userId, LoanDto.LoanApplicationRequest request) {
 User user=userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user id not found "+userId));
      Bank bank=bankRepository.findByAccountNumber(request.getDisbursementAccountNumber())
              .orElseThrow(()->new ResourceNotFoundException("account not found : "+request.getDisbursementAccountNumber()));

      BigDecimal rate=getInterestRate(request.getLoanType());
      BigDecimal emi=calculateEmiAmount(request.getPrincipalAmount(),rate,request.getTenureMonths());
        NumberGenerator ob=new NumberGenerator();
      String loanNumber=ob.generateLoanNumber();

        Loan loan=Loan.builder()
                .loanNumber(loanNumber)
                .loanType(request.getLoanType())
                .principalAmount(request.getPrincipalAmount())
                .outstandingAmount(request.getPrincipalAmount())
                .interestRate(rate)
                .tenureMonths(request.getTenureMonths())
                .emiAmount(emi)
                .createdAt(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(request.getTenureMonths()))
                .status(Loan.LoanStatus.PENDING)
                .accNumber(bank.getAccountNumber())   // <-- Add this
                .user(user)
                .bank(bank)
                .build();
         loan=loanRepository.save(loan);
      return mapToResponse(loan);
    }

    @Override
    public LoanDto.LoanResponse getLoanByNumber(String loanNumber) {
        return mapToResponse(getLoanEntity(loanNumber));
    }

    @Override
    public List<LoanDto.LoanResponse> getLoansByUser(Long userId) {
        if (!userRepository.existsById(userId)){
            throw new ResourceNotFoundException("Usr not found: "+userId);
        }return loanRepository.findByUserId(userId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public LoanDto.EmiCalculationResponse calculateEmi(BigDecimal principal, BigDecimal annualRate, Integer tenureMonths) {
                       BigDecimal emi=calculateEmiAmount(principal,annualRate,tenureMonths);
                       BigDecimal totalAmount=emi.multiply(BigDecimal.valueOf(tenureMonths));
                       BigDecimal totalInterest=totalAmount.subtract(principal);

                       return LoanDto.EmiCalculationResponse.builder()
                .principalAmount(principal)
                .interestRate(annualRate)
                .tenureMonths(tenureMonths)
                .monthlyEmi(emi)
                .totalAmount(totalAmount)
                .totalInterest(totalInterest)
                .build();
    }

    @Override
    public LoanDto.LoanResponse approveLoan(LoanDto.LoanApprovalRequest request)throws InvalidLoanStatusException {
       Loan loan=getLoanEntity(request.getLoanNumber());
       if (loan.getStatus() != Loan.LoanStatus.PENDING){
           throw new InvalidLoanStatusException("Loan "+request.getLoanNumber()+" is not in PENDING status");
       }
       if (request.getCustomInterestRate() !=null){
           loan.setInterestRate(request.getCustomInterestRate());
           BigDecimal newEmi=calculateEmiAmount(loan.getPrincipalAmount()
                   ,request.getCustomInterestRate(),loan.getTenureMonths());
           loan.setEmiAmount(newEmi);
       }
       loan.setStatus(Loan.LoanStatus.ACTIVE);
       loan.setNextEmiDate(LocalDateTime.now().plusMinutes(1));

       Bank bank=loan.getBank();
       bank.setAmount(bank.getAmount()+loan.getPrincipalAmount().doubleValue());
       bankRepository.save(bank);

       NumberGenerator ob=new NumberGenerator();
        Transaction txn=Transaction.builder()
                .transactionId(ob.generateTransactionId())
                .type(Transaction.TransactionType.LOAN_DISBURSEMENT)
                .amount(loan.getPrincipalAmount().doubleValue())
                .balanceAfter(bank.getAmount())
                .createdAt(LocalDateTime.now())
                .description("loan disturesed :"+loan.getLoanNumber())
                .status(Transaction.TransactionStatus.SUCCESS)
                .bank(bank)
                .build();
        transactionRepository.save(txn);
        loan=loanRepository.save(loan);
        return mapToResponse(loan);
    }

    @Override
    public LoanDto.LoanResponse rejectLoan(LoanDto.LoanRejectionRequest request) {
       Loan loan=getLoanEntity(request.getLoanNumber());
       if (loan.getStatus() != Loan.LoanStatus.PENDING){
           throw new InvalidLoanStatusException("loan "+request.getLoanNumber()+" is not in PENDING status");
       }
       loan.setStatus(Loan.LoanStatus.REJECTED);
       loan.setRejectionReason(request.getRejectionReason());
       loan=loanRepository.save(loan);

        return mapToResponse(loan);
    }

    @Override
    public List<LoanDto.LoanResponse> getAllLoans() {
        return loanRepository
                .findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<LoanDto.LoanResponse> getLoansByStatus(String status) {
        Loan.LoanStatus loanStatus= Loan.LoanStatus.valueOf(status.toUpperCase());
        return loanRepository.findByStatus(loanStatus).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<LoanDto.LoanResponse> getPendingLoans() {
        return loanRepository.findPendingLoans().stream().map(this::mapToResponse).toList();
    }

    @Override
    public LoanDto.LoanResponse payEmiManually(LoanDto.EmiPaymentRequest request) {

        Loan loan = getLoanEntity(request.getLoanNumber());

        if (loan.getStatus() != Loan.LoanStatus.ACTIVE) {
            throw new InvalidLoanStatusException("Loan is not ACTIVE");
        }

        Bank bank = bankRepository
                .findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account not found: " + request.getAccountNumber()));

        if (bank.getAmount() < loan.getEmiAmount().doubleValue()) {
            throw new InsufficientBalanceException(
                    "Insufficient balance for EMI. Required: ₹" + loan.getEmiAmount()
                            + " | Available: ₹" + bank.getAmount());
        }

        // Balance ghata do
        bank.setAmount(bank.getAmount()-loan.getEmiAmount().doubleValue());
        bankRepository.save(bank);

        // Outstanding update karo
        loan.setOutstandingAmount(loan.getOutstandingAmount().subtract(loan.getEmiAmount()));
        loan.setNextEmiDate(loan.getNextEmiDate().plusMinutes(1));

        // Loan close hua?
        if (loan.getOutstandingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setOutstandingAmount(BigDecimal.ZERO);
            loan.setStatus(Loan.LoanStatus.CLOSED);
        }

        loan = loanRepository.save(loan);

        NumberGenerator ob=new NumberGenerator();
        // Transaction record
        Transaction txn = Transaction.builder()
                .transactionId(ob.generateTransactionId())
                .type(Transaction.TransactionType.LOAN_REPAYMENT)
                .amount(loan.getEmiAmount().doubleValue())
                .balanceAfter(bank.getAmount())
                .description("EMI payment: " + loan.getLoanNumber())
                .createdAt(LocalDateTime.now())
                .status(Transaction.TransactionStatus.SUCCESS)
                .bank(bank)
                .build();
        transactionRepository.save(txn);

        return mapToResponse(loan);
    }
    @Override
    @Transactional
    @Scheduled(fixedRate = 60000) // Every 1 minute
    public void processEmiPayments() {

        System.out.println("===== Scheduler Running ===== " + LocalDateTime.now());

        List<Loan> dueLoans = loanRepository.findLoansWithEmiDue(LocalDateTime.now());

        System.out.println("Due Loans: " + dueLoans.size());

        for (Loan loan : dueLoans) {
            try {

                System.out.println("Processing Loan : " + loan.getLoanNumber());

                Bank bank = loan.getBank();

                if (bank == null || bank.getAccountNumber() == null) {
                    System.out.println("No account linked for loan: " + loan.getLoanNumber());
                    continue;
                }

                // Insufficient Balance
                if (bank.getAmount() < loan.getEmiAmount().doubleValue()) {

                    System.out.println("Insufficient Balance for Loan : " + loan.getLoanNumber());

                    loan.setStatus(Loan.LoanStatus.DEFAULTED);
                    loanRepository.save(loan);

                    continue;
                }

                // Deduct EMI
                bank.setAmount(bank.getAmount() - loan.getEmiAmount().doubleValue());
                bankRepository.save(bank);

                loan.setOutstandingAmount(
                        loan.getOutstandingAmount().subtract(loan.getEmiAmount())
                );

                // Next EMI after 1 minute
                loan.setNextEmiDate(LocalDateTime.now().plusMinutes(1));

                if (loan.getOutstandingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    loan.setOutstandingAmount(BigDecimal.ZERO);
                    loan.setStatus(Loan.LoanStatus.CLOSED);
                }

                loanRepository.save(loan);

                Transaction txn = Transaction.builder()
                        .transactionId(new NumberGenerator().generateTransactionId())
                        .type(Transaction.TransactionType.LOAN_REPAYMENT)
                        .amount(loan.getEmiAmount().doubleValue())
                        .balanceAfter(bank.getAmount())
                        .createdAt(LocalDateTime.now())
                        .description("Auto EMI : " + loan.getLoanNumber())
                        .status(Transaction.TransactionStatus.SUCCESS)
                        .bank(bank)
                        .build();

                transactionRepository.save(txn);

                System.out.println("EMI Deducted Successfully : " + loan.getLoanNumber());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//      /**
//     * EMI Formula:
//     * EMI = P × r × (1+r)^n / ((1+r)^n - 1)
//     *
//     * P = Principal
//     * r = monthly interest rate = annual rate / 12 / 100
//     * n = tenure in months
//     */
    private BigDecimal calculateEmiAmount(BigDecimal principal,BigDecimal annualRate,int tenureMonths){
        BigDecimal r=annualRate.divide(BigDecimal.valueOf(1200),10, RoundingMode.HALF_UP);
        BigDecimal onePlusR=BigDecimal.ONE.add(r);
       BigDecimal onePlusRpowN=onePlusR.pow(tenureMonths,new MathContext(10));
       BigDecimal numerator=principal.multiply(r).multiply(onePlusRpowN);
       BigDecimal denominator=onePlusRpowN.subtract(BigDecimal.ONE);
       return numerator.divide(denominator,2,RoundingMode.HALF_UP);
    }

    private BigDecimal getInterestRate(Loan.LoanType type){
        return switch (type){
            case HOME -> HOME_RATE;
            case PERSONAL -> PERSONAL_RATE;
            case VEHICLE -> VEHICLE_RATE;
            case EDUCATION -> EDUCATION_RATE;
            case BUSINESS -> BUSINESS_RATE;
        };
    }
    private LoanDto.LoanResponse mapToResponse(Loan l){
        return LoanDto.LoanResponse.builder()
                .id(l.getId())
                .loanNumber(l.getLoanNumber())
                .loanType(l.getLoanType())
                .principalAmount(l.getPrincipalAmount())
                .outstandingAmount(l.getOutstandingAmount())
                .interestRate(l.getInterestRate())
                .tenureMonths(l.getTenureMonths())
                .emiAmount(l.getEmiAmount())
                .nextEmiDate(l.getNextEmiDate())
                .endDate(l.getEndDate())
                .status(l.getStatus())
                .rejectionReason(l.getRejectionReason())
                .borrowerName(l.getUser().getName())
                .disbursementAccount(l.getAccNumber() !=null ? l.getBank().getAccountNumber():null)
                .createdAt(l.getCreatedAt())
                .updatedAt(l.getUpdatedAt())
                .build();
    }

    private Loan getLoanEntity(String loanNumber){
        return loanRepository.findByLoanNumber(loanNumber)
                .orElseThrow(()->new ResourceNotFoundException("Loan not found : "+loanNumber));
    }
}
