package gr.aueb.cf.doctor_app.core.exceptions;

public class InvalidCredentialsException extends AppGenericException{

    private static final String DEFAULT_CODE = "Invalid Credentials";

    public InvalidCredentialsException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
