package itmo.populationservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class SoapClientConfig {

    private static final Logger log = LoggerFactory.getLogger(SoapClientConfig.class);

    @Value("${ssl.truststore.path:file:../certs/truststore.p12}")
    private Resource trustStoreResource;

    @Value("${ssl.truststore.password:changeit}")
    private String trustStorePassword;

    @PostConstruct
    public void configureSsl() {
        try {
            log.info("Loading truststore from: {}", trustStoreResource);

            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            try (InputStream is = trustStoreResource.getInputStream()) {
                trustStore.load(is, trustStorePassword.toCharArray());
            }
            log.info("Truststore loaded successfully");

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) ->
                    "localhost".equals(hostname) || "127.0.0.1".equals(hostname)
            );

            log.info("SSL configured successfully");

        } catch (Exception e) {
            log.warn("Failed to load truststore, using trust-all: {}", e.getMessage());
            trustAll();
        }
    }

    private void trustAll() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("itmo.populationservice.soap");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMarshaller(marshaller());
        template.setUnmarshaller(marshaller());
        return template;
    }
}

