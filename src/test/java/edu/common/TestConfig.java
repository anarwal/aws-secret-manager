package edu.common;

import edu.common.aws.AWSSecretManagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ConfigurationProperties
@PropertySources(@PropertySource("classpath:test.properties"))
public class TestConfig {

    @Value("${SM_AWS_ACCESS_KEY}")
    private String access_key;

    @Value("${SM_AWS_SECRET_KEY}")
    private String secret_key;

    @Value("${aws.sm.region}")
    private String region;

    @Value("${aws.sm.endpoint}")
    private String endpoint;

    @Bean
    public AWSSecretManagerService smService(){
        return new AWSSecretManagerService(access_key, secret_key, endpoint, region);
    }


    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setSecretKey(String secret_key) {
        this.secret_key = secret_key;
    }

    public void setAccessKey(String access_key) {
        this.access_key = access_key;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
