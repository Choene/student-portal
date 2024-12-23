package com.example.studentportalapi.controller;

import com.example.studentportalapi.model.Student;
import com.example.studentportalapi.service.OtpService;
import com.example.studentportalapi.service.StudentService;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final StudentService studentService;
    private final OtpService otpService;

    public ProfileController(StudentService studentService, OtpService otpService) {
        this.studentService = studentService;
        this.otpService = otpService;

    }

    @GetMapping
    public ResponseEntity<?> getProfile(@RequestParam String email) {
        Optional<Student> student = studentService.findStudentByEmail(email);
        return student.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/mfa")
    public ResponseEntity<?> toggleTwoFactor(@RequestParam String email, @RequestParam(required = false) String otp) {
        Optional<Student> studentOptional = studentService.findStudentByEmail(email);
        if (studentOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found.");
        }

        Student student = studentOptional.get();

        if (otp != null) {
            boolean isValidOtp = otpService.validateOtp(email, otp);
            if (!isValidOtp) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
            }
        }

        // Toggle two-factor authentication
        student.setTwoFactorEnabled(!student.isTwoFactorEnabled());
        studentService.registerStudent(student);

        return ResponseEntity.ok(Map.of("message", "Two-Factor Authentication status updated successfully.", "twoFactorEnabled", student.isTwoFactorEnabled()));
    }


    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        try {
            boolean otpSent = otpService.sendOtpToEmail(email);
            if (otpSent) {
                return ResponseEntity.ok(Map.of("message", "OTP sent"));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP");
        }
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.validateOtp(email, otp);
        if (isValid) {
            Optional<Student> studentOptional = studentService.findStudentByEmail(email);
            if (studentOptional.isPresent()) {
                Student student = studentOptional.get();
                student.setTwoFactorEnabled(true); // Enable 2FA
                studentService.registerStudent(student);

                return ResponseEntity.ok(Map.of("message", "Two-Factor Authentication enabled successfully.", "twoFactorEnabled", true));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP");
    }
}
