package com.monocept.myapp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.monocept.myapp.dto.InstallmentPaymentRequestDto;
import com.monocept.myapp.dto.PolicyAccountRequestDto;
import com.monocept.myapp.enums.PremiumType;
import com.monocept.myapp.service.CustomerManagementService;
import com.monocept.myapp.service.InstallmentService;
import com.monocept.myapp.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;

@RestController
@RequestMapping("GuardianLifeAssurance/checkout")
public class CheckoutController {

    @Autowired
    private StripeService stripeService;
    @Autowired
	private CustomerManagementService customerManagementService;
    
    @Autowired
    private InstallmentService installmentService;


    @PostMapping("/create-checkout-session")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> createCheckoutSession(@RequestBody Map<String, Object> requestBody) {
        double amount = Double.parseDouble(requestBody.get("amount").toString());
        String successUrl = "http://localhost:3000/success";
        String cancelUrl = "http://localhost:3000/cancel";

        // Extracting requestData from requestBody
        Map<String, Object> requestData = (Map<String, Object>) requestBody.get("requestData");
        System.out.println(requestData);

        try {
            // Pass the requestData to the Stripe service to include it as metadata
            Session session = stripeService.createCheckoutSession(amount, successUrl, cancelUrl, requestData);
            return ResponseEntity.ok(session.getUrl());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create checkout session: " + e.getMessage());
        }
    }
    
    @PostMapping("{customerId}/policies/installments/{installmentId}/create-checkout-session")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> createInstallmentCheckoutSession(
            @PathVariable(name = "customerId") Long customerId,
            @PathVariable(name = "installmentId") Long installmentId,
            @RequestBody InstallmentPaymentRequestDto paymentRequest) {

    	System.out.println("CustomerId: " + customerId + ", InstallmentId: " + installmentId + ", AmountDue: " + paymentRequest.getAmount());
        try {


            // Call the service to create a Stripe checkout session
            String checkoutSessionUrl = stripeService.createInstallmentCheckoutSession(customerId, installmentId, paymentRequest);
            
            return ResponseEntity.ok(checkoutSessionUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create checkout session: " + e.getMessage());
        }
    }
    @PostMapping("/agent/{agentId}/withdrawal/create-checkout-session")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<String> createAgentWithdrawalCheckoutSession(
            @PathVariable(name = "agentId") Long agentId,
            @RequestBody Map<String, Object> requestBody) {

        double amount = Double.parseDouble(requestBody.get("amount").toString());
        String successUrl = "http://localhost:3000/success";
        String cancelUrl = "http://localhost:3000/cancel";

        try {
            // Metadata to be included in the Stripe session
            Map<String, String> requestData = Map.of(
                "agentId", String.valueOf(agentId),
                "amount", String.valueOf(amount),
                "type", "agentWithdrawal"
            );

            // Create a Stripe checkout session
            Session session = stripeService.createWithdrawalCheckoutSession(amount, successUrl, cancelUrl, requestData);
            return ResponseEntity.ok(session.getUrl());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create checkout session: " + e.getMessage());
        }
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> paymentData) throws DocumentException {
        String sessionId = paymentData.get("sessionId");
        System.out.println("Session ID received: " + sessionId);

        try {

            Session session = Session.retrieve(sessionId);
            System.out.println("Session retrieved: " + session);

            if ("paid".equals(session.getPaymentStatus())) {
                String paymentIntentId = session.getPaymentIntent();
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                String chargeId = paymentIntent.getCharges().getData().get(0).getId();

                String paymentType = session.getMetadata().get("type");

                if ("policyPurchase".equals(paymentType)) {

                    String insuranceSchemeId = session.getMetadata().get("insuranceSchemeId");
                    String premiumType = session.getMetadata().get("premiumType");
                    String policyTerm = session.getMetadata().get("policyTerm");
                    String premiumAmount = session.getMetadata().get("premiumAmount");
                    String customerIdString = session.getMetadata().get("customerId");
                    String agentIdString=session.getMetadata().get("agentId");

                    PolicyAccountRequestDto accountRequestDto = new PolicyAccountRequestDto();
                    accountRequestDto.setInsuranceSchemeId(Long.parseLong(insuranceSchemeId));
                    accountRequestDto.setPremiumType(PremiumType.valueOf(premiumType));
                    accountRequestDto.setPolicyTerm(Long.parseLong(policyTerm));
                    accountRequestDto.setPremiumAmount(Double.parseDouble(premiumAmount));
                    accountRequestDto.setStripeToken(chargeId);
                    accountRequestDto.setAgentId(Long.parseLong(agentIdString));

                    Long customerId = Long.parseLong(customerIdString);
                    Long policyId = customerManagementService.processPolicyPurchase(accountRequestDto, customerId);

                    return ResponseEntity.ok(Map.of("success", true, "policyId", policyId, "customerId", customerId,"paymentType",paymentType));

                } else if ("installmentPayment".equals(paymentType)) {
                    // Handle installment payment
                    String installmentId = session.getMetadata().get("installmentId");
                    String customerIdString = session.getMetadata().get("customerId");
                    String amountDue = session.getMetadata().get("amountDue");

                    InstallmentPaymentRequestDto paymentRequest = new InstallmentPaymentRequestDto();
                    paymentRequest.setInstallmentId(Long.parseLong(installmentId));
                    paymentRequest.setPaymentToken(chargeId);  // Set the Stripe chargeId as the payment token
                    paymentRequest.setAmount(Double.parseDouble(amountDue));
                    paymentRequest.setCustomerId(Long.parseLong(customerIdString));

                    long policyNo = installmentService.processInstallmentPayment(paymentRequest);

                    return ResponseEntity.ok(Map.of("success", true, "message", "Installment payment processed successfully","paymentType",paymentType, "customerId", customerIdString,"policyNo",policyNo));

                } else {
                    // Return a response for an unrecognized payment type
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Unrecognized payment type"));
                }

            } else {
                // Payment was not successful
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Payment verification failed."));
            }

        } catch (StripeException e) {
            // Handle Stripe API errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error verifying payment: " + e.getMessage()));
        } catch (Exception e) {
            // Handle general errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }




    
    

}
