package gr.aueb.cf.doctor_app.core.exceptions;

public class InvalidArgumentException extends AppGenericException{

    private static final String DEFAULT_CODE = "Invalid Argument";

    public InvalidArgumentException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
