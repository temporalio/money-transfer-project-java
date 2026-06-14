// @@@SNIPSTART money-transfer-java-worker
package moneytransferapp;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class MoneyTransferWorker {

    public static void main(String[] args) {
        // Connect to Temporal Cloud using the gRPC endpoint and API key supplied
        // via environment variables, with TLS enabled.
        WorkflowServiceStubs serviceStub = WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(System.getenv("TEMPORAL_ADDRESS"))
                        .addApiKey(() -> System.getenv("TEMPORAL_API_KEY"))
                        .setEnableHttps(true)
                        .build());

        // The Worker uses the Client to communicate with the Temporal Service
        WorkflowClient client = WorkflowClient.newInstance(serviceStub,
                WorkflowClientOptions.newBuilder()
                        .setNamespace(System.getenv("TEMPORAL_NAMESPACE"))
                        .build());

        // A WorkerFactory creates Workers
        WorkerFactory factory = WorkerFactory.newInstance(client);

        // A Worker listens to one Task Queue.
        // This Worker processes both Workflows and Activities
        Worker worker = factory.newWorker(Shared.MONEY_TRANSFER_TASK_QUEUE);

        // Register a Workflow implementation with this Worker
        // The implementation must be known at runtime to dispatch Workflow tasks
        // Workflows are stateful so a type is needed to create instances.
        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);

        // Register Activity implementation(s) with this Worker.
        // The implementation must be known at runtime to dispatch Activity tasks
        // Activities are stateless and thread safe so a shared instance is used.
        worker.registerActivitiesImplementations(new AccountActivityImpl());

        System.out.println("Worker is running and actively polling the Task Queue.");
        System.out.println("To quit, use ^C to interrupt.");

        // Start all registered Workers. The Workers will start polling the Task Queue.
        factory.start();
    }
}
// @@@SNIPEND
