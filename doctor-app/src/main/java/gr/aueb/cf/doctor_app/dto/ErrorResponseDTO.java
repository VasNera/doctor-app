package gr.aueb.cf.doctor_app.dto;

public record ErrorResponseDTO(

        String code,
        String message
)
{
    public ErrorResponseDTO(String code){
        this(code, "");

    }

}
