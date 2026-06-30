package com.omiicare.qa.appointment;

import com.omiicare.qa.appointment.api.AppointmentResponse;
import com.omiicare.qa.appointment.api.BookAppointmentRequest;
import com.omiicare.qa.shared.api.ApiResponse;
import com.omiicare.qa.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Appointment scheduling API. Permission-based authorization. */
@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "Scheduling and cancellation")
public class AppointmentController {

    private static final int MAX_PAGE_SIZE = 100;

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('appointment:read')")
    @Operation(summary = "List appointments (paginated)")
    public ApiResponse<PageResponse<AppointmentResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return ApiResponse.ok(
                PageResponse.from(
                        appointmentService.list(
                                PageRequest.of(
                                        Math.max(page, 0),
                                        safeSize,
                                        Sort.by("scheduledStart").descending()))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('appointment:read')")
    @Operation(summary = "Fetch an appointment by id")
    public ApiResponse<AppointmentResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(appointmentService.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('appointment:write')")
    @Operation(summary = "Book a new appointment")
    public ApiResponse<AppointmentResponse> book(
            @Valid @RequestBody BookAppointmentRequest request) {
        return ApiResponse.ok(appointmentService.book(request));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('appointment:write')")
    @Operation(summary = "Cancel an appointment")
    public ApiResponse<AppointmentResponse> cancel(@PathVariable Long id) {
        return ApiResponse.ok(appointmentService.cancel(id));
    }
}
