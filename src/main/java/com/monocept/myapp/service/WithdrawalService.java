package com.monocept.myapp.service;

import java.time.LocalDate;

import com.monocept.myapp.entity.WithdrawalRequest;
import com.monocept.myapp.enums.WithdrawalRequestStatus;
import com.monocept.myapp.util.PagedResponse;

public interface WithdrawalService {

	void approveWithdrawal(long withdrawalId);

	void rejectWithdrawal(long withdrawalId);


	void createAgentWithdrawalRequest(long agentId, double amount, String stripeToken);

	PagedResponse<WithdrawalRequest> getWithdrawalsWithFilters(Long customerId, Long agentId,
			WithdrawalRequestStatus status, LocalDate fromDate, LocalDate toDate, int page, int size, String sortBy,
			String direction);

	PagedResponse<WithdrawalRequest> getCommissionWithdrawalsWithFilters(int page, int size, String sortBy,
			String direction, Long agentId, WithdrawalRequestStatus status, LocalDate fromDate, LocalDate toDate);






}
