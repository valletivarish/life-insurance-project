package com.monocept.myapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.monocept.myapp.entity.Customer;
import com.monocept.myapp.entity.User;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

	@Query("SELECT c FROM Customer c WHERE "
		       + "(:name IS NULL OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) "
		       + "AND (:city IS NULL OR LOWER(c.address.city.name) LIKE LOWER(CONCAT('%', :city, '%'))) "
		       + "AND (:state IS NULL OR LOWER(c.address.city.state.name) LIKE LOWER(CONCAT('%', :state, '%'))) "
		       + "AND (:isActive IS NULL OR c.active = :isActive)")
		Page<Customer> findByFilters(@Param("name") String name, 
		                             @Param("city") String city, 
		                             @Param("state") String state, 
		                             @Param("isActive") Boolean isActive, 
		                             Pageable pageable);

	Customer findByUser(User user);


}
