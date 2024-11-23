package devmagic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // @SpringBootApplication đã tự động bao gồm @ComponentScan
public class DevMagicApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevMagicApplication.class, args);
    }
}
