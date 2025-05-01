package com.trading.safetrade_simulator.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.safetrade_simulator.DTO.PositionsDTO;
import com.trading.safetrade_simulator.Repositories.PositionRepository;
import com.trading.safetrade_simulator.Repositories.UserRepository;
import com.trading.safetrade_simulator.model.Instruments;
import com.trading.safetrade_simulator.model.PositionOrders;
import com.trading.safetrade_simulator.model.QuotesData;
import com.trading.safetrade_simulator.model.User;
import com.trading.safetrade_simulator.service.CustomUserDetail;
import com.trading.safetrade_simulator.service.PositionsService;
import com.trading.safetrade_simulator.service.RedisOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PositionsServiceImpl implements PositionsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private RedisOperationService redisOperationService;

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

            List<PositionOrders> positions = positionRepository.findByUser(user);
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

//                if(positions.get(i).getExitPrice() > 0){
//                    continue;
//                }
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
                        double pandl = (pos.getLTP() - pos.getAvgPrice())*pos.getQuantity();
                        String change = String.format("%.2f", ((pos.getLTP() - pos.getAvgPrice()) / pos.getAvgPrice()) * 100) + "%";
                        pos.setPandL(pandl);
                        pos.setChange(change);
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


//    public Map<Integer, QuotesData> getQuoteData(List<Instruments> list) {
//        Object object = redisOperation.findByKey("IIFLSession", String.class);
//        if (object != null) {
//            String token = object.toString();
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Content-Type", "application/json");
//            headers.set("Authorization", token);
//            StringBuilder instrumentsArray = new StringBuilder();
//            for (int i = 0; i < list.size(); i++) {
//                int exchangeInstrumentId = Integer.parseInt(list.get(i).getExchangeInstrumentID());
//                instrumentsArray.append(String.format(
//                        " {\n" +
//                                " \"exchangeSegment\": 2,\n" +
//                                " \"exchangeInstrumentID\": %d\n" +
//                                " }",
//                        exchangeInstrumentId));
//                if (i < list.size() - 1) {
//                    instrumentsArray.append(",\n");
//                }
//            }
//            String requestBody = String.format(
//                    "{\n" +
//                            " \"instruments\": [\n" +
//                            "%s\n" +
//                            " ],\n" +
//                            " \"xtsMessageCode\": 1501,\n" +
//                            " \"publishFormat\": \"JSON\"\n" +
//                            "}",
//                    instrumentsArray.toString());
//
//            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
//            try{
//                ResponseEntity<String> response = restTemplate.exchange(quoteUrl,
//                        HttpMethod.POST, entity, String.class);
//                if(response.getStatusCode() == HttpStatus.BAD_REQUEST){
//                    System.out.println("Bad request " + response.getBody());
//                }
//                else{
//                    String responseBody = response.getBody();
//                    Map<Integer, QuotesData> quotesDataList = jsonParserService.getPareseQuoteData(responseBody);
//                    return quotesDataList;
//                }
//            }catch (Exception ex){
//
//                System.out.println("token not match issue in quote data");
//                return null;
//            }
//
//        }
//        return null;
//    }



}
