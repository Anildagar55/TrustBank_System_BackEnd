package com.example.Idea.Controller;

import com.example.Idea.DTO.LoanDto;
import com.example.Idea.Service.LoanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor

public class LoanController {
    private LoanService loanService;

    @PostMapping("/apply/{userId}")
    public ResponseEntity<?>applyLoan(@PathVariable Long userId, @RequestBody LoanDto.LoanApplicationRequest request){
        LoanDto.LoanResponse response=loanService.applyForLoan(userId,request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Loan application submitted succesfully"+response);
    }

    @PutMapping("/approve")
    public ResponseEntity<?>approveLoan(@RequestBody LoanDto.LoanApprovalRequest request){
        LoanDto.LoanResponse response=loanService.approveLoan(request);
        return ResponseEntity.ok("Loan approved and amount disbursed "+response);
    }
    @PutMapping("/reject")
    public ResponseEntity<?> rejectLoan(
            @Valid @RequestBody LoanDto.LoanRejectionRequest request) {

        LoanDto.LoanResponse response = loanService.rejectLoan(request);
        return ResponseEntity.ok("Loan rejected"+response);
    }

    @PostMapping("/emi/pay")
    public ResponseEntity<?> payEmi(
            @Valid @RequestBody LoanDto.EmiPaymentRequest request) {
        LoanDto.LoanResponse response = loanService.payEmiManually(request);
        return ResponseEntity.ok(("EMI payment successful ✓ "+response));
    }
    @GetMapping("/calculator")
    public ResponseEntity<?>calculatorEmi(@RequestParam @DecimalMin("1000")BigDecimal principal,
                                          @RequestParam @DecimalMin("1.0") BigDecimal annualRate,
                                          @RequestParam @Min(1) @Max(360) Integer tenureMonths){
        LoanDto.EmiCalculationResponse response=loanService.calculateEmi(principal,annualRate,tenureMonths);
        return ResponseEntity.ok("EMI Calculated"+response);
    }
    @GetMapping("/{loanNumber}")
    public ResponseEntity<?>getLoanByNumber(@PathVariable String loanNumber){
        return ResponseEntity.ok("Loan details fetched"+loanService.getLoanByNumber(loanNumber));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<?>getLoanByUser(@PathVariable Long userId){
        return ResponseEntity.ok("User loans fetched "+loanService.getLoansByUser(userId));
    }
    @GetMapping("/admin/pending")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
//    @Operation(summary = "Saare pending loans — review ke liye")
    public ResponseEntity<?> getPendingLoans() {

        return ResponseEntity.ok("Pending loans fetched"+loanService.getPendingLoans());
    }

    // ═══════════════════════════════════════════════════════════════
    //  9. GET LOANS BY STATUS  (Admin only)
    //  GET /api/loans/admin/status/{status}
    //  e.g. /api/loans/admin/status/ACTIVE
    // ═══════════════════════════════════════════════════════════════
    @GetMapping("/admin/status/{status}")
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Status ke hisaab se loans filter karo — Admin only")
    public ResponseEntity<?> getLoansByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(status + " loans fetched"+loanService.getLoansByStatus(status));
    }

    // ═══════════════════════════════════════════════════════════════
    //  10. GET ALL LOANS  (Admin only)
    //  GET /api/loans/admin/all
    // ═══════════════════════════════════════════════════════════════
    @GetMapping("/admin/all")
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Saare loans dekho — Admin only")
    public ResponseEntity<?> getAllLoans() {

        return ResponseEntity.ok("All loans fetched"+ loanService.getAllLoans());
    }
}
