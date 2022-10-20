package com.tiktok;


import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.Configuration;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TikTokFinalProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TikTokFinalProjectApplication.class, args);

    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ApiClient defaultClient() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        return defaultClient;
    }

}
