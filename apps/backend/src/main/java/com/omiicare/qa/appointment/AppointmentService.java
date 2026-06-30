package com.omiicare.qa.appointment;

import com.omiicare.qa.appointment.api.AppointmentMapper;
import com.omiicare.qa.appointment.api.AppointmentResponse;
import com.omiicare.qa.appointment.api.BookAppointmentRequest;
import com.omiicare.qa.appointment.domain.AppointmentEntity;
import com.omiicare.qa.appointment.domain.AppointmentRepository;
import com.omiicare.qa.audit.AuditService;
import com.omiicare.qa.patient.domain.PatientRepository;
import com.omiicare.qa.provider.domain.ProviderRepository;
import com.omiicare.qa.shared.error.BusinessRuleException;
import com.omiicare.qa.shared.error.ResourceNotFoundException;
import com.omiicare.qa.shared.tenant.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Appointment scheduling use cases. Enforces the documented business rules: an appointment must
 * reference an existing patient and provider in the caller's tenant (BR-APPT-003), must end after
 * it starts (BR-APPT-002), and must not overlap another active appointment for the same provider
 * (BR-APPT-001, no double-booking).
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ProviderRepository providerRepository;
    private final AppointmentMapper mapper;
    private final AuditService auditService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            ProviderRepository providerRepository,
            AppointmentMapper mapper,
            AuditService auditService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.providerRepository = providerRepository;
        this.mapper = mapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> list(Pageable pageable) {
        return appointmentRepository
                .findAllByTenantId(tenantId(), pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse get(Long id) {
        return mapper.toResponse(require(id));
    }

    @Transactional
    public AppointmentResponse book(BookAppointmentRequest request) {
        Long tenantId = tenantId();
        if (!request.scheduledEnd().isAfter(request.scheduledStart())) {
            throw new BusinessRuleException(
                    "BR-APPT-002", "Appointment end must be after its start");
        }
        patientRepository
                .findByIdAndTenantId(request.patientId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", request.patientId()));
        providerRepository
                .findByIdAndTenantId(request.providerId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", request.providerId()));

        long overlapping =
                appointmentRepository.countOverlapping(
                        tenantId,
                        request.providerId(),
                        request.scheduledStart(),
                        request.scheduledEnd());
        if (overlapping > 0) {
            throw new BusinessRuleException(
                    "BR-APPT-001",
                    "Provider already has an appointment overlapping the requested time");
        }

        AppointmentEntity entity = new AppointmentEntity();
        entity.setTenantId(tenantId);
        entity.setPatientId(request.patientId());
        entity.setProviderId(request.providerId());
        entity.setScheduledStart(request.scheduledStart());
        entity.setScheduledEnd(request.scheduledEnd());
        entity.setReason(request.reason());
        entity.setStatus("BOOKED");
        AppointmentEntity saved = appointmentRepository.save(entity);
        auditService.record(
                "APPOINTMENT_BOOK", "Appointment", String.valueOf(saved.getId()), tenantId);
        return mapper.toResponse(saved);
    }

    @Transactional
    public AppointmentResponse cancel(Long id) {
        AppointmentEntity entity = require(id);
        if ("CANCELLED".equals(entity.getStatus())) {
            throw new BusinessRuleException("BR-APPT-004", "Appointment is already cancelled");
        }
        entity.setStatus("CANCELLED");
        auditService.record("APPOINTMENT_CANCEL", "Appointment", String.valueOf(id), tenantId());
        return mapper.toResponse(entity);
    }

    private AppointmentEntity require(Long id) {
        return appointmentRepository
                .findByIdAndTenantId(id, tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    private Long tenantId() {
        return TenantContext.getTenantId();
    }
}
