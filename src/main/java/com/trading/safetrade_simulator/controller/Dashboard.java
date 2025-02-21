package com.trading.safetrade_simulator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading.safetrade_simulator.service.IIFLService;
import jakarta.annotation.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class Dashboard {

    @Autowired
    private IIFLService iiflService;
    @GetMapping("")
    public ResponseEntity<?> getmarketData() throws JsonProcessingException {

      try {
          if (!iiflService.isSessionTokenPresent()) {
              iiflService.addSessionToken();
          }

          if (!iiflService.isInstrumentPresent()) {
              iiflService.addInstruments();
          }
          return new ResponseEntity<>("Success", HttpStatus.OK);


      }catch (Exception ex){
          ex.printStackTrace();
          return new ResponseEntity<>("Failed", HttpStatus.NOT_FOUND);
      }

    }
}
