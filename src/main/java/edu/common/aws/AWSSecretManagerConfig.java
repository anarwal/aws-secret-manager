package edu.common.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Service;

@Service
@Configuration
@PropertySources(@PropertySource("classpath:application.properties"))
public class AWSSecretManagerConfig {
    @Value("${aws.sm.access_key}")
    private String accessKey;

    @Value("${aws.sm.secret_key}")
    private String secretKey;

    @Value("${aws.sm.region}")
    private String region;

    @Value("${aws.sm.endpoint}")
    private String endpoint;

    @Bean
    public AWSSecretsManager secretsManagerClient() {
        AwsClientBuilder.EndpointConfiguration config = new AwsClientBuilder.EndpointConfiguration(endpoint, region);

        AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)));
        clientBuilder.setEndpointConfiguration(config);

        AWSSecretsManager secretsManagerClient = clientBuilder.build();

        return secretsManagerClient;
    }
}
