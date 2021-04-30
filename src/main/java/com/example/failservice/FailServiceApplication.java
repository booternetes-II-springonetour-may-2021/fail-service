package com.example.failservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/")
@SpringBootApplication
public class FailServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FailServiceApplication.class, args);
	}

	private final Object monitor = new Object();
	private int counter = 0;

	//	@PostMapping
	ResponseEntity<?> timeout(@RequestBody Map<String, String> payload)
		throws Exception {

		System.out.println(
			"timeout payload: " + payload + ", time: " + Instant.now());

		Thread.sleep(8_000);
		return ResponseEntity.ok().build();
	}

	@PostMapping
	ResponseEntity<?> ok(@RequestBody Map<String, String> payload) {
		System.out.println(
			"retry payload: " + payload + " , counter: " + this.counter + ", time: " + Instant.now());
		return ResponseEntity.ok().build();
	}

	@PostMapping
	ResponseEntity<?> acceptRetries(@RequestBody Map<String, String> payload) {
		System.out.println(
			"retry payload: " + payload + " , counter: " + this.counter + ", time: " + Instant.now());
		synchronized (this.monitor) {
			this.counter += 1;
			if (this.counter < 5) {
				System.out.println("ERROR!");
				return ResponseEntity.badRequest().build();
			}
		}
		System.out.println("OK!");
		return ResponseEntity.ok().build();
	}
}
