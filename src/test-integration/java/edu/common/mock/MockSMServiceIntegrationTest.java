package edu.common.mock;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.assertEquals;

public class MockSMServiceIntegrationTest {
    private String secretId;

    private MockSMService mockSMService;

    private List<String> directoriesToCleanup = new ArrayList<>();

    private static final String FILE_EXTENSION = ".json";

    private static final String DEFAULT_PATH = "./target/";

    @Before
    public void setup() {
        secretId = randomAlphabetic(15);
        mockSMService = new MockSMService();
    }

    @After
    public void teardown() {
        mockSMService.deleteSecret(secretId);

        for(String directory : directoriesToCleanup) {
            FileUtils.deleteQuietly(new File(directory));
        }
    }

    @Test
    public void defaultConstructor() {
        mockSMService.createSecret(secretId, "Content not read");
        File fileCreatedTest = new File(DEFAULT_PATH+secretId+FILE_EXTENSION);
        Assert.assertTrue(fileCreatedTest.exists());
    }

    @Test
    public void stringConstructor() {
        String expectedPath = "./target/test-location/";
        directoriesToCleanup.add(expectedPath);

        mockSMService = new MockSMService(expectedPath);
        mockSMService.createSecret(secretId, "Content not read");
        File fileCreatedTest = new File(expectedPath+secretId+FILE_EXTENSION);
        Assert.assertTrue(fileCreatedTest.exists());
    }

    @Test
    public void stringConstructorWithMissingTailSlash() {
        String expectedPath = "./target/test-location";

        mockSMService = new MockSMService(expectedPath);
        assertEquals(expectedPath + "/", mockSMService.getTargetDirectory());
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringConstructorNullPath() {
        mockSMService = new MockSMService(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringConstructorEmptyPath() {
        mockSMService = new MockSMService("");
    }

    @Test
    public void createSecret() throws IOException {
        String expectedContent = randomAlphanumeric(15);

        mockSMService.createSecret(secretId, expectedContent);
        File fileCreatedTest = new File(DEFAULT_PATH+secretId+FILE_EXTENSION);
        Assert.assertTrue(fileCreatedTest.exists());

        String readContent = FileUtils.readFileToString(fileCreatedTest, Charset.defaultCharset());
        ObjectMapper mapper = new ObjectMapper();
        MockSMService.MockSecretFile values = mapper.readValue(readContent, MockSMService.MockSecretFile.class);
        assertEquals(expectedContent, values.getSecretContent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSecretNullId() throws IOException {
        String expectedContent = randomAlphanumeric(15);

        mockSMService.createSecret(null, expectedContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSecretEmptyId() throws IOException {
        String expectedContent = randomAlphanumeric(15);

        mockSMService.createSecret("", expectedContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSecretNullValue() throws IOException {
        mockSMService.createSecret(secretId, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSecretEmptyValue() throws IOException {
        mockSMService.createSecret(secretId, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDuplicateSecret() throws IOException {
        String expectedContent = randomAlphanumeric(15);

        mockSMService = new MockSMService();
        mockSMService.createSecret(secretId, expectedContent);
        mockSMService.createSecret(secretId, expectedContent);
    }

    @Test
    public void getSecret() {
        String expectedContent = randomAlphanumeric(15);

        mockSMService.createSecret(secretId, expectedContent);

        String readContent = mockSMService.getSecret(secretId);
        assertEquals(expectedContent, readContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSecretDoesNotExist() {
        mockSMService.getSecret(secretId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSecretIdNull() {
        mockSMService.getSecret(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getSecretIdEmpty() {
        mockSMService.getSecret("");
    }

    @Test
    public void updateSecret() {
        String expectedContent = randomAlphanumeric(15);

        mockSMService.createSecret(secretId, randomAlphanumeric(15));
        mockSMService.updateSecretValue(secretId, expectedContent);

        String readContent = mockSMService.getSecret(secretId);

        assertEquals(expectedContent, readContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateSecretDoesNotExist() {
        String expectedContent = randomAlphanumeric(15);

        mockSMService.updateSecretValue(secretId, expectedContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateSecretIdNull() {
        String expectedContent = randomAlphanumeric(15);

        mockSMService.updateSecretValue(null, expectedContent);
    }


    @Test(expected = IllegalArgumentException.class)
    public void updateSecretIdEmpty() {
        String expectedContent = randomAlphanumeric(15);

        mockSMService.updateSecretValue("", expectedContent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateSecretValueNull() {
        mockSMService.createSecret(secretId, randomAlphanumeric(15));
        String expectedContent = randomAlphanumeric(15);

        mockSMService.updateSecretValue(secretId, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateSecretValueBlank() {
        mockSMService.createSecret(secretId, randomAlphanumeric(15));
        String expectedContent = randomAlphanumeric(15);

        mockSMService.updateSecretValue(secretId, "");
    }

    @Test
    public void deleteSecret() {
        mockSMService.createSecret(secretId, "Content not read");
        mockSMService.deleteSecret(secretId);

        File fileDeletedTest = new File(DEFAULT_PATH+secretId+FILE_EXTENSION);
        Assert.assertFalse(fileDeletedTest.exists());
    }

    @Test
    public void deleteSecretDoesNotExist() {
        // No Exception should be thrown even though secret does not exist
        mockSMService.deleteSecret(secretId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteSecretIdNull() {
        // No Exception should be thrown even though secret does not exist
        mockSMService.deleteSecret(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteSecretIdBlank() {
        // No Exception should be thrown even though secret does not exist
        mockSMService.deleteSecret("");
    }

    @Test
    public void updateDescription() throws Exception {
        String expectedDescription = randomAlphabetic(15);
        String expectedContent = randomAlphabetic(15);
        mockSMService.createSecret(secretId, expectedContent);
        mockSMService.updateSecretDescription(secretId, expectedDescription);

        File fileUpdated = new File(DEFAULT_PATH+secretId+FILE_EXTENSION);
        ObjectMapper mapper = new ObjectMapper();

        MockSMService.MockSecretFile values = mapper.readValue(FileUtils.readFileToString(fileUpdated, Charset.defaultCharset()), MockSMService.MockSecretFile.class);
        assertEquals(expectedContent, values.getSecretContent());
        assertEquals(expectedDescription, values.getSecretDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateDescriptionSecretNotFound() {
        mockSMService.updateSecretDescription(secretId, randomAlphabetic(15));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateDescriptionSecretIdNull() {
        mockSMService.updateSecretDescription(null, randomAlphabetic(15));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateDescriptionSecretIdEmpty() {
        mockSMService.updateSecretDescription("", randomAlphabetic(15));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateDescriptionSecretDescriptionNull() {
        mockSMService.createSecret(secretId, randomAlphanumeric(15));
        mockSMService.updateSecretDescription(secretId, null);
    }
}
