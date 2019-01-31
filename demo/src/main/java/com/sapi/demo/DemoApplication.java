package com.sapi.demo;

import com.github.xiaour.sapi.annotation.Sapi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Sapi(controllers = {"com.sapi.demo.controller"})
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
