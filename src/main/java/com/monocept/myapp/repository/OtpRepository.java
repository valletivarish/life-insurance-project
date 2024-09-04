package com.monocept.myapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monocept.myapp.entity.OtpEntity;
@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long>{

	void deleteByUsername(String username);

	Optional<OtpEntity> findByUsernameAndOtp(String username, String otp);

}
