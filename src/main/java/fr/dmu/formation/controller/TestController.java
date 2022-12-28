package fr.dmu.formation.controller;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@RestController
public class TestController
{
    @Value("${trust.store}")
    private Resource trustStore;

    @Value("${trust.store.password}")
    private String trustStorePassword;

    @GetMapping(value = "/client/test")
    public String test() throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException
    {
        System.out.println(" ======================================");
        System.out.println("SSL password = "+trustStorePassword);
        System.out.println(" ======================================");


        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray())
                .build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        HttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate rt = new RestTemplate(factory);
        String res = rt.getForObject("https://localhost:8443/test", String.class);

        return "Client a reçu : "+res;
    }
}
