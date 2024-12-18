package com.example.studentportalapi.controller;

import com.example.studentportalapi.dto.RegistrationRequest;
import jakarta.validation.Valid;
import com.example.studentportalapi.model.Student;
import com.example.studentportalapi.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Student loginRequest) {
        return studentService.login(loginRequest.getEmail(), loginRequest.getPassword())
                .map(student -> ResponseEntity.ok("Login successful! Welcome, " + student.getName()))
                .orElse(ResponseEntity.status(401).body("Invalid email or password"));
    }

    // TODO: Add validation and password encryption later
    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody RegistrationRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Password and Confirm Password do not match");
        }

        Student student = Student.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword()) // Plain password for now; encryption comes later
                .build();

        return ResponseEntity.ok(studentService.registerStudent(student));
    }


    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam String email) {
        return studentService.findStudentByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
