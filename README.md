# aws-secret-manager
[Tests](https://github.com/anarwal/aws-secret-manager/workflows/.github/workflows/ci.yml/badge.svg)

Simple library to talk to AWS secrets manager using AWS SDK. It provides an interface to talk to AWS CRUD APIs. 
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
