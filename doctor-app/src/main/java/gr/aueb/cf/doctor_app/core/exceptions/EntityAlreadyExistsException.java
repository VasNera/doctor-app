package gr.aueb.cf.doctor_app.core.exceptions;

public class EntityAlreadyExistsException extends AppGenericException{

   private static final String DEFAULT_CODE = "Already exists";

    public EntityAlreadyExistsException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
