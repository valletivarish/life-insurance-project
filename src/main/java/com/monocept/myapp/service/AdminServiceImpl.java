package com.monocept.myapp.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.AdminRequestDto;
import com.monocept.myapp.dto.EmployeeRequestDto;
import com.monocept.myapp.dto.EmployeeResponseDto;
import com.monocept.myapp.dto.RegisterDto;
import com.monocept.myapp.entity.Admin;
import com.monocept.myapp.entity.Employee;
import com.monocept.myapp.entity.Role;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.exception.GuardianLifeAssuranceApiException;
import com.monocept.myapp.exception.GuardianLifeAssuranceException;
import com.monocept.myapp.repository.AdminRepository;
import com.monocept.myapp.repository.RoleRepository;
import com.monocept.myapp.repository.UserRepository;

import jakarta.validation.Valid;
@Service
public class AdminServiceImpl implements AdminService {

	 @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private RoleRepository roleRepository;

	    @Autowired
	    private AdminRepository adminRepository;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

		@Override
		public String updateAdmin(@Valid AdminRequestDto adminRequestDto) {
			Admin admin = adminRepository.findById(adminRequestDto.getAdminId()).orElseThrow(()-> new GuardianLifeAssuranceException.UserNotFoundException("Sorry, we couldn't find an admin with ID: " + adminRequestDto.getAdminId()));
			User user = admin.getUser();
			
			if (adminRequestDto.getEmail() != null && adminRequestDto.getEmail() != "") {
				user.setEmail(adminRequestDto.getEmail());
			}
			admin.setActive(adminRequestDto.isActive());
			
			userRepository.save(user);
			adminRepository.save(admin);
			return "admin updated sucessfuly";
		}

		@Override
		public String deleteAdmin(long adminId) {
			Admin admin = adminRepository.findById(adminId)
					.orElseThrow(() -> new GuardianLifeAssuranceException.UserNotFoundException("Sorry, we couldn't find an admin with ID: " + adminId));
			if (!admin.isActive()) {
				throw new GuardianLifeAssuranceException.UserAlreadyDeActivatedException("admin with ID " + adminId + " is already deactivated.");
			}
			admin.setActive(false);
			adminRepository.save(admin);
			return "Admin Deleted Successfully";
		}
		}
	      
	
		
		
		

