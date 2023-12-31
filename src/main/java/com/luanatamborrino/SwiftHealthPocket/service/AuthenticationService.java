package com.luanatamborrino.SwiftHealthPocket.service;

import com.luanatamborrino.SwiftHealthPocket.dto.response.LoginResponse;
import com.luanatamborrino.SwiftHealthPocket.security.JwtService;
import com.luanatamborrino.SwiftHealthPocket.dto.request.AuthenticationRequest;
import com.luanatamborrino.SwiftHealthPocket.dto.request.RegisterRequest;
import com.luanatamborrino.SwiftHealthPocket.model.Ruolo;
import com.luanatamborrino.SwiftHealthPocket.model.Utente;
import com.luanatamborrino.SwiftHealthPocket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public void register(RegisterRequest request) {
    //TODO fare i soliti controlli di sicurezza
        Utente utente = Utente.builder()
                .nome(request.getNome())
                .cognome(request.getCognome())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .ruolo(Ruolo.PAZIENTE) //TODO il ruolo deve far parte della request (quindi deve sceglierlo l'utente)
                .build();

        repository.save(utente);
    }

    public LoginResponse authenticate(AuthenticationRequest request) {
        //TODO fare i soliti controlli di sicurezza
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Utente utente = repository.findByEmail(request.getEmail())
                .orElseThrow();

        return LoginResponse.builder()
                .jwt(jwtService.generateToken(utente))
                .build();
    }

    public HttpHeaders putJwtInHttpHeaders(String jwt) {

        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer: " + jwt);

        return headers;
    }
}
