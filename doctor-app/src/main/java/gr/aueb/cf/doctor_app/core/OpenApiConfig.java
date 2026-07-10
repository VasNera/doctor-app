package gr.aueb.cf.doctor_app.core;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration


    @SecurityScheme(
            name = "Bearer Authentication",
            type = SecuritySchemeType.HTTP,
            bearerFormat = "JWT",
            scheme = "bearer"
    )

    public class OpenApiConfig{

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("DoctorApp API")
                        .version("1.0.0")
                        .description("""
                                  REST API for a single-clinic appointment booking system.
                                  Supports three roles with JWT-based authentication:
                                  - **Admin** — registers doctors in the system
                                  - **Doctor** — activates their account via email,
                                   generates available time slots, and views their schedule
                                  - **Patient** — self-registers, browses doctors' available slots,
                                   and books or cancels appointments
                                  Key features : capability-based authorization,
                                   doctor email activation, automatic time-slot generation,
                                   and concurrency-safe booking
                                   (pessimistic locking to prevent double-booking of the same slot).
                                  """));
    }

    @Bean
    public OperationCustomizer globalSecurityResponses(){
        return (operation, handlerMethod) -> {
            boolean isSecured = handlerMethod.hasMethodAnnotation(SecurityRequirement.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(SecurityRequirement.class);


            if (isSecured){
                operation.getResponses()
                        .addApiResponse("401", new ApiResponse()
                                .description("Unauthorized. Jwt token is missing or invalid"))
                        .addApiResponse("403", new ApiResponse()
                                .description("Forbidden. You don't have permission to access this resource"));
            }
            return operation;

        };
    }

    }

