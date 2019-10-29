package edu.common;


public interface ISecretManagerService {

    /**
     * Creates a new secret
     * @param name Specifies the friendly name of the new secret.
     * @param secretString  Specifies text data that you want to encrypt and store in this new version of the secret.
     */
    void createSecret(String name, String secretString);

    /**
     * Modifies the content of a secret.
     * @param secretId Specifies the secret that you want to update or to which you want to add a new version.
     *                 You can specify either the Amazon Resource Name (ARN) or the friendly name of the secret.
     * @param secretString Specifies text data that you want to encrypt and store in this new version of the secret.
     */
    void updateSecretValue(String secretId, String secretString);

    /**
     * Retrieves the contents of the encrypted fields SecretString from the specified version of a secret.
     * @param secretId Specifies the secret containing the version that you want to retrieve.
     *                 You can specify either the Amazon Resource Name (ARN) or the friendly name of the secret.
     * @return Returns following values of secret: ARN, CreatedDate, Name, SecretBinary, SecretString, VersionId, VersionStages
     */
    String getSecret(String secretId);

    /**
     * Deletes an entire secret and all of its versions
     * @param secretId Specifies the secret containing the version that you want to retrieve.
     *                 You can specify either the Amazon Resource Name (ARN) or the friendly name of the secret.
     */
    void deleteSecret(String secretId);

}

