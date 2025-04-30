package com.trading.safetrade_simulator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading.safetrade_simulator.DTO.DashboardData;
import com.trading.safetrade_simulator.model.User;
import com.trading.safetrade_simulator.service.CustomUserDetail;
import com.trading.safetrade_simulator.service.DashboardService;
import com.trading.safetrade_simulator.service.IIFLService;
import jakarta.annotation.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class Dashboard {

    @Autowired
    private IIFLService iiflService;

    @Autowired
    private DashboardService dashboardService;
    @GetMapping("")
    public ResponseEntity<?> getDashboardData() throws JsonProcessingException {
     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      try {
          if (!iiflService.isSessionTokenPresent()) {
              iiflService.addSessionToken();
          }

          if (!iiflService.isInstrumentPresent()) {
              iiflService.addInstruments();
          }



          //ADDED
         if(authentication != null){
             DashboardData dashboardData = dashboardService.getDashboardData(authentication);
             return new ResponseEntity<>(dashboardData,HttpStatus.OK);
         }
//          CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
//          String username =  userDetail.getUsername();
//          User user = userRepository.findByEmail(username).get();

          //END ADDED

          return new ResponseEntity<>("Success", HttpStatus.OK);


      }catch (Exception ex){
          ex.printStackTrace();
          return new ResponseEntity<>("Failed", HttpStatus.NOT_FOUND);
      }

    }
}
