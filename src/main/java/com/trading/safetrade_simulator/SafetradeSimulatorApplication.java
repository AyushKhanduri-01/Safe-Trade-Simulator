package com.trading.safetrade_simulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SafetradeSimulatorApplication {

	@Autowired
	public static void main(String[] args) {
		SpringApplication.run(SafetradeSimulatorApplication.class, args);
	}
}
