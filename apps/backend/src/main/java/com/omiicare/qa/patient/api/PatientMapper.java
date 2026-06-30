package com.omiicare.qa.patient.api;

import com.omiicare.qa.patient.domain.PatientEntity;
import org.mapstruct.Mapper;

/** MapStruct mapper between {@link PatientEntity} and its API representation. */
@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientResponse toResponse(PatientEntity entity);
}
