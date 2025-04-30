package com.trading.safetrade_simulator.config;

import com.trading.safetrade_simulator.service.CustomUserDetail;
import com.trading.safetrade_simulator.service.JwtService;
import com.trading.safetrade_simulator.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer")){

            String token = authHeader.substring(7);
            System.out.println("Token : " + token);

            if(jwtService.validateToken(token)){
               String userNameFromToken = jwtService.getUsernameFromToken(token);
               System.out.println("userName   " + userNameFromToken );
//                old
                UserDetails userDetails = userService.loadUserByUsername(userNameFromToken);



                if(SecurityContextHolder.getContext().getAuthentication()==null){
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            else {
                System.out.println("not validate " );
            }
            System.out.println("end");

        }
        else{
            System.out.println("unauthorised auth");
        }
        filterChain.doFilter(request,response);
    }
}
