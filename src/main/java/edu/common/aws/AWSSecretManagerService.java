package edu.common.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import edu.common.exception.SMServiceException;
import edu.common.ISecretManagerService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AWSSecretManagerService implements ISecretManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSSecretManagerService.class);

    private AWSSecretsManager secretsManagerClient;

    public AWSSecretManagerService(String accessKey, String secretKey, String endpoint, String region) {
        AwsClientBuilder.EndpointConfiguration config = new AwsClientBuilder.EndpointConfiguration(endpoint, region);

        AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)));
        clientBuilder.setEndpointConfiguration(config);

        secretsManagerClient = clientBuilder.build();
    }

    /**
     * Creates the {@link CreateSecretRequest#name} in Secrets Manager
     *
     * @param name  {@link CreateSecretRequest#name}
     * @param secretString {@link CreateSecretRequest#secretString}
     */
    @Override
    public void createSecret(String name, String secretString) {
        StopWatch stopWatch = new StopWatch();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Storing secret value");
                stopWatch.start();
            }

            CreateSecretRequest csr = new CreateSecretRequest().withName(name).withSecretString(secretString);
            secretsManagerClient.createSecret(csr);

            if (LOGGER.isDebugEnabled()) {
                stopWatch.stop();
                LOGGER.info("Created secret in {} milliseconds");
            }

        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            throw new SMServiceException(e.getMessage(), e);
        }
    }

    /**
     * Updates the {@link UpdateSecretRequest#description} of {@link UpdateSecretRequest#secretId} in Secrets Manager
     *
     * @param secretId  {@link UpdateSecretRequest#secretId}
     * @param secretDescription {@link UpdateSecretRequest#description}
     */

    public void updateSecretDescription(String secretId, String secretDescription) {
        StopWatch stopWatch = new StopWatch();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Storing secret value");
                stopWatch.start();
            }

            UpdateSecretRequest usr = new UpdateSecretRequest().withSecretId(secretId).withDescription(secretDescription);
            secretsManagerClient.updateSecret(usr);

            if (LOGGER.isDebugEnabled()) {
                stopWatch.stop();
                LOGGER.info("Updated secret in {} nanoseconds",  stopWatch.getNanoTime());
            }

        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            throw new SMServiceException(e.getMessage(), e);
        }

    }

    /**
     * Updates the {@link UpdateSecretRequest#secretString} of {@link UpdateSecretRequest#secretId} in Secrets Manager
     *
     * @param secretId  {@link UpdateSecretRequest#secretId}
     * @param secretString {@link UpdateSecretRequest#secretString}
     */
    @Override
    public void updateSecretValue(String secretId, String secretString) {
        StopWatch stopWatch = new StopWatch();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Storing secret value");
                stopWatch.start();
            }

            UpdateSecretRequest usr = new UpdateSecretRequest().withSecretId(secretId).withSecretString(secretString);
            secretsManagerClient.updateSecret(usr);

            if (LOGGER.isDebugEnabled()) {
                stopWatch.stop();
                LOGGER.info("Updated secret in {} nanoseconds",  stopWatch.getNanoTime());
            }

        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
            throw new SMServiceException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves the {@link GetSecretValueRequest#secretId} in Secrets Manager
     *
     * @param secretId  {@link GetSecretValueRequest#secretId}
     */
    @Override
    public String getSecret(String secretId) {
        try {
            GetSecretValueRequest gsr = new GetSecretValueRequest().withSecretId(secretId);
            GetSecretValueResult value = secretsManagerClient.getSecretValue(gsr);

            if (value == null || StringUtils.isBlank(value.getSecretString())) {
                //couldn't get record
                throw new SMServiceException("Value came back Blank for Secret Named: " + secretId);
            }

            return value.getSecretString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new SMServiceException(e.getMessage(), e);
        }
    }

    /**
     * Deletes the {@link DeleteSecretRequest#secretId} in Secrets Manager
     *
     * @param secretId  {@link DeleteSecretRequest#secretId}
     */
    @Override
    public void deleteSecret(String secretId) {
        try {
            DeleteSecretRequest dsr = new DeleteSecretRequest().withSecretId(secretId);
            secretsManagerClient.deleteSecret(dsr);
        } catch (ResourceNotFoundException e) {
            LOGGER.debug("Resource not found for Secret ID " + secretId + ". Nothing to delete so quietly ignoring.");
        }
    }
}
