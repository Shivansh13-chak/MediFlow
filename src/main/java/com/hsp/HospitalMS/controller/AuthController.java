package com.hsp.HospitalMS.controller;

import com.hsp.HospitalMS.dtos.auth.AuthResponse;
import com.hsp.HospitalMS.dtos.auth.LoginRequest;
import com.hsp.HospitalMS.dtos.user.UserResponseDto;
import com.hsp.HospitalMS.dtos.user.UserSignupDto;
import com.hsp.HospitalMS.security.jwt.JwtUtil;
import com.hsp.HospitalMS.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    public UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(
            @Valid @RequestBody UserSignupDto userSignupDto) {

        UserResponseDto response = userService.createuser(userSignupDto);

        return ResponseEntity.ok(response);
    }



        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private JwtUtil jwtUtil;

        @PostMapping("/login")
        public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
        e.printStackTrace();
        throw e;
    }

            String token = jwtUtil.generateToken(request.getEmail());

            return ResponseEntity.ok(new AuthResponse(token));
        }
    }





