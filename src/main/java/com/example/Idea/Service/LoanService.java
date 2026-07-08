package com.example.Idea.Service;

import com.example.Idea.DTO.LoanDto;
import com.example.Idea.Model.Loan;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Service
public interface LoanService {
   LoanDto.LoanResponse applyForLoan(Long userId, LoanDto.LoanApplicationRequest request);

   LoanDto.LoanResponse getLoanByNumber(String loanNumber);

   List<LoanDto.LoanResponse> getLoansByUser(Long userId);

   LoanDto.EmiCalculationResponse calculateEmi(BigDecimal principal,
                                               BigDecimal annualRate,
                                               Integer tenureMonths);

   // ── Admin / Employee Operations ──────────────────────────────
   LoanDto.LoanResponse approveLoan(LoanDto.LoanApprovalRequest request);

   LoanDto.LoanResponse rejectLoan(LoanDto.LoanRejectionRequest request);

   List<LoanDto.LoanResponse> getAllLoans();

   List<LoanDto.LoanResponse> getLoansByStatus(String status);

   List<LoanDto.LoanResponse> getPendingLoans();

   // ── EMI Operations ───────────────────────────────────────────
   LoanDto.LoanResponse payEmiManually(LoanDto.EmiPaymentRequest request);

   void processEmiPayments();
}
