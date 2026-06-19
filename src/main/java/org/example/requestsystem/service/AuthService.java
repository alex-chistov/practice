package org.example.requestsystem.service;

import org.example.requestsystem.dto.AuthResponse;
import org.example.requestsystem.dto.LoginRequest;
import org.example.requestsystem.dto.RegisterRequest;
import org.example.requestsystem.model.AppUser;
import org.example.requestsystem.repository.UserRepository;
import org.example.requestsystem.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с таким именем уже существует");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(username), username);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный логин или пароль");
        }

        return new AuthResponse(jwtService.generateToken(request.getUsername()), request.getUsername());
    }
}
