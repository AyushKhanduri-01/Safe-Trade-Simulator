package com.trading.safetrade_simulator.service.impl;

import com.trading.safetrade_simulator.Repositories.UserRepository;
import com.trading.safetrade_simulator.model.Instruments;
import com.trading.safetrade_simulator.model.User;
import com.trading.safetrade_simulator.service.CustomUserDetail;
import com.trading.safetrade_simulator.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public void addToWishlist(String instrumentDescription, Authentication authentication) {


        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        String username =  userDetail.getUsername();
        User user = userRepository.findByEmail(username).get();
        if(user!=null){
            List<String> list =user.getWishlist();
            if(list == null){
                list = new ArrayList<>();
            }
            if(list.contains(instrumentDescription)) return;
            list.add(instrumentDescription);
            user.setWishlist(list);
            userRepository.save(user);
            System.out.println(user.getWishlist().size() + "   size of wishlist");
        }

    }

    @Override
    public void removeFormWishlist(String instrumentDescription, Authentication authentication) {
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        String username =  userDetail.getUsername();
        User user = userRepository.findByEmail(username).get();
        if(user!=null){
            List<String> list =user.getWishlist();
            if(list == null && !list.contains(instrumentDescription)){
                return;
            }
            list.remove(instrumentDescription);
            user.setWishlist(list);
            userRepository.save(user);
            System.out.println(user.getWishlist().size() + "   size of wishlist");
        }
    }

    @Override
    public ResponseEntity<List<Instruments>> getWishlist(Authentication authentication) {
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        String username =  userDetail.getUsername();
        User user = userRepository.findByEmail(username).get();
        if(user != null && user.getWishlist() != null && !user.getWishlist().isEmpty()){
            List<String> instrumentList = user.getWishlist();


        }
    }
}
