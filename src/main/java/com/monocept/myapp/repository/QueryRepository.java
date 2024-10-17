package com.monocept.myapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.monocept.myapp.entity.CustomerQuery;
import com.monocept.myapp.entity.Customer;

public interface QueryRepository extends JpaRepository<CustomerQuery, Long> {

	Page<CustomerQuery> findByCustomer(Customer customer, PageRequest pageRequest);

	Page<Query> findAllByResolvedFalse(PageRequest pageRequest);

	@Query("SELECT cq FROM CustomerQuery cq WHERE " +
		       "(:search IS NULL OR :search = '' OR LOWER(cq.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
		       "OR LOWER(cq.message) LIKE LOWER(CONCAT('%', :search, '%'))) " +
		       "AND (:resolved IS NULL OR cq.resolved = :resolved)")
		Page<CustomerQuery> findAllByCriteria(@Param("search") String search, 
		                                      @Param("resolved") Boolean resolved, 
		                                      PageRequest pageable);




}
