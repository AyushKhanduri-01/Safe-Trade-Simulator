package com.trading.safetrade_simulator.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.safetrade_simulator.DTO.PositionsDTO;
import com.trading.safetrade_simulator.Repositories.PositionRepository;
import com.trading.safetrade_simulator.Repositories.UserRepository;
import com.trading.safetrade_simulator.enums.OrderStatus;
import com.trading.safetrade_simulator.model.*;
import com.trading.safetrade_simulator.service.CustomUserDetail;
import com.trading.safetrade_simulator.service.IIFLService;
import com.trading.safetrade_simulator.service.PositionsService;
import com.trading.safetrade_simulator.service.RedisOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class PositionsServiceImpl implements PositionsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private RedisOperationService redisOperationService;

    @Autowired
    private IIFLService iiflService;

    @Autowired
    private RestTemplate restTemplate;

     @Value("${IIFL.quoteUrl}")
    private String quoteUrl;

    @Override
    public List<PositionsDTO> getPositions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
            String username = userDetail.getUsername();
            User user = userRepository.findByEmail(username).get();


            ZoneId zone = ZoneId.of("Asia/Kolkata"); // Set to Asia/Kolkata explicitly
            LocalDate today = LocalDate.now(zone);

            Date startOfDay = Date.from(today.atStartOfDay(zone).toInstant());
            Date endOfDay = Date.from(today.plusDays(1).atStartOfDay(zone).toInstant());

            List<PositionOrders> positions = positionRepository.findByUserAndCreatedAtBetween(user, startOfDay, endOfDay);

