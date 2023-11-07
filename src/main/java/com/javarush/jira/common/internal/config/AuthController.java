package com.javarush.jira.common.internal.config;

import com.javarush.jira.common.internal.config.jwt.JwtRequest;
import com.javarush.jira.common.internal.config.jwt.JwtResponse;
import com.javarush.jira.common.internal.config.jwt.JwtTokenUtils;
import com.javarush.jira.common.internal.config.jwt.UserNotFoundError;
import com.javarush.jira.login.AuthUser;
import com.javarush.jira.login.internal.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @PostMapping("/api/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest jwtRequest) {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername()
                            , jwtRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new UserNotFoundError(HttpStatus.UNAUTHORIZED.toString()
                    , e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = new AuthUser(userRepository.getExistedByEmail(jwtRequest.getUsername()));
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
