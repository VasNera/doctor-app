package gr.aueb.cf.doctor_app.core.exceptions;

public class TimeSlotNotAvailableException extends AppGenericException{

    private static final String DEFAULT_CODE = "_NOT_AVAILABLE";

    public TimeSlotNotAvailableException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
