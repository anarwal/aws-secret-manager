package edu.common.aws;

import com.amazonaws.services.secretsmanager.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AWSSecretManagerConfig.class)
public class AWSSecretManagerConfigIntegrationTest {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    AWSSecretManagerConfig awsSmConfig;

    private String name= randomAlphanumeric(10);
    private String secretString= "Password123";

    @Test
    public void createTest(){
        createTestWithRequiredParams(name, secretString);
        deleteSecretTest(name);
    }

    @Test
    public void getSecretTest(){
        createTestWithRequiredParams(name, secretString);
        GetSecretValueRequest gsr = new GetSecretValueRequest().withSecretId(name);
        GetSecretValueResult getResult = awsSmConfig.secretsManagerClient().getSecretValue(gsr);
        assertEquals(getResult.getName(), name);
        assertEquals(getResult.getSecretString(), secretString);
        deleteSecretTest(name);
    }

    @Test
    public void updateSecretTest(){
        createTestWithRequiredParams(name, secretString);
        UpdateSecretRequest usr = new UpdateSecretRequest().withSecretId(name).withDescription("Add this description");
        UpdateSecretResult updateResult = awsSmConfig.secretsManagerClient().updateSecret(usr);
        assertEquals(updateResult.getName(), name);
        deleteSecretTest(name);
    }

    @Test
    public void deleteSecret(){
        createTestWithRequiredParams(name, secretString);
        deleteSecretTest(name);
    }

    public void deleteSecretTest(String name){
        DeleteSecretRequest dsr = new DeleteSecretRequest().withSecretId(name);
        DeleteSecretResult deleteResult = awsSmConfig.secretsManagerClient().deleteSecret(dsr);
        assertEquals(deleteResult.getName(), name);
    }

    @Test
    public void generateRandomPassword(){
        GetRandomPasswordRequest grpr = new GetRandomPasswordRequest().withPasswordLength(Long.valueOf(10));
        GetRandomPasswordResult getRandomPasswordResult = awsSmConfig.secretsManagerClient().getRandomPassword(grpr);
        assertEquals(getRandomPasswordResult.getRandomPassword().length(), 10);
    }

    @Test
    public void listAllSecrets(){
        ListSecretsRequest lsr = new ListSecretsRequest();
        ListSecretsResult listSecretsResult = awsSmConfig.secretsManagerClient().listSecrets(lsr);
    }

    private void createTestWithRequiredParams(String secretName, String secretValue){
        CreateSecretRequest csr = new CreateSecretRequest().withName(secretName).withSecretString(secretValue).withDescription("This is a sample");
        CreateSecretResult createResult= awsSmConfig.secretsManagerClient().createSecret(csr);
        assertEquals(createResult.getName(), name);
    }

}
