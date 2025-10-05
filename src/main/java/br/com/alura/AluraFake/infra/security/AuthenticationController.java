package br.com.alura.AluraFake.infra.security;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid AuthenticationRequest dto) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.password());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String jwt = tokenService.generateToken(authentication);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }
}