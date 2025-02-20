package com.trading.safetrade_simulator.model;

import com.trading.safetrade_simulator.enums.Roles;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Data
@Document("users")
public class User  {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private String password;
    private double walet = 1000000.00;


    private List<String> roles;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = new ArrayList<>();
        this.roles.add("USER");
    }
}
