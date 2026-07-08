package com.example.Idea.DTO;
import jakarta.validation.constraints.*;
import com.example.Idea.Model.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
public class LoanDto {
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
@Builder
    public static class LoanApplicationRequest{
        @NotNull(message = "Loan type required")
        private Loan.LoanType loanType;

        @NotNull(message = "Principal amount required")
        @DecimalMin(value = "1000.0", message = "Minimum loan amount ₹1000")
        @DecimalMax(value = "10000000.0", message = "Maximum loan amount ₹1 crore")
       private BigDecimal principalAmount;

        @NotNull(message = "Tenure required")
        @Min(value = 1,   message = "Minimum tenure 1 month")
        @Max(value = 360, message = "Maximum tenure 360 months (30 years)")
        private Integer tenureMonths;

        @NotBlank(message = "Disbursement account number required")
        private String disbursementAccountNumber;

        private String purpose;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanApprovalRequest{
        @NotBlank(message = "Loan number required")
        private String loanNumber;

        // override interest rate dena ho to (optional)
        @DecimalMin(value = "0.0", message = "Interest rate must be positive")
        private BigDecimal customInterestRate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanRejectionRequest{

        @NotBlank(message = "Loan number required")
        private String loanNumber;

        @NotBlank(message = "Rejection reason required")
        @Size(min = 10, max = 500, message = "Reason must be 10-500 characters")
        private String rejectionReason;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmiPaymentRequest {

        @NotBlank(message = "Loan number required")
        private String loanNumber;

        @NotBlank(message = "Account number required")
        private String accountNumber;   // jis account se EMI kategi
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoanResponse{
        private Long id;
        private String loanNumber;
        private Loan.LoanType loanType;
        private BigDecimal principalAmount;
        private BigDecimal outstandingAmount;
        private BigDecimal interestRate;
        private Integer tenureMonths;
        private BigDecimal      emiAmount;          // monthly EMI
        private LocalDateTime nextEmiDate;
        private LocalDateTime       endDate;
        private Loan.LoanStatus status;
        private String          rejectionReason;    // agar reject hua
        private String          borrowerName;       // user ka naam
        private String          disbursementAccount;
        private LocalDateTime createdAt;
        private LocalDateTime   updatedAt;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmiCalculationResponse{
        private BigDecimal principalAmount;
        private BigDecimal interestRate;      // annual %
        private Integer    tenureMonths;
        private BigDecimal monthlyEmi;
        private BigDecimal totalAmount;       // total pay karna hoga
        private BigDecimal totalInterest;
    }
}
