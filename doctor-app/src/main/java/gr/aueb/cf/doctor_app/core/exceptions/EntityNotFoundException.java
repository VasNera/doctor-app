package gr.aueb.cf.doctor_app.core.exceptions;

public class EntityNotFoundException extends AppGenericException{

    private static final String DEFAULT_CODE = "Not Found";

    public EntityNotFoundException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
