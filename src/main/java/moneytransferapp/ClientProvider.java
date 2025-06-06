package moneytransferapp;

import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.temporal.authorization.AuthorizationGrpcMetadataProvider;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.SimpleSslContextBuilder;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.net.ssl.SSLException;

public class ClientProvider {

    /**
     * @return a Workflow Client that is configured to use mTLS, API Key, or no
     * authentication, based on the whether / how environment variables are set.
     * @throws java.io.IOException if there is a problem with configuration
     */
    public static WorkflowClient getClient() throws IOException {
        // Specify the address of your Temporal Service (format: hostname:port)
        String hostPort = System.getenv("TEMPORAL_ADDRESS");
        if (hostPort == null) {
            hostPort = "localhost:7233";
        }

        // Specify the Namespace to use for requests from this Client
        String namespace = System.getenv("TEMPORAL_NAMESPACE");
        if (namespace == null) {
            namespace = "default";
        }

        // Specify the path to the certificate and private key paths if using mTLS auth
        String clientCertFile = System.getenv("TEMPORAL_TLS_CERT");
        String clientCertPrivateKey = System.getenv("TEMPORAL_TLS_KEY");

        // If using API Key authentication, specify the API Key value
        String apiKey = System.getenv("TEMPORAL_API_KEY");

        // Default to no authentication
        WorkflowClient client = getDefaultClient(hostPort, namespace);
        if (clientCertFile != null && clientCertPrivateKey != null) {
            client = getMtlsClient(hostPort, namespace, clientCertFile, clientCertPrivateKey);
        } else if (apiKey != null) {
            client = getApiKeyClient(hostPort, namespace, apiKey);
        }

        return client;
    }

    /**
     * @param hostPort Temporal Service address (format as hostname:port)
     * @param namespace Namespace that this Client should use for requests
     * @return a Workflow Client that is not configured to use either mTLS or
     * API Key authentication (this is intended for use with a local Temporal
     * Service started from the <code>temporal</code> CLI)
     */
    public static WorkflowClient getDefaultClient(String hostPort, String namespace) {
        WorkflowServiceStubsOptions stubsOptions = WorkflowServiceStubsOptions.newBuilder()
                .setTarget(hostPort)
                .build();

        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(stubsOptions);

        WorkflowClientOptions options = WorkflowClientOptions.newBuilder()
                .setNamespace(namespace)
                .build();

        return WorkflowClient.newInstance(service, options);
    }

    /**
     * @param hostPort Temporal Service address (format as hostname:port)
     * @param namespace Namespace that this Client should use for requests
     * @param tlsCertificatePath Path to the TLS certificate file
     * @param tlsPrivateKeyPath Path to the TLS private key file
     * @return a Workflow Client configured to use API Key authentication
     * @throws java.io.FileNotFoundException if there is a problem loading TLS
     * key or certificate
     * @throws javax.net.ssl.SSLException if there is a problem with
     * establishing a TLS connection
     */
    public static WorkflowClient getMtlsClient(String hostPort, String namespace,
            String tlsCertificatePath, String tlsPrivateKeyPath) throws FileNotFoundException, SSLException {

        InputStream clientCertInputStream = new FileInputStream(tlsCertificatePath);
        InputStream clientKeyInputStream = new FileInputStream(tlsPrivateKeyPath);
        SslContext sslContext = SimpleSslContextBuilder.forPKCS8(clientCertInputStream, clientKeyInputStream).build();

        // Specify the IP address, port, and SSL Context for the Service Stubs options
        WorkflowServiceStubsOptions stubsOptions = WorkflowServiceStubsOptions.newBuilder()
                .setSslContext(sslContext)
                .setTarget(hostPort)
                .build();

        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(stubsOptions);

        WorkflowClientOptions options = WorkflowClientOptions.newBuilder()
                .setNamespace(namespace)
                .build();

        return WorkflowClient.newInstance(service, options);
    }

    /**
     * @param hostPort Temporal Service address (format as hostname:port)
     * @param namespace Namespace that this Client should use for requests
     * @param apiKey Value of the API key to use for authentication
     * @return a Workflow Client configured to use API Key authentication
     * @throws javax.net.ssl.SSLException if there is a problem with
     * establishing a TLS connection
     */
    public static WorkflowClient getApiKeyClient(String hostPort, String namespace,
            String apiKey) throws SSLException {

        WorkflowServiceStubsOptions.Builder stubsOptions
                = WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(hostPort)
                        .setSslContext(SimpleSslContextBuilder.noKeyOrCertChain().setUseInsecureTrustManager(false).build())
                        .addGrpcMetadataProvider(
                                new AuthorizationGrpcMetadataProvider(() -> "Bearer " + apiKey));

        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(stubsOptions.build());
        WorkflowClientOptions clientOptions = WorkflowClientOptions.newBuilder().setNamespace(namespace).build();
        return WorkflowClient.newInstance(service, clientOptions);
    }
}
