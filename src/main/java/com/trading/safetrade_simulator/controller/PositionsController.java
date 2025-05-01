package com.trading.safetrade_simulator.controller;

import com.trading.safetrade_simulator.DTO.PositionsDTO;
import com.trading.safetrade_simulator.service.PositionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/positions")
public class PositionsController {

    @Autowired
    private PositionsService positionsService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllPositions(){
      List<PositionsDTO> list = positionsService.getPositions();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
