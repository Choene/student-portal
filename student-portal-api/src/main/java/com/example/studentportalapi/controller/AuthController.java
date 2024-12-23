package com.example.studentportalapi.controller;

import com.example.studentportalapi.config.JwtUtils;
import com.example.studentportalapi.dto.RegistrationRequest;
import com.example.studentportalapi.model.Student;
import com.example.studentportalapi.service.MailService;
import com.example.studentportalapi.service.OtpService;
import com.example.studentportalapi.service.StudentService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final StudentService studentService;
    private final MailService mailService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(StudentService studentService, MailService mailService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, OtpService otpService) {
        this.studentService = studentService;
        this.mailService = mailService;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        Optional<Student> studentOptional = studentService.login(email, password);

        if (studentOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        Student student = studentOptional.get();

        if (student.isTwoFactorEnabled()) {
            try {
                otpService.sendOtpToEmail(email);
                return ResponseEntity.status(202).body("OTP sent to email");
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Error sending OTP: " + e.getMessage());
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }

        String token = jwtUtils.generateToken(email);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody RegistrationRequest request) {
        System.out.println("Registering user: " + request.getEmail());
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            System.out.println("Passwords do not match");
            return ResponseEntity.badRequest().body("Password and Confirm Password do not match");
        }

        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        Student student = Student.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encryptedPassword)
                .build();

        System.out.println("Saving user: " + student);
        return ResponseEntity.ok(studentService.registerStudent(student));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        if (otpService.validateOtp(email, otp)) {
            String token = jwtUtils.generateToken(email);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(401).body("Invalid or expired OTP");
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        //TODO: Implement forgot password logic (send reset email)
        return ResponseEntity.ok("Password reset link sent to " + email);
    }
}
