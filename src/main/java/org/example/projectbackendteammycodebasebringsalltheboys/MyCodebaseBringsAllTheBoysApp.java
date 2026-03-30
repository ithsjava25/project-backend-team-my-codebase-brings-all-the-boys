package org.example.projectbackendteammycodebasebringsalltheboys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MyCodebaseBringsAllTheBoysApp {

  public static void main(String[] args) {
    SpringApplication.run(MyCodebaseBringsAllTheBoysApp.class, args);
  }
}
