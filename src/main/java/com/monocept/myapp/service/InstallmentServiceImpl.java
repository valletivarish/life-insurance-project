package com.monocept.myapp.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.InstallmentPaymentRequestDto;
import com.monocept.myapp.entity.Agent;
import com.monocept.myapp.entity.Commission;
import com.monocept.myapp.entity.Installment;
import com.monocept.myapp.entity.Payment;
import com.monocept.myapp.entity.PolicyAccount;
import com.monocept.myapp.enums.CommissionType;
import com.monocept.myapp.enums.InstallmentStatus;
import com.monocept.myapp.enums.PaymentStatus;
import com.monocept.myapp.repository.AgentRepository;
import com.monocept.myapp.repository.CommissionRepository;
import com.monocept.myapp.repository.InstallmentRepository;
import com.monocept.myapp.repository.PaymentRepository;
import com.monocept.myapp.repository.PolicyRepository;

@Service
public class InstallmentServiceImpl implements InstallmentService {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PolicyRepository policyAccountRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private CommissionRepository commissionRepository;

    @Override
    public String processInstallmentPayment(InstallmentPaymentRequestDto request) {
        Installment installment = installmentRepository.findById(request.getInstallmentId())
                .orElseThrow(() -> new RuntimeException("Installment not found"));

        if (installment.getStatus() != InstallmentStatus.PENDING) {
            throw new RuntimeException("Installment is already paid or not due");
        }

        String paymentReference = stripeService.processPayment(request.getPaymentToken(), request.getAmount());

        installment.setAmountPaid(request.getAmount());
        installment.setPaymentDate(LocalDate.now());
        installment.setPaymentReference(paymentReference);
        installment.setStatus(InstallmentStatus.PAID);

        Payment payment = new Payment();
        payment.setChargeId(paymentReference);
        payment.setAmount(request.getAmount());
        payment.setCustomerId(request.getCustomerId());
        payment.setStatus(PaymentStatus.PAID);
        
        PolicyAccount policyAccount = installment.getInsurancePolicy();
       

        paymentRepository.save(payment);

        installmentRepository.save(installment);

        policyAccount.getPayments().add(payment);
        policyAccountRepository.save(policyAccount);

        applyInstallmentCommission(policyAccount, request.getAmount());

        return "Installment payment successful!";
    }

    private void applyInstallmentCommission(PolicyAccount policyAccount, double amount) {
        Agent agent = policyAccount.getAgent();

        if (agent == null) {
            return; 
        }

        double commissionRate = policyAccount.getInsuranceScheme().getInstallmentCommRatio();
        double commissionAmount = amount * commissionRate;

        Commission commission = new Commission();
        commission.setAgent(agent);
        commission.setAmount(commissionAmount);
        commission.setCommissionType(CommissionType.INSTALLMENT);

        commissionRepository.save(commission);

        double updatedTotalCommission = agent.getTotalCommission() + commissionAmount;
        agent.setTotalCommission(updatedTotalCommission);
        agentRepository.save(agent); 
    }
}
