package com.example.Idea.Controller;

//import com.bank.dto.ApiResponse;
//import com.bank.dto.LoanDto;
//import com.bank.service.LoanService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.Idea.DTO.LoanDto;
import com.example.Idea.Service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
//@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Loan", description = "Loan apply, approve, reject aur EMI APIs")
public class LoanController {
    @Autowired
    LoanService loanService;

    // ═══════════════════════════════════════════════════════════════
    //  1. LOAN APPLY
    //  POST /api/loans/apply/{userId}
    //
    //  Body:
    //  {
    //    "loanType": "HOME",
    //    "principalAmount": 500000,
    //    "tenureMonths": 120,
    //    "disbursementAccountNumber": "BANK20241234567890",
    //    "purpose": "Ghar kharidna hai"
    //  }
    // ═══════════════════════════════════════════════════════════════
    @PostMapping("/apply/{userId}")
    @Operation(summary = "Loan ke liye apply karo")
    public ResponseEntity<?> applyLoan(
            @PathVariable Long userId,
            @Valid @RequestBody LoanDto.LoanApplicationRequest request) {

        LoanDto.LoanResponse response = loanService.applyForLoan(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // ═══════════════════════════════════════════════════════════════
    //  2. APPROVE LOAN  (Admin / Employee only)
    //  PUT /api/loans/approve
    //
    //  Body:
    //  {
    //    "loanNumber": "LOAN-20241234567",
    //    "customInterestRate": 8.00    ← optional
    //  }
    // ═══════════════════════════════════════════════════════════════
    @PutMapping("/approve")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @Operation(summary = "Loan approve karo — Admin/Employee only")
    public ResponseEntity<?> approveLoan(
            @Valid @RequestBody LoanDto.LoanApprovalRequest request) {

        LoanDto.LoanResponse response = loanService.approveLoan(request);
        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════════════════
    //  3. REJECT LOAN  (Admin / Employee only)
    //  PUT /api/loans/reject
    //
    //  Body:
    //  {
    //    "loanNumber": "LOAN-20241234567",
    //    "rejectionReason": "Low CIBIL score"
    //  }
    // ═══════════════════════════════════════════════════════════════
    @PutMapping("/reject")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    @Operation(summary = "Loan reject karo — Admin/Employee only")
    public ResponseEntity<?> rejectLoan(
            @Valid @RequestBody LoanDto.LoanRejectionRequest request) {

        LoanDto.LoanResponse response = loanService.rejectLoan(request);
        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════════════════
    //  4. PAY EMI MANUALLY
    //  POST /api/loans/emi/pay
    //
    //  Body:
    //  {
    //    "loanNumber": "LOAN-20241234567",
    //    "accountNumber": "BANK20241234567890"
    //  }
    // ═══════════════════════════════════════════════════════════════
    @PostMapping("/emi/pay")
    @Operation(summary = "Manually EMI bharo")
    public ResponseEntity<?> payEmi(
            @Valid @RequestBody LoanDto.EmiPaymentRequest request) {

        LoanDto.LoanResponse response = loanService.payEmiManually(request);
        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════════════════
    //  5. EMI CALCULATOR
    //  GET /api/loans/calculator?principal=500000&annualRate=8.5&tenureMonths=120
    // ═══════════════════════════════════════════════════════════════
    @GetMapping("/calculator")
    @Operation(summary = "Apply karne se pehle EMI calculate karo")
    public ResponseEntity<?> calculateEmi(
            @RequestParam @DecimalMin("1000") BigDecimal principal,
            @RequestParam @DecimalMin("1.0")  BigDecimal annualRate,
            @RequestParam @Min(1) @Max(360)   Integer tenureMonths) {

        LoanDto.EmiCalculationResponse response =
                loanService.calculateEmi(principal, annualRate, tenureMonths);
        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════════════════
    //  6. GET LOAN BY NUMBER
    //  GET /api/loans/{loanNumber}
    //  e.g. /api/loans/LOAN-20241234567
    // ═══════════════════════════════════════════════════════════════
    @GetMapping("/{loanNumber}")
    @Operation(summary = "Loan number se loan details fetch karo")
    public ResponseEntity<?> getLoanByNumber(
            @PathVariable String loanNumber) {

        return ResponseEntity.ok(loanService.getLoanByNumber(loanNumber));
    }

    // ═══════════════════════════════════════════════════════════════
    //  7. GET ALL LOANS OF A USER
    //  GET /api/loans/user/{userId}
    // ═══════════════════════════════════════════════════════════════
    @GetMapping("/user/{userId}")
    @Operation(summary = "User ke saare loans dekho")
    public ResponseEntity<?> getLoansByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(loanService.getLoansByUser(userId));
    }

    // ═══════════════════════════════════════════════════════════════
    //  8. GET PENDING LOANS  (Admin / Employee only)
    //  GET /api/loans/admin/pending
    // ═══════════════════════════════════════════════════════════════
    @GetMapping("/admin/pending")
    @Operation(summary = "Saare pending loans — review ke liye")
    public ResponseEntity<?> getPendingLoans() {

        return ResponseEntity.ok(loanService.getPendingLoans());
    }

    // ═══════════════════════════════════════════════════════════════
    //  9. GET LOANS BY STATUS  (Admin only)
    //  GET /api/loans/admin/status/{status}
    //  e.g. /api/loans/admin/status/ACTIVE
    // ═══════════════════════════════════════════════════════════════
    @GetMapping("/admin/status/{status}")
    @Operation(summary = "Status ke hisaab se loans filter karo — Admin only")
    public ResponseEntity<?> getLoansByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(loanService.getLoansByStatus(status));
    }

    // ═══════════════════════════════════════════════════════════════
    //  10. GET ALL LOANS  (Admin only)
    //  GET /api/loans/admin/all
    // ═══════════════════════════════════════════════════════════════
    @GetMapping("/admin/all")
    @Operation(summary = "Saare loans dekho — Admin only")
    public ResponseEntity<?> getAllLoans() {

        return ResponseEntity.ok(loanService.getAllLoans());
    }
}