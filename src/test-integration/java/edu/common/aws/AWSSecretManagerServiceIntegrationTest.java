package edu.common.aws;

import java.time.LocalDateTime;

import edu.common.TestConfig;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AWSSecretManagerServiceIntegrationTest {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    AWSSecretManagerService awsSmService;

    private String secretString = "Password123";
    private String secretName;

    @Before
    public void setUp() {
        secretName = randomAlphanumeric(10) + LocalDateTime.now().hashCode();
    }

    @After
    public void tearDown() {
        awsSmService.deleteSecret(secretName);
    }


    @Test
    public void createTest(){
        awsSmService.createSecret(secretName, secretString);
        Assert.assertEquals(awsSmService.getSecret(secretName), secretString);
    }

    @Test
    public void getTest() {
        awsSmService.createSecret(secretName, secretString);
        Assert.assertEquals(awsSmService.getSecret(secretName), secretString);
    }

    @Test
    public void updateTest() {
        awsSmService.createSecret(secretName, secretString);
        awsSmService.updateSecretDescription(secretName, "this is the description");
        Assert.assertEquals(awsSmService.getSecret(secretName), secretString);
    }

    //TODO: Fix the test to verify exception
    @Ignore
    @Test
    public void deleteTest() {
        awsSmService.createSecret(secretName, secretString);
        awsSmService.deleteSecret(secretName);
        awsSmService.getSecret(secretName);
    }
}
