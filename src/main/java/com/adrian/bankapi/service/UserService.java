package com.adrian.bankapi.service;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.adrian.bankapi.dto.UserResponse;
import com.adrian.bankapi.exception.EmailAlreadyExistsException;
import com.adrian.bankapi.exception.UserNotFoundException;
import com.adrian.bankapi.repository.UserRepository;
import com.adrian.bankapi.dto.UserRequest;
import com.adrian.bankapi.entity.User;
import com.adrian.bankapi.dto.LoginResponse;
import com.adrian.bankapi.dto.LoginRequest;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("El email ya está registrado");
        }
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBirthDate(request.getBirthDate());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);

    }

    public LoginResponse login(LoginRequest request) {

        System.out.println("=== ENTRANDO EN LOGIN ===");

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Credenciales incorrectas"));

        System.out.println("Email recibido: " + request.getEmail());
        System.out.println("Password introducida: " + request.getPassword());
        System.out.println("Password BD: " + user.getPassword());

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        System.out.println("¿Coinciden?: " + matches);

        if (!matches) {
            throw new UserNotFoundException("Credenciales incorrectas");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(token);
    }

    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));


        return mapToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UserResponse updateUser(Long id, UserRequest userRequest) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setBirthDate(userRequest.getBirthDate());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user) {

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());

        return response;
    }

}