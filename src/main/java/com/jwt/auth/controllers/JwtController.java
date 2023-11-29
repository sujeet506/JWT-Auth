package com.jwt.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jwt.auth.model.JwtRequest;
import com.jwt.auth.model.JwtResponse;
import com.jwt.auth.services.CustomUserDetailsService;
import com.jwt.auth.util.JwtUtil;

@RestController
public class JwtController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@GetMapping("/welcome")
	public String test()
	{
		return "Welcome Page";
	}
	
	@PostMapping("/token")
	//@RequestMapping(value="/token",method=RequestMethod.POST)
	public ResponseEntity<?> generateToken( @RequestBody JwtRequest jwtRequest)
	{
		System.out.println(jwtRequest);

		try {
			this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
		}
		catch(UsernameNotFoundException e) {
			e.printStackTrace();
			throw new UsernameNotFoundException("User not found");
		}
		catch(BadCredentialsException e)
		{
			e.printStackTrace();
			throw new BadCredentialsException("Bad Credential");
		}
		
		UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(jwtRequest.getUsername());
		String token = this.jwtUtil.generateToken(userDetails);
		System.out.println("Token : " +token);
		
		return ResponseEntity.ok(new JwtResponse(token));
	}
}
