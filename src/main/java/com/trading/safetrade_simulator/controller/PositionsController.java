package com.trading.safetrade_simulator.controller;

import com.trading.safetrade_simulator.DTO.PositionsDTO;
import com.trading.safetrade_simulator.service.PositionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/edit")
    public ResponseEntity<?> editPosition(@RequestBody Map<String, Object> payload) {
//
        try {
            String positionId = payload.get("positionId").toString();
            Double stopLoss = Double.parseDouble(payload.get("sl").toString());
            Double target = Double.parseDouble(payload.get("target").toString());

            System.out.println("Received positionId: " + positionId);
            System.out.println("Received stopLoss: " + stopLoss);
            System.out.println("Received target: " + target);

            positionsService.editPosition(positionId,stopLoss,target);

            return new ResponseEntity<>("Edit succesfully",HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Excerpiton in Edit positon controller : " + e);
            return new ResponseEntity<>("Failed to Edit",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/exit")
    public ResponseEntity<String> exitPosition(@RequestBody Map<String, Object> payload) {
        String positionId = payload.get("positionId").toString();
        Double ltp = Double.parseDouble(payload.get("ltp").toString());
        for(int i=0; i<20; i++){
            System.out.println();
        }
        System.out.println(positionId + " " + ltp);
        try {
            // Perform the exit logic (e.g., update exitPrice, etc.)
            positionsService.exitPosition(positionId,ltp);
            return ResponseEntity.ok("Position exited successfully");
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to Exit",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
