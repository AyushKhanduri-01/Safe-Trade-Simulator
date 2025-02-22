package com.trading.safetrade_simulator.controller;

import com.trading.safetrade_simulator.model.Instruments;
import com.trading.safetrade_simulator.model.QuotesData;
import com.trading.safetrade_simulator.model.User;
import com.trading.safetrade_simulator.service.CustomUserDetail;
import com.trading.safetrade_simulator.service.IIFLService;
import com.trading.safetrade_simulator.service.RedisOperationService;
import com.trading.safetrade_simulator.service.WishlistService;
import com.trading.safetrade_simulator.utility.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/marketdata")
public class MarketDataSearch {

    @Autowired
    private RedisOperationService redisOperationService;
    @Autowired
    private IIFLService iiflService;

    @Autowired
    private Helper helper;

    @Autowired
    private WishlistService wishlistService;

    @PostMapping("/search")
    public ResponseEntity<List<Instruments>> getSearchInstrument(@RequestParam String query){
      query = query.toUpperCase();
      List<Instruments> list = redisOperationService.findByPattern(query);
      Map<Integer, QuotesData> map = iiflService.getQuoteData(list);
      if(map == null){
          // Display only Instrument name with disable buy and sell and without ltp when instrument.quoteData == null
          return ResponseEntity.status(HttpStatus.OK).body(list);
      }
      list = helper.mapData(list,map);
      if(list.isEmpty()){
          return ResponseEntity.status(HttpStatus.NO_CONTENT).body(list);
      }
       return ResponseEntity.ok(list);
    }

    @PostMapping("/wishlist")
    public ResponseEntity<?> addToWishlist(@RequestParam String instrumentDescription){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            try {
                wishlistService.addToWishlist(instrumentDescription,authentication);
                return new ResponseEntity<>("Added Succesfully",HttpStatus.OK);
            }catch (Exception ex){
                return  new ResponseEntity<>("Failed to Add",HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return  new ResponseEntity<>("Authentication Failed",HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/wishlist")
    public ResponseEntity<?> removeFromWishlist(@RequestParam String instrumentDescription){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            try {
                wishlistService.removeFormWishlist(instrumentDescription,authentication);
                return new ResponseEntity<>("Removed Succesfully",HttpStatus.OK);
            }catch (Exception ex){
                return  new ResponseEntity<>("Failed to Remove",HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return  new ResponseEntity<>("Authentication Failed",HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/wishlist")
    public ResponseEntity<List<Instruments>> getWishlist(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!= null){
           try{
               return wishlistService.getWishlist(authentication);
           }catch (Exception ex){
               return new ResponseEntity<>(new ArrayList<>(),HttpStatus.BAD_REQUEST);
           }

        }
        else {
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
        }
    }

}
