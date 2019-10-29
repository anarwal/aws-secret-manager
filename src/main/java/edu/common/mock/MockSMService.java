package edu.common.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.common.exception.SMServiceException;
import edu.common.ISecretManagerService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static net.logstash.logback.argument.StructuredArguments.keyValue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class MockSMService implements ISecretManagerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockSMService.class);

    private String targetDirectory;

    private static final String FILE_EXTENSION_TYPE = ".json";

    private ObjectMapper mapper;

    /**
     * Class for Reading and Writing a Mock File. Is Static so {@link ObjectMapper#readValue} works.
     */
    public static class MockSecretFile {
        private String secretContent;
        private String secretDescription;

        public MockSecretFile() {}

        public String getSecretContent() {
            return secretContent;
        }

        public void setSecretContent(String secretContent) {
            this.secretContent = secretContent;
        }

        public String getSecretDescription() {
            return secretDescription;
        }

        public void setSecretDescription(String secretDescription) {
            this.secretDescription = secretDescription;
        }
    }

    /**
     * Default Constructor using Target Director as file path.
     */
    public MockSMService() {
        this("./target/");
    }

    /**
     * Constructor using provided path as location for storing files.
     */
    public MockSMService(String targetDirectory) {
        Assert.isTrue(isNotBlank(targetDirectory), "Target Directory cannot be blank");
        if(!targetDirectory.endsWith("/")) {
            targetDirectory = targetDirectory + "/";
        }
        this.targetDirectory = targetDirectory;
        mapper = new ObjectMapper();
    }

    /**
     * Creates a new Secret.
     * @param secretId Secret ID to use
     * @param secretString  Specifies text data that you want to encrypt and store in this new version of the secret.
     */
    @Override
    public void createSecret(String secretId, String secretString) {
        StopWatch stopWatch = new StopWatch();
        Assert.isTrue(isNotBlank(secretId), "Secret Id cannot be blank");
        Assert.isTrue(isNotBlank(secretString), "Secret Value cannot be blank");

        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Storing file {} on local filesystem with location {}", keyValue("file", secretId), keyValue("location", targetDirectory));
                stopWatch.start();
            }
            File fileToCreate = new File(targetDirectory+secretId+FILE_EXTENSION_TYPE);
            Assert.isTrue(!fileToCreate.exists(), "Secret with ID \"" + secretId + "\" already exists" );

            MockSecretFile content = new MockSecretFile();
            content.setSecretContent(secretString);

            FileUtils.writeStringToFile(new File(targetDirectory+secretId+FILE_EXTENSION_TYPE), mapper.writeValueAsString(content), Charset.defaultCharset(), false);
            if (LOGGER.isDebugEnabled()) {
                stopWatch.stop();
                LOGGER.info("{} saved in file system at {} in {} milliseconds",
                        keyValue("fileName", secretId), keyValue("location", targetDirectory), stopWatch.getTotalTimeMillis());
            }
        } catch (IOException e) {
            throw new SMServiceException("Could not create Secret File: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Updates the Secret Description
     *
     * @param secretId Specifies the secret that you want to update or to which you want to add a new version.
     *                 You can specify either the Amazon Resource Name (ARN) or the friendly name of the secret.
     * @param secretDescription Specifies text data that you want to encrypt and store in this new version of the secret.
     */
    public void updateSecretDescription(String secretId, String secretDescription) {
        StopWatch stopWatch = new StopWatch();

        Assert.isTrue(isNotBlank(secretId), "Secret Id cannot be blank");
        Assert.notNull(secretDescription, "Secret Description cannot be null but can be blank");

        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Storing file {} on local filesystem with location {}", keyValue("file", secretId), keyValue("location", targetDirectory));
                stopWatch.start();
            }

            MockSecretFile contentToWrite;
            File existingSecret = new File(targetDirectory+secretId+FILE_EXTENSION_TYPE);
            Assert.isTrue(existingSecret.exists(), "There is currently no Secret with ID: " + secretId);

            contentToWrite = mapper.readValue(FileUtils.readFileToString(existingSecret, Charset.defaultCharset()), MockSecretFile.class);
            contentToWrite.setSecretDescription(secretDescription);

            FileUtils.writeStringToFile(existingSecret, mapper.writeValueAsString(contentToWrite), Charset.defaultCharset(), false);
            if (LOGGER.isDebugEnabled()) {
                stopWatch.stop();
                LOGGER.info("{} saved in file system at {} in {} milliseconds",
                        keyValue("fileName", secretId), keyValue("location", targetDirectory), stopWatch.getTotalTimeMillis());
            }
        } catch (IOException e) {
            throw new SMServiceException("Could not Update Secret File Descrpition: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Updates the Secret Value
     *
     * @param secretId Specifies the secret that you want to update or to which you want to add a new version.
     *                 You can specify either the Amazon Resource Name (ARN) or the friendly name of the secret.
     * @param secretString Specifies text data that you want to encrypt and store in this new version of the secret.
     */
    @Override
    public void updateSecretValue(String secretId, String secretString) {
        Assert.isTrue(isNotBlank(secretId), "Secret Id cannot be blank");
        Assert.isTrue(isNotBlank(secretString), "Secret Value cannot be blank");

        StopWatch stopWatch = new StopWatch();
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("Storing file {} on local filesystem with location {}", keyValue("file", secretId), keyValue("location", targetDirectory));
                stopWatch.start();
            }

            MockSecretFile contentToWrite;
            File existingSecret = new File(targetDirectory+secretId+FILE_EXTENSION_TYPE);
            Assert.isTrue(existingSecret.exists(), "There is currently no Secret with ID: " + secretId);

            contentToWrite = mapper.readValue(FileUtils.readFileToString(existingSecret, Charset.defaultCharset()), MockSecretFile.class);
            contentToWrite.setSecretContent(secretString);

            FileUtils.writeStringToFile(new File(targetDirectory+secretId+FILE_EXTENSION_TYPE), mapper.writeValueAsString(contentToWrite), Charset.defaultCharset(), false);
            if (LOGGER.isDebugEnabled()) {
                stopWatch.stop();
                LOGGER.info("{} saved in file system at {} in {} milliseconds",
                        keyValue("fileName", secretId), keyValue("location", targetDirectory), stopWatch.getTotalTimeMillis());
            }
        } catch (IOException e) {
            throw new SMServiceException("Could not Update Secret File: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Returns the Secret Value for the provided Secret ID.
     *
     * @param secretId Specifies the secret containing the version that you want to retrieve.
     *                 You can specify either the Amazon Resource Name (ARN) or the friendly name of the secret.
     * @return Secret Content
     */
    @Override
    public String getSecret(String secretId) {
        Assert.isTrue(isNotBlank(secretId), "Secret Id cannot be blank");

        try {
            return mapper.readValue(FileUtils.readFileToString(new File(targetDirectory+secretId+ FILE_EXTENSION_TYPE), Charset.defaultCharset()), MockSecretFile.class).getSecretContent();
        }catch (IOException e) {
            throw new IllegalArgumentException("There is no Secret with ID: " + secretId);
        }
    }

    /**
     * If Secret File and/or Secret Description File exists, they are deleted
     * @param secretId Specifies the secret containing the version that you want to retrieve.
     */
    @Override
    public void deleteSecret(String secretId) {
        Assert.isTrue(isNotBlank(secretId), "Secret Id cannot be blank");

        FileUtils.deleteQuietly(new File(targetDirectory +secretId+ FILE_EXTENSION_TYPE));
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }
}
