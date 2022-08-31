package com.doubledice.databuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
//@EnableConfigurationProperties
public class DataBuilderApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(DataBuilderApplication.class);
        app.setLazyInitialization(true);
                app.run(args);
        openHomePage();
    }
    private static void openHomePage() throws IOException {
        Runtime rt = Runtime.getRuntime();
        rt.exec("rundll32 url.dll,FileProtocolHandler " + "http://localhost");
    }

}
