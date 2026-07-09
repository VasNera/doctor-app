package gr.aueb.cf.doctor_app.service;

import gr.aueb.cf.doctor_app.core.exceptions.EntityNotFoundException;
import gr.aueb.cf.doctor_app.core.exceptions.InvalidArgumentException;
import gr.aueb.cf.doctor_app.core.exceptions.TimeSlotNotAvailableException;
import gr.aueb.cf.doctor_app.dto.AppointmentInsertDTO;
import gr.aueb.cf.doctor_app.dto.AppointmentReadOnlyDTO;
import gr.aueb.cf.doctor_app.model.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IAppointmentService {

    AppointmentReadOnlyDTO bookAppointment(AppointmentInsertDTO appointmentInsertDTO, String username)
        throws EntityNotFoundException, TimeSlotNotAvailableException;

    Page<AppointmentReadOnlyDTO> getPatientsAppointments
            (String username, AppointmentStatus appointmentStatus, Pageable pageable)
    throws EntityNotFoundException;

    Page<AppointmentReadOnlyDTO> getDoctorsAppointments
            (String username, AppointmentStatus appointmentStatus, Pageable pageable)
        throws EntityNotFoundException;

    AppointmentReadOnlyDTO cancelAppointment(UUID uuid, String username)
            throws EntityNotFoundException, InvalidArgumentException;
}
