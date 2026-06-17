package com.northernArc.firstbootapp;

import com.northernArc.firstbootapp.controller.TodoConsoleController;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*Automatically scan root packages for Component and Service*/
@SpringBootApplication
public class FirstbootappApplication implements CommandLineRunner {

	@Autowired
	TodoConsoleController todoConsoleController;

	public static void main(String[] args) {

		SpringApplication.run(FirstbootappApplication.class, args);
	}

	@Override
	public void run(String ... args) throws Exception{
		todoConsoleController.showMenu();
	}

}
