package com.trading.safetrade_simulator.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private static  final String SECRET_KEY ="kaklfjaskldjfaskfifjfjkafjakfryufcaxikjgamfxpjghaklfjfghjklertyuiocvbnmajskfklfj";
    private static final long ACCESS_TOKEN_EXPIRATION =  60*60*1000; //15 min
    private static final long REFRESH_TOKEN_EXPIRATION = 48*60*1000;

    //for generating token
    public String generateToken(String username,boolean isAccessToken){
        long expiration = isAccessToken ?ACCESS_TOKEN_EXPIRATION:REFRESH_TOKEN_EXPIRATION;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    //get name from token
    public String getUsernameFromToken(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();

    }

    public boolean validateToken(String token){
//        try{
//            Jwts.parser().setSigningKey(SECRET_KEY.getBytes())
//                    .build()
//                    .parseEncryptedClaims(token);
//            return true;
//        }
//        catch (JwtException ex){
//            return  false;
//        }

        try {
            // Parse the token and validate its signature and expiration
            Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                    .build()// Ensure SECRET_KEY is correctly defined
                    .parseClaimsJws(token);  // Use parseClaimsJws() for signed JWT
            return true; // Token is valid
        } catch (JwtException ex) {
            System.out.println("Invalid JWT token: " + ex.getMessage());
            return false;
        }

    }
}
