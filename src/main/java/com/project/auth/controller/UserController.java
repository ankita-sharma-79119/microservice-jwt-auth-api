package com.project.auth.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.auth.dto.User;
import com.project.auth.service.UserService;
import com.project.auth.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody User user) {
		userService.registerUser(user);
		return ResponseEntity.ok("User Registered");
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User loginRequest){
		Optional<User> userOptional = userService.findByUserName(loginRequest.getUsername());
		
		if(userOptional.isPresent()) {
			User user = userOptional.get();
			if(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
				String token = jwtUtil.generateToken(loginRequest.getUsername());
				return ResponseEntity.ok("Bearer "+token);
			}
		}
		
		return ResponseEntity.status(401).body("Invalid username or password");
	}
	
	@GetMapping("/profile")
	public ResponseEntity<?> getProfile(){
		return ResponseEntity.ok("Access Granted. Secured profile info!");
	}

}
