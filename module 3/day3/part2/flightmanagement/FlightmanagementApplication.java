package com.northernArc.flightmanagement;

import com.northernArc.flightmanagement.controller.FlightConsoleController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlightmanagementApplication implements CommandLineRunner {

	@Autowired
	FlightConsoleController flightConsoleController;

	public static void main(String[] args) {
		SpringApplication.run(FlightmanagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		flightConsoleController.showMenu();
	}
}
