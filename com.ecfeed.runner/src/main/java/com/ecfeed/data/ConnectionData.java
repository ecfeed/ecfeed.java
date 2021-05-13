package com.ecfeed.data;

import com.ecfeed.Config;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;

public class ConnectionData {

    private HttpClient httpClient;
    private String httpAddress;
    private KeyStore keyStoreInstance;
    private String keyStorePassword;
    private Path keyStorePath;

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public String getHttpAddress() {
        return httpAddress;
    }

    public Path getKeyStorePath() {
        return keyStorePath;
    }

    public ConnectionData(String httpAddress, Path keyStorePath, String keyStorePassword) {
        this.httpAddress = httpAddress;
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;

        this.keyStoreInstance = getKeyStoreInstance();
        this.httpClient = setupGetHTTPClient();
    }

    public static ConnectionData create(String httpAddress, Path keyStorePath, String keyStorePassword) {

        return new ConnectionData(httpAddress, keyStorePath, keyStorePassword);
    }

    private KeyStore getKeyStoreInstance() {

        try (InputStream keyStoreInputStream = Files.newInputStream(this.keyStorePath)) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
            return keyStore;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("The algorithm for checking the keystore integrity could not be found.", e);
        } catch (CertificateException e) {
            throw new IllegalArgumentException("At least one of the certificates included in the keystore could not be loaded.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("The keystore password is incorrect. Store path: " + this.keyStorePath, e);
        } catch (KeyStoreException e) {
            throw new IllegalArgumentException("The keystore could not be accessed.", e);
        }
    }

    private HttpClient setupGetHTTPClient() {

        return HttpClients.custom().setSSLContext(getSSLContext()).build();
    }

    private SSLContext getSSLContext() {

        try {
            return getKeyMaterial(getTrustMaterial(SSLContexts.custom())).build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalArgumentException("KeyStore certificates could not be loaded.", e);
        }
    }

    private SSLContextBuilder getKeyMaterial(SSLContextBuilder context) {

        try {
            if (!this.keyStoreInstance.containsAlias(Config.Key.certClient)) {
                throw new IllegalArgumentException("The client certificate could not be found: " + keyStorePath.toAbsolutePath());
            }

            PrivateKeyStrategy strategy = (aliases, socket) -> Config.Key.certClient;
            return context.loadKeyMaterial(this.keyStoreInstance, keyStorePassword.toCharArray(), strategy);
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            throw new IllegalArgumentException("The client certificate could not be accessed.", e);
        }
    }

    private SSLContextBuilder getTrustMaterial(SSLContextBuilder context) {

        try {
            if (!this.keyStoreInstance.containsAlias(Config.Key.certServer)) {
                throw new IllegalArgumentException("The server certificate could not be found: " + this.keyStorePath.toAbsolutePath());
            }

            java.security.cert.Certificate cert = this.keyStoreInstance.getCertificate(Config.Key.certServer);
            TrustStrategy strategy = (chain, authType) -> Arrays.asList((Certificate[]) chain).contains(cert);
            return context.loadTrustMaterial(strategy);
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            throw new IllegalArgumentException("The server certificate could not be accessed.", e);
        }
    }

}
