package gr.aueb.cf.doctor_app.controller;


import gr.aueb.cf.doctor_app.authentication.AuthenticationService;
import gr.aueb.cf.doctor_app.dto.AuthenticationRequestDTO;
import gr.aueb.cf.doctor_app.dto.AuthenticationResponseDTO;
import gr.aueb.cf.doctor_app.dto.ErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    @Operation(
            summary = "Authenticate a user",
            description = "Validates a username and a password and returns a jwt token."
    )

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200" , description = "Authentication successful",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthenticationResponseDTO.class))),
            @ApiResponse(
                    responseCode = "401" , description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)))
    })

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @Valid @RequestBody AuthenticationRequestDTO requestDTO){
                AuthenticationResponseDTO responseDTO = authenticationService.authenticate(requestDTO);
                return ResponseEntity.ok(responseDTO);
    }

}
