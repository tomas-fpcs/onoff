package se.fpcs.elpris.onoff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OnOffApplication {

  public static void main(String[] args) {
    SpringApplication.run(OnOffApplication.class, args);
  }

}
