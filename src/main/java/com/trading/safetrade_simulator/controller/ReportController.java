package com.trading.safetrade_simulator.controller;

import com.trading.safetrade_simulator.model.PositionOrders;
import com.trading.safetrade_simulator.service.PositionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private PositionsService positionsService;

    @PostMapping("")
    public ResponseEntity<?> getReport(@RequestBody Map<String, String> dateRange){
        String startDateStr = dateRange.get("startDate");
        String endDateStr = dateRange.get("endDate");

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        try{
            List<PositionOrders> reportlist = positionsService.getReport(startDate,endDate);
            return new ResponseEntity<>(reportlist, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>("Failed to return data",HttpStatus.NOT_FOUND);
        }




    }
}
