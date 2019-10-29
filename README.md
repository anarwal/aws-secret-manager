# aws-secret-manager
![Tests](https://github.com/anarwal/aws-secret-manager/workflows/.github/workflows/ci.yml/badge.svg)

Simple library to talk to AWS secrets manager using AWS SDK. It provides an interface to communicate with Secrets Manger using AWS CRUD APIs.
You can choose either to use:
 1. AWS Secrets Manager (Production usage)
 2. File System (Mock for local testing)

To perform a build and execute all unit tests:
```
mvn clean install
```

To execute all component tests:
```
mvn -P test-integration test
```
#### Functions provided:

- createSecret(String name, String secretString): Creates a new secret
- updateSecretValue(String secretId, String secretString): Modifies the content of a secret
- getSecret(String secretId): Retrieves the contents of the encrypted fields SecretString from the specified version of a secret
- deleteSecret(String secretId): Deletes an entire secret and all of its versions

You have two different classes available here:
- AWSSecretManagerService: Use this for application, it stores secrets on AWS Secret Manager
- MockSMService: Use this for testing purpose, it uses your system to store secrets

----------
Instantiate bean by including following after adding dependency to pom:
```
    @Value("${aws.sm.access_key}")
    private String access_key;

    @Value("${aws.sm.secret_key}")
    private String secret_key;

    @Value("${aws.sm.region}")
    private String region;

    @Value("${aws.sm.endpoint}")
    private String endpoint;

    @Bean
    public AWSSecretManagerService smService(){
        return new AWSSecretManagerService(access_key, secret_key, endpoint, region);
    }
```
