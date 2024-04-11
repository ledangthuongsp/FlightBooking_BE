package com.example.FlightBooking.Services.AuthJWT;


import com.example.FlightBooking.DTOs.Request.Auth.SignInDTO;
import com.example.FlightBooking.DTOs.Request.Auth.SignUpDTO;
import com.example.FlightBooking.Enum.Roles;
import com.example.FlightBooking.Models.Users;
import com.example.FlightBooking.Models.Veritifications;
import com.example.FlightBooking.Repositories.TokenRepository;
import com.example.FlightBooking.Repositories.UserRepository;
import com.example.FlightBooking.Repositories.VeritificationRepository;
import com.example.FlightBooking.Utils.EmailUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import jakarta.mail.MessagingException;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;


    @Autowired
    private VeritificationRepository veritificationRepository;
    @Autowired
    private EmailUtils emailUtil;
    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            TokenRepository tokenRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users signup(SignUpDTO input) {
        Optional<Users> existingUser = userRepository.findByUsername(input.getUsername());

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username '" + input.getUsername() + "' already exists. Please choose a different username.");
        }
        Users user = new Users();
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setFullName(input.getFullName());
        user.setDayOfBirth(input.getDayOfBirth());
        user.setRole(Roles.CUSTOMER.name());
        return userRepository.save(user);
    }

    public Users authenticate(SignInDTO input) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getUsername(),
                            input.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid username or password. Please check your credentials.");
        }

        return userRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found. Please check your username."));
    }
    public String forgot_password(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email: " + email));

        try {
            emailUtil.sendSetPasswordEmail(user.getEmail());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return "Successful! Please check your email";
    }
    public String reset_password(String email, Long otp, String newPassword, String confirmPassword){
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with this email: " + email));

        Veritifications veritifications = veritificationRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Ver not found with this email "));

            if(veritifications.getCodeOTP().equals(otp)){
                if(newPassword.equals(confirmPassword)){
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    return "Successful! Password was reset";
                }else {
                    return "Password do not match! Please fill again";
                }
            }else {
                return "OTP wrong! Please fill again";
            }
    }
    public String change_password(String email, String oldPassword, String newPassword){
        Users users = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: "+ email));

        if(passwordEncoder.encode(oldPassword).equals(users.getPassword())){
            users.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(users);
            return  "Successful! Password was change";
        }else {
            return "Failed! Password is wrong";
        }


    }
}
