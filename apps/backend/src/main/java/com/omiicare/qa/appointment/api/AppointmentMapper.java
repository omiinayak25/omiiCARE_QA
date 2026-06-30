package com.omiicare.qa.appointment.api;

import com.omiicare.qa.appointment.api.AppointmentResponse;
import com.omiicare.qa.appointment.domain.AppointmentEntity;
import org.mapstruct.Mapper;

/** MapStruct mapper between {@link AppointmentEntity} and its API representation. */
@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    AppointmentResponse toResponse(AppointmentEntity entity);
}
