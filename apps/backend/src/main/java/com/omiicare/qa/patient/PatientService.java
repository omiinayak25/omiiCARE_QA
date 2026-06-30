package com.omiicare.qa.patient;

import com.omiicare.qa.audit.AuditService;
import com.omiicare.qa.patient.api.CreatePatientRequest;
import com.omiicare.qa.patient.api.PatientMapper;
import com.omiicare.qa.patient.api.PatientResponse;
import com.omiicare.qa.patient.api.UpdatePatientRequest;
import com.omiicare.qa.patient.domain.PatientEntity;
import com.omiicare.qa.patient.domain.PatientRepository;
import com.omiicare.qa.shared.error.ConflictException;
import com.omiicare.qa.shared.error.ResourceNotFoundException;
import com.omiicare.qa.shared.tenant.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Patient use cases. Every operation is tenant-scoped via {@link TenantContext} and mutations are
 * audited. MRN uniqueness is enforced per tenant; an MRN is auto-generated when the caller omits
 * it.
 */
@Service
public class PatientService {

    private final PatientRepository repository;
    private final PatientMapper mapper;
    private final AuditService auditService;

    public PatientService(
            PatientRepository repository, PatientMapper mapper, AuditService auditService) {
        this.repository = repository;
        this.mapper = mapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> search(String term, Pageable pageable) {
        String normalized = StringUtils.hasText(term) ? term.trim() : null;
        return repository.search(tenantId(), normalized, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PatientResponse get(Long id) {
        return mapper.toResponse(require(id));
    }

    @Transactional
    public PatientResponse create(CreatePatientRequest request) {
        Long tenantId = tenantId();
        String mrn = StringUtils.hasText(request.mrn()) ? request.mrn() : generateMrn(tenantId);
        if (repository.existsByTenantIdAndMrn(tenantId, mrn)) {
            throw new ConflictException("A patient with MRN " + mrn + " already exists");
        }
        PatientEntity entity = new PatientEntity();
        entity.setTenantId(tenantId);
        entity.setMrn(mrn);
        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
        entity.setDateOfBirth(request.dateOfBirth());
        entity.setGender(request.gender());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        PatientEntity saved = repository.save(entity);
        auditService.record("PATIENT_CREATE", "Patient", String.valueOf(saved.getId()), tenantId);
        return mapper.toResponse(saved);
    }

    @Transactional
    public PatientResponse update(Long id, UpdatePatientRequest request) {
        PatientEntity entity = require(id);
        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
        entity.setDateOfBirth(request.dateOfBirth());
        entity.setGender(request.gender());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setStatus(request.status());
        auditService.record("PATIENT_UPDATE", "Patient", String.valueOf(id), tenantId());
        return mapper.toResponse(entity);
    }

    @Transactional
    public void delete(Long id) {
        PatientEntity entity = require(id);
        repository.delete(entity);
        auditService.record("PATIENT_DELETE", "Patient", String.valueOf(id), tenantId());
    }

    private PatientEntity require(Long id) {
        return repository
                .findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
    }

    private String generateMrn(Long tenantId) {
        return "MRN-" + tenantId + "-" + System.nanoTime();
    }

    private Long tenantId() {
        return TenantContext.getTenantId();
    }
}