//            List<PositionOrders> positions = positionRepository.findByUser(user);
            List<PositionsDTO> returnList = new ArrayList<>();
            Map<Integer,PositionsDTO> map  = new HashMap<>();

            for(PositionOrders positionOrders : positions){
                PositionsDTO positionsDTO = new PositionsDTO();
                positionsDTO.setPositionId(positionOrders.getId());
                positionsDTO.setProductType(positionOrders.getProductType());
                positionsDTO.setOrderDuration(positionOrders.getOrderDuration());
                positionsDTO.setInstrumentDescription(positionOrders.getInstrumentDescription());
                positionsDTO.setQuantity(positionOrders.getQuantity());
                positionsDTO.setAvgPrice(positionOrders.getPrice());
                positionsDTO.setType(positionOrders.getOrderType());
                positionsDTO.setStopLoss(positionOrders.getStopLoss());
                positionsDTO.setTargetPrice(positionOrders.getTarget());
                positionsDTO.setExitPrice(positionOrders.getExitPrice());

                if(positionOrders.getExitPrice() > 0){
                    positionsDTO.setPandL(positionOrders.getPAndL());
                    positionsDTO.setChange(String.valueOf(positionOrders.getPercentChange()));
                }
//                private double LTP;
//                private double PandL;
//                private String change;

                returnList.add(positionsDTO);
            }


            Object object = redisOperationService.findByKey("IIFLSession", String.class);
            if(object != null){
            String token = object.toString();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", token);
            StringBuilder instrumentsArray = new StringBuilder();

               for(int i=0; i<positions.size(); i++){

                if(positions.get(i).getExitPrice() > 0){
                    continue;
                }
                   Instruments instruments =redisOperationService.findByKey(positions.get(i).getInstrumentDescription(),Instruments.class);
                   if(instruments == null){
                       System.out.println("Skipped instrument : " + instruments.getExchangeInstrumentID());
                       continue;
                   }

                int exchangeInstrumentId = Integer.parseInt(instruments.getExchangeInstrumentID());
                map.put(exchangeInstrumentId,returnList.get(i));
                instrumentsArray.append(String.format(
                        " {\n" +
                                " \"exchangeSegment\": 2,\n" +
                                " \"exchangeInstrumentID\": %d\n" +
                                " }",
                        exchangeInstrumentId));
                if (i < positions.size() - 1) {
                    instrumentsArray.append(",\n");
                }
              }
               String requestBody = String.format(
                    "{\n" +
                            " \"instruments\": [\n" +
                            "%s\n" +
                            " ],\n" +
                            " \"xtsMessageCode\": 1512,\n" +
                            " \"publishFormat\": \"JSON\"\n" +
                            "}",
                    instrumentsArray.toString());

                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            try{
                ResponseEntity<String> response = restTemplate.exchange(quoteUrl,
                        HttpMethod.POST, entity, String.class);
                if(response.getStatusCode() == HttpStatus.BAD_REQUEST){
                    System.out.println("Bad request " + response.getBody());
                }
                else{

                    String responseBody = response.getBody();
                    System.out.println("response body : " + responseBody);
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(responseBody);
                    JsonNode listQuotes = root.path("result").path("listQuotes");

                    for (JsonNode quoteNode : listQuotes) {
                        JsonNode quote = mapper.readTree(quoteNode.asText());
                        int instrumentId = quote.path("ExchangeInstrumentID").asInt();
                        double price = quote.path("LastTradedPrice").asDouble();

                        PositionsDTO pos = map.get(instrumentId);
                        pos.setLTP(price);

                        if(pos.getExitPrice() > 0){
                            double pandl = (pos.getExitPrice() - pos.getAvgPrice())*pos.getQuantity();
                            String change = String.format("%.2f", ((pos.getExitPrice() - pos.getAvgPrice()) / pos.getAvgPrice()) * 100) + "%";
                            pos.setPandL(pandl);
                            pos.setChange(change);
                        }
                        else {
                            double pandl = (pos.getLTP() - pos.getAvgPrice()) * pos.getQuantity();
                            String change = String.format("%.2f", ((pos.getLTP() - pos.getAvgPrice()) / pos.getAvgPrice()) * 100) + "%";
                            pos.setPandL(pandl);
                            pos.setChange(change);
                        }
                        map.put(instrumentId,pos);
                    }

                    return returnList;
                }
            }catch (Exception ex){
                System.out.println("Exception is positionServiceImple : " + ex);
                return null;
            }
            }
        }

        return null;
    }

    @Override
    public void executeStopLandTarget() {
      List<PositionOrders> positionsList = positionRepository.findByExitPriceZeroAndStopLossOrTargetSet();
      System.out.println("Positon List inside executeStopLossandTarget : " + positionsList.size());

        for (PositionOrders position : positionsList) {
            String insturmentDescription = position.getInstrumentDescription();

            try {
                double ltp = iiflService.getSingleQuoteData(insturmentDescription);
                double sl = position.getStopLoss();
                double trg= position.getTarget();

                if(ltp <= sl || ltp >= trg){
                    double pandl = (ltp - position.getPrice())*position.getQuantity();
                    double inichange = ((ltp - position.getPrice()) / position.getPrice()) * 100;
                    double change = Double.parseDouble(String.format("%.2f", inichange));
                    User user = position.getUser();
                    position.setExitPrice(ltp);
                    position.setPAndL(pandl);
                    position.setPercentChange(change);
                    position.setUpdatedAt(LocalDateTime.now());
                    user.setWallet(position.getUser().getWallet() + (ltp * position.getQuantity()));
                    position.setUser(user);
                    if(ltp <= sl){
                        position.setStopLossTrigger(true);
                        position.setOrderStatus(OrderStatus.STOPLOSS_HIT);
                    }
                    else{
                        position.setTargetTrigger(true);
                        position.setOrderStatus(OrderStatus.TARGET_HIT);
                    }
                    positionRepository.save(position);
                    userRepository.save(user);
                }
            }catch (Exception ex){

            }
        }

    }

    @Override
    public void executeSquareOff() {
        List<PositionOrders> positionsList = positionRepository.findByExitPriceZero();
        System.out.println("Positions List inside executeSquareOFF : " + positionsList.size());

        for (PositionOrders position : positionsList) {
            String insturmentDescription = position.getInstrumentDescription();
            try {
                double ltp = iiflService.getSingleQuoteData(insturmentDescription);
                double pandl = (ltp - position.getPrice())*position.getQuantity();
                double inichange = ((ltp - position.getPrice()) / position.getPrice()) * 100;
                double change = Double.parseDouble(String.format("%.2f", inichange));
                User user = position.getUser();
                position.setExitPrice(ltp);
                position.setPAndL(pandl);
                position.setPercentChange(change);
                position.setUpdatedAt(LocalDateTime.now());
                user.setWallet(position.getUser().getWallet() + (ltp * position.getQuantity()));
                position.setUser(user);
                position.setOrderStatus(OrderStatus.SQUAREOFF);

                userRepository.save(user);
                positionRepository.save(position);

            } catch (Exception ex) {
                System.out.println("Error in ExecuteLimitOrder " + ex.getMessage());
            }
        }
    }

    @Override
    public List<PositionOrders> getReport(LocalDate startDate, LocalDate endDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
            String username = userDetail.getUsername();
            User user = userRepository.findByEmail(username).get();

            List<PositionOrders> list = positionRepository.findByUserIdAndCreatedAtBetween(user,startDate,endDate);
            System.out.println("report list insde getReport : " + list.get(0));

            return list;
        }
        return null;
    }

    @Override
    public void editPosition(String positionId, Double stopLoss, Double target) {
        PositionOrders positionOrders = positionRepository.findById(positionId).get();
        positionOrders.setStopLoss(stopLoss);
        positionOrders.setTarget(target);
        positionRepository.save(positionOrders);
    }

    @Override
    public void exitPosition(String positionId, Double ltp) {
        try {
            PositionOrders position = positionRepository.findById(positionId).get();
            double pandl = (ltp - position.getPrice())*position.getQuantity();
            double inichange = ((ltp - position.getPrice()) / position.getPrice()) * 100;
            double change = Double.parseDouble(String.format("%.2f", inichange));
            User user = position.getUser();
            position.setExitPrice(ltp);
            position.setPAndL(pandl);
            position.setPercentChange(change);
            position.setUpdatedAt(LocalDateTime.now());
            user.setWallet(position.getUser().getWallet() + (ltp * position.getQuantity()));
            position.setUser(user);
            position.setOrderStatus(OrderStatus.EXIT);

            userRepository.save(user);
            positionRepository.save(position);
        }
        catch (Exception ex){
            System.out.println("Exception in exitposition method : "+ ex.toString());
        }

    }

}
