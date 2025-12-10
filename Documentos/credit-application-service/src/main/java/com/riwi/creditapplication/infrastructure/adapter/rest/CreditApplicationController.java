package com.riwi.creditapplication.infrastructure.adapter.rest;

import com.riwi.creditapplication.application.usecase.EvaluateCreditApplicationUseCase;
import com.riwi.creditapplication.application.usecase.RegisterCreditApplicationUseCase;
import com.riwi.creditapplication.domain.model.CreditApplication;
import com.riwi.creditapplication.domain.model.RiskEvaluation;
import com.riwi.creditapplication.infrastructure.adapter.rest.dto.CreditApplicationRequest;
import com.riwi.creditapplication.infrastructure.adapter.rest.dto.EvaluateCreditApplicationRequest;
import com.riwi.creditapplication.application.usecase.GetCreditApplicationUseCase;
import com.riwi.creditapplication.application.usecase.GetAffiliateCreditApplicationsUseCase;
import com.riwi.creditapplication.infrastructure.persistence.entity.UserEntity;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller (Input Adapter) for Credit Application operations.
 * This adapter receives HTTP requests and delegates to use cases.
 */
@RestController
@RequestMapping("/credit-applications")
public class CreditApplicationController {

    private final RegisterCreditApplicationUseCase registerCreditApplicationUseCase;
    private final EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase;
    private final GetCreditApplicationUseCase getCreditApplicationUseCase;
    private final GetAffiliateCreditApplicationsUseCase getAffiliateCreditApplicationsUseCase;
    private final com.riwi.creditapplication.infrastructure.adapter.rest.mapper.CreditApplicationMapper creditApplicationMapper;

    public CreditApplicationController(
            RegisterCreditApplicationUseCase registerCreditApplicationUseCase,
            EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase,
            GetCreditApplicationUseCase getCreditApplicationUseCase,
            GetAffiliateCreditApplicationsUseCase getAffiliateCreditApplicationsUseCase,
            com.riwi.creditapplication.infrastructure.adapter.rest.mapper.CreditApplicationMapper creditApplicationMapper) {
        this.registerCreditApplicationUseCase = registerCreditApplicationUseCase;
        this.evaluateCreditApplicationUseCase = evaluateCreditApplicationUseCase;
        this.getCreditApplicationUseCase = getCreditApplicationUseCase;
        this.getAffiliateCreditApplicationsUseCase = getAffiliateCreditApplicationsUseCase;
        this.creditApplicationMapper = creditApplicationMapper;
    }

    /**
     * POST /credit-applications
     * Registers a new credit application.
     * 
     * @param request the credit application request
     * @return the registered credit application
     */
    @PostMapping
    public ResponseEntity<CreditApplication> registerCreditApplication(
            @Valid @RequestBody CreditApplicationRequest request,
            Authentication authentication) {

        CreditApplication creditApplication = creditApplicationMapper.toDomain(request);
        CreditApplication created = registerCreditApplicationUseCase.execute(creditApplication);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * POST /credit-applications/{id}/evaluate
     * Evaluates a credit application.
     * 
     * @param id the credit application ID
     * @return the risk evaluation result
     */
    @PostMapping("/{id}/evaluate")
    @PreAuthorize("hasAnyRole('ANALISTA', 'ADMIN')")
    public ResponseEntity<RiskEvaluation> evaluateCreditApplication(@PathVariable Long id) {
        RiskEvaluation evaluation = evaluateCreditApplicationUseCase.execute(id);
        return ResponseEntity.ok(evaluation);
    }

    /**
     * GET /credit-applications/my-applications
     * Retrieves credit applications for the authenticated affiliate.
     * 
     * @param authentication the authentication object
     * @return list of credit applications
     */
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('AFILIADO')")
    public ResponseEntity<List<CreditApplication>> getMyApplications(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        List<CreditApplication> apps = getAffiliateCreditApplicationsUseCase.execute(user.getEmail());
        return ResponseEntity.ok(apps);
    }

    /**
     * GET /credit-applications/{id}
     * Retrieves a credit application by ID.
     * 
     * @param id the credit application ID
     * @return the credit application
     */
    @GetMapping("/{id}")
    public ResponseEntity<CreditApplication> getCreditApplication(@PathVariable Long id) {
        CreditApplication creditApplication = getCreditApplicationUseCase.execute(id);
        return ResponseEntity.ok(creditApplication);
    }

}
