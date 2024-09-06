package com.monocept.myapp.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.monocept.myapp.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	@Query("SELECT p FROM Payment p WHERE "
            + "(:minAmount IS NULL OR p.amount >= :minAmount) "
            + "AND (:maxAmount IS NULL OR p.amount <= :maxAmount) "
            + "AND (:startDate IS NULL OR p.paymentDate >= :startDate) "
            + "AND (:endDate IS NULL OR p.paymentDate <= :endDate) "
            + "AND (:customerId IS NULL OR p.customerId = :customerId) "
            + "AND (:paymentId IS NULL OR p.paymentId = :paymentId)")
    Page<Payment> findByFilters(@Param("minAmount") Double minAmount,
                                @Param("maxAmount") Double maxAmount,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate,
                                @Param("customerId") String customerId,
                                @Param("paymentId") Long paymentId,
                                Pageable pageable);

}
