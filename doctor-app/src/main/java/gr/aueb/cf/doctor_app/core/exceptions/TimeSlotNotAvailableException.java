package gr.aueb.cf.doctor_app.core.exceptions;

public class TimeSlotNotAvailableException extends AppGenericException{

    private static final String DEFAULT_CODE = "Not Available";

    public TimeSlotNotAvailableException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
