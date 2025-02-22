package com.trading.safetrade_simulator.service;

import com.trading.safetrade_simulator.model.Instruments;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface WishlistService {
    void addToWishlist(String instrumentDescription, Authentication authentication);

    void removeFormWishlist(String instrumentDescription, Authentication authentication);

    ResponseEntity<List<Instruments>> getWishlist(Authentication authentication);
}
