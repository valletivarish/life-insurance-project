//package com.monocept.myapp.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.monocept.myapp.service.StripeService;
//
//@RestController
//@RequestMapping("/GuardianLifeAssurance")
//@PreAuthorize("hasRole('CUSTOMER')")
//public class StripeApiController {
//	
//	@Autowired
//	private StripeService stripeService;
//	
////	@PostMapping("/card/token")
////    @ResponseBody
////    public StripeTokenDto createCardToken(@RequestBody StripeTokenDto model) {
////
////
////        return stripeService.createCardToken(model);
////    }
//
////    @PostMapping("/charge")
////    @ResponseBody
////    public StripeChargeDto charge(@RequestBody StripeChargeDto model) {
////
////        return stripeService.charge(model);
////    }
//
//}
