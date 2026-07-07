package gr.aueb.cf.doctor_app.authentication;

import gr.aueb.cf.doctor_app.dto.AuthenticationRequestDTO;
import gr.aueb.cf.doctor_app.dto.AuthenticationResponseDTO;
import gr.aueb.cf.doctor_app.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO requestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.username(),requestDTO.password()));
        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(authentication.getName(),user.getRole().getName());
        return new AuthenticationResponseDTO(token);


    }
}
