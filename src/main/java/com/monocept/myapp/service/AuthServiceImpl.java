package com.monocept.myapp.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.ChangePasswordRequestDto;
import com.monocept.myapp.dto.LoginDto;
import com.monocept.myapp.dto.RegisterDto;
import com.monocept.myapp.entity.Admin;
import com.monocept.myapp.entity.Role;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
import com.monocept.myapp.repository.AdminRepository;
import com.monocept.myapp.repository.RoleRepository;
import com.monocept.myapp.repository.UserRepository;
import com.monocept.myapp.security.JwtTokenProvider;

@Service
public class AuthServiceImpl implements AuthService {

	private AuthenticationManager authenticationManager;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	private JwtTokenProvider jwtTokenProvider;
	private AdminRepository adminRepository;

	public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
			RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
			AdminRepository adminRepository) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
		this.adminRepository = adminRepository;
	}

	@Override
	public String login(LoginDto loginDto) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtTokenProvider.generateToken(authentication);

		return token;
	}

	@Override
	public String register(RegisterDto registerDto) {
		if (userRepository.existsByUsername(registerDto.getUsername())) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Username already exists!");
		}

		if (userRepository.existsByEmail(registerDto.getEmail())) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Email already exists!");
		}

		User user = new User();
		user.setUsername(registerDto.getUsername());
		user.setEmail(registerDto.getEmail());
		user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

		Set<Role> roles = new HashSet<>();
		for (String roleName : registerDto.getRoles()) {
			Role role = roleRepository.findByName(roleName).orElseThrow(
					() -> new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST, "Role not found: " + roleName));
			roles.add(role);
		}

		user.setRoles(roles);
		userRepository.save(user);
		if (registerDto.getRoles().contains("ROLE_ADMIN")) {
			Admin admin = new Admin();
			admin.setName(registerDto.getName());
			admin.setUser(user);
			admin.setActive(true);
			adminRepository.save(admin);
		}

		return "User registered successfully!";
	}

	private String getEmailFromSecurityContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			return userDetails.getUsername();
		}
		return null;
	}

	@Override
	public String changePassword(ChangePasswordRequestDto changePasswordRequestDto) {
		String userNameOrEmail = getEmailFromSecurityContext();
		User user = userRepository.findByUsernameOrEmail(userNameOrEmail, userNameOrEmail).orElse(null);

		if (user == null) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST,
					"We couldn't find your account. Please try again.");
		}

		if (!passwordEncoder.matches(changePasswordRequestDto.getExistingPassword(), user.getPassword())) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST,
					"The current password you entered is incorrect. Please double-check and try again.");
		}

		if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getConfirmPassword())) {
			throw new GuardianLifeAssuranceApiException(HttpStatus.BAD_REQUEST,
					"The new passwords you entered do not match. Please make sure both passwords are the same.");
		}

		user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
		userRepository.save(user);

		return "Your password has been updated successfully. Please use your new password for future logins.";
	}

}
