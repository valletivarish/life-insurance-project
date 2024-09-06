package com.monocept.myapp.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.StripeChargeDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.model.Charge;
import com.stripe.model.Payout;
import com.stripe.model.Refund;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StripeService {

	@Value("${api.stripe.key}")
	private String stripeApiKey;

	@PostConstruct
	public void init() {
		Stripe.apiKey = stripeApiKey;
	}
//	public StripeTokenDto createCardToken(StripeTokenDto model) {
//
//        try {
//            Map<String, Object> card = new HashMap<>();
//            card.put("number", model.getCardNumber());
//            card.put("exp_month", Integer.parseInt(model.getExpMonth()));
//            card.put("exp_year", Integer.parseInt(model.getExpYear()));
//            card.put("cvc", model.getCvc());
//            Map<String, Object> params = new HashMap<>();
//            params.put("card", card);
//            Token token = Token.create(params);
//            if (token != null && token.getId() != null) {
//                model.setSuccess(true);
//                model.setToken(token.getId());
//            }
//            return model;
//        } catch (StripeException e) {
//            log.error("StripeService (createCardToken)", e);
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }

	public StripeChargeDto chargeAndCreatePolicy(StripeChargeDto chargeRequest) {

		try {
			chargeRequest.setSuccess(false);
			Map<String, Object> chargeParams = new HashMap<>();
			chargeParams.put("amount", (int) (chargeRequest.getAmount() * 100));
			chargeParams.put("currency", "INR");
			chargeParams.put("description",
					"Payment for id " + chargeRequest.getAdditionalInfo().getOrDefault("ID_TAG", ""));
			chargeParams.put("source", chargeRequest.getStripeToken());
			Map<String, Object> metaData = new HashMap<>();
			metaData.put("id", chargeRequest.getChargeId());
			metaData.putAll(chargeRequest.getAdditionalInfo());
			chargeParams.put("metadata", metaData);
			Charge charge = Charge.create(chargeParams);
			chargeRequest.setMessage(charge.getOutcome().getSellerMessage());

			if (charge.getPaid()) {
				chargeRequest.setChargeId(charge.getId());
				chargeRequest.setSuccess(true);

			}
			return chargeRequest;
		} catch (StripeException e) {
			log.error("StripeService (charge)", e);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public StripeChargeDto chargeAndCreatePolicy(StripeChargeDto chargeRequest, String customerName, String customerEmail) {
	    try {
	        chargeRequest.setSuccess(false);
	        Map<String, Object> chargeParams = new HashMap<>();
	        
	        chargeParams.put("amount", (int) (chargeRequest.getAmount() * 100));  
	        chargeParams.put("currency", "INR");
	        
	        chargeParams.put("description", "Payment for policy by customer: " + customerName);
	        
	        chargeParams.put("source", chargeRequest.getStripeToken());
	        
	        Map<String, Object> metaData = new HashMap<>();
	        metaData.put("customer_name", customerName);
	        metaData.put("customer_email", customerEmail);
	        metaData.put("policy_id", chargeRequest.getAdditionalInfo().getOrDefault("ID_TAG", ""));
	        
	        chargeParams.put("metadata", metaData);

	        // Create the charge
	        Charge charge = Charge.create(chargeParams);
	        chargeRequest.setMessage(charge.getOutcome().getSellerMessage());

	        if (charge.getPaid()) {
	            chargeRequest.setChargeId(charge.getId());
	            chargeRequest.setSuccess(true);
	        }

	        return chargeRequest;
	    } catch (StripeException e) {
	        log.error("StripeService (charge)", e);
	        throw new RuntimeException(e.getMessage());
	    }
	}


	public void processStripeRefund(String chargeId, double amount) {
	    try {
	        Map<String, Object> params = new HashMap<>();
	        params.put("charge", chargeId);
	        params.put("amount", (int) (amount * 100));

	        Refund.create(params);
	        System.out.println("Refund successful for charge: " + chargeId);
	    } catch (StripeException e) {
	        throw new RuntimeException("Refund failed: " + e.getMessage());
	    }
	}

	public void processAgentPayout(String stripeToken, double amount) {
	    try {
	        Map<String, Object> payoutParams = new HashMap<>();
	        payoutParams.put("amount", (int) (amount * 100)); 
	        payoutParams.put("currency", "INR");
	        payoutParams.put("destination", stripeToken); 

	        Payout.create(payoutParams);
	        System.out.println("Payout successful for agent.");
	    } catch (StripeException e) {
	        throw new RuntimeException("Payout failed: " + e.getMessage());
	    }
	}

	public Balance retrieveBalance() {
        try {
            return Balance.retrieve();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve Stripe balance: " + e.getMessage());
        }
    }

	public String processPayment(String paymentToken, double amount) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("amount", (int) (amount * 100)); 
            params.put("currency", "INR");
            params.put("source", paymentToken); 

            Charge charge = Charge.create(params);
            return charge.getId(); 
        } catch (StripeException e) {
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }
    }

	public void processCustomerPayout(String stripeToken, double amount) {
	    try {
	        Map<String, Object> params = new HashMap<>();
	        params.put("amount", (int) (amount * 100)); 
	        params.put("currency", "usd");  
	        params.put("source", stripeToken);  

	        Payout.create(params);
	        System.out.println("Payout successful for token: " + stripeToken);
	    } catch (StripeException e) {
	        throw new RuntimeException("Payout failed: " + e.getMessage());
	    }
	}



}
