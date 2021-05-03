package com.example.failservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class FailServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FailServiceApplication.class, args);
    }
}

@RestController
@RequestMapping("/")
class FailController {
    private final Object monitor = new Object();
    private int counter = 0;

    @PostMapping("/ok")
    ResponseEntity<?> ok(@RequestBody Map<String, String> payload) {
        System.out.println("> Payload: " + payload
                + ", time: " + Instant.now());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/timeout")
    ResponseEntity<?> timeout(@RequestBody Map<String, String> payload)
            throws Exception {

        System.out.println(">>> Timeout payload: " + payload
                + ", time: " + Instant.now());

        Thread.sleep(8_000);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/retry")
    ResponseEntity<?> acceptRetries(@RequestBody Map<String, String> payload) {
        System.out.println(">> Retry payload: " + payload
                + " , counter: " + this.counter
                + ", time: " + Instant.now());

        synchronized (monitor) {
            counter++;
            if (counter < 5) {
                System.out.println("<< Failure " + counter + " >>");
                return ResponseEntity.badRequest().build();
            }
        }
        System.out.println("<< Retry successful! >>");
        return ResponseEntity.ok().build();
    }
}
