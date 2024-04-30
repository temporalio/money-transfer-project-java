package moneytransferapp;
// @@@SNIPSTART money-transfer-project-template-java-worker-import
import java.lang.System;
import java.io.FileInputStream;
import java.io.InputStream;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.SimpleSslContextBuilder;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import java.io.IOException;
// @@@SNIPEND

public class MoneyTransferWorker {
  public static void main(String[] args) throws IOException {
        // @@@SNIPSTART money-transfer-project-template-java-worker-certs
        // Get the key and certificate from your environment or local machine
        String clientCertFile = System.getenv("TEMPORAL_MTLS_TLS_CERT");
        String clientCertPrivateKey = System.getenv("TEMPORAL_MTLS_TLS_KEY");

        // Open the key and certificate as Input Streams
        InputStream clientCertInputStream = new FileInputStream(clientCertFile);
        InputStream clientKeyInputStream = new FileInputStream(clientCertPrivateKey);

        // Generate the sslContext using the Client Cert and Key
        SslContext sslContext = SimpleSslContextBuilder.forPKCS8(clientCertInputStream, clientKeyInputStream).build();
        // @@@SNIPEND
        // @@@SNIPSTART money-transfer-project-template-java-worker-options
        // Specify the host and port of your Temporal Cloud Namespace
        // Host and port format: namespace.unique_id.tmprl.cloud:port
        String namespace = System.getenv("TEMPORAL_NAMESPACE");
        String hostPort = System.getenv("TEMPORAL_HOST_URL");

        // Specify the IP address, port, and SSL Context for the Service Stubs options
        WorkflowServiceStubsOptions stubsOptions = WorkflowServiceStubsOptions.newBuilder()
                .setSslContext(sslContext)
                .setTarget(hostPort)
                .build();
        // @@@SNIPEND
        // Generate the gRPC stubs using the options provided
        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(stubsOptions);
        // @@@SNIPSTART money-transfer-project-template-java-worker-client-options
        // Specify the namespace in the Client options
        WorkflowClientOptions options = WorkflowClientOptions.newBuilder()
                .setNamespace(namespace)
                .build();

        WorkflowClient client = WorkflowClient.newInstance(service, options);
        // @@@SNIPEND
        WorkerFactory factory = WorkerFactory.newInstance(client);

        Worker worker = factory.newWorker(Shared.MONEY_TRANSFER_TASK_QUEUE);

        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
        worker.registerActivitiesImplementations(new AccountActivityImpl());

        factory.start();
    }
}
