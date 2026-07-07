package gr.aueb.cf.doctor_app.controller;


import gr.aueb.cf.doctor_app.authentication.AuthenticationService;
import gr.aueb.cf.doctor_app.dto.AuthenticationRequestDTO;
import gr.aueb.cf.doctor_app.dto.AuthenticationResponseDTO;
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

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @Valid @RequestBody AuthenticationRequestDTO requestDTO){
                AuthenticationResponseDTO responseDTO = authenticationService.authenticate(requestDTO);
                return ResponseEntity.ok(responseDTO);
    }

}
