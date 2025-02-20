package com.trading.safetrade_simulator.DTO;

import com.trading.safetrade_simulator.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private User user;
}
