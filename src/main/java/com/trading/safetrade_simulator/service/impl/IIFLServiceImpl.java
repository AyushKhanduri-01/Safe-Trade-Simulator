package com.trading.safetrade_simulator.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.safetrade_simulator.model.Instruments;
import com.trading.safetrade_simulator.model.QuotesData;
import com.trading.safetrade_simulator.service.IIFLService;
import com.trading.safetrade_simulator.service.JsonParserService;
import com.trading.safetrade_simulator.service.RedisOperationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedTransferQueue;


@Service
public class IIFLServiceImpl implements IIFLService {

    @Autowired
    private RedisOperationService redisOperation;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JsonParserService jsonParserService;

    @Value("${IIFL.secretKey}")
    private String secretKey;

    @Value("${IIFL.appKey}")
    private String appKey;

    @Value("${IIFL.loginUrl}")
    private String loginurl;

    @Value("${IIFL.masterUrl}")
    private String masterUrl;

    @Value("${IIFL.quoteUrl}")
    private String quoteUrl;

    @Override
    public boolean isSessionTokenPresent() {
        return redisOperation.isSessionTokenPresent("IIFLSession");
    }

    @Override
    public boolean isInstrumentPresent() {
        return redisOperation.isInstrumentPresent("IsInsturmentPresent");
    }

    @Override
    public void addSessionToken() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        String requestBody = String.format("{\"secretKey\":\"%s\", \"appKey\":\"%s\"}", secretKey, appKey);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(loginurl, HttpMethod.POST, entity, String.class);
        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        JsonNode resultNode = jsonNode.get("result");
        String token = resultNode.get("token").asText();
        redisOperation.saveSessionToken("IIFLSession", token, redisOperation.expirationTime());
    }

    @Override
    public String getSessionToken() {
        return null;
    }

    @Override
    public Map<Integer, QuotesData> getQuoteData(List<Instruments> list) {
        Object object = redisOperation.findByKey("IIFLSession", String.class);
        if (object != null) {
            String token = object.toString();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", token);
            StringBuilder instrumentsArray = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                int exchangeInstrumentId = Integer.parseInt(list.get(i).getExchangeInstrumentID());
                instrumentsArray.append(String.format(
                        " {\n" +
                                " \"exchangeSegment\": 2,\n" +
                                " \"exchangeInstrumentID\": %d\n" +
                                " }",
                        exchangeInstrumentId));
                if (i < list.size() - 1) {
                    instrumentsArray.append(",\n");
                }
            }
            String requestBody = String.format(
                    "{\n" +
                            " \"instruments\": [\n" +
                            "%s\n" +
                            " ],\n" +
                            " \"xtsMessageCode\": 1501,\n" +
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
                    Map<Integer, QuotesData> quotesDataList = jsonParserService.getPareseQuoteData(responseBody);
                    return quotesDataList;
                }
            }catch (Exception ex){

                System.out.println("token not match issue in quote data");
                return null;
            }

        }
        return null;
    }

    @Override
    public void addInstruments() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        String requestBody = String.format("{\"exchangeSegmentList\": [%s]}", "\"NSEFO\"");
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(masterUrl, HttpMethod.POST, entity, String.class);
        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String resultNode = jsonNode.get("result").asText();
        List<Instruments> instruments =  jsonParserService.getParsedInstrumentsFromAPI(resultNode);
        redisOperation.saveInstrumentToken("IsInsturmentPresent", "yes", redisOperation.expirationTime());
        int i=0;
        for(Instruments inst: instruments){
            redisOperation.saveInstrumentInCache(inst,inst.getDescription(),redisOperation.expirationTime());
            i++;
        }
        System.out.println(i+" no. of instrumetns added");
    }

    @Override
    public Double getSingleQuoteData(String instrumentDescription) {

        Instruments instrument = redisOperation.findByKey(instrumentDescription, Instruments.class);
        Object object = redisOperation.findByKey("IIFLSession", String.class);
        String token = object.toString();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", token);

        int exchangeInstrumentId = Integer.parseInt(instrument.getExchangeInstrumentID());
        String requestBody = String.format(
                "{\n" +
                        " \"instruments\": [\n" +
                        "   {\n" +
                        "     \"exchangeSegment\": 2,\n" +
                        "     \"exchangeInstrumentID\": %d\n" +
                        "   }\n" +
                        " ],\n" +
                        " \"xtsMessageCode\": 1501,\n" +
                        " \"publishFormat\": \"JSON\"\n" +
                        "}",
                exchangeInstrumentId);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(quoteUrl,
                HttpMethod.POST, entity, String.class);
        String responseBody = response.getBody();

        QuotesData quotesData = jsonParserService.getPareseQuoteData(responseBody).get(exchangeInstrumentId);

        return quotesData != null ? quotesData.getLastTradedPrice() : null;
    }
}
