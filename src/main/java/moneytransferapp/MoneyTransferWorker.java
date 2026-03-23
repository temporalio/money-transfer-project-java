// @@@SNIPSTART money-transfer-java-worker
package moneytransferapp;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.envconfig.ClientConfigProfile;
import io.temporal.envconfig.LoadClientConfigProfileOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.io.IOException;

public class MoneyTransferWorker {

    public static void main(String[] args) throws IOException {
        // The Worker uses the Client to communicate with the Temporal Service
        // This code will configure a Client based on details specified in the
        // profile specified by the TEMPORAL_PROFILEenvironment variable. If
        // unset, it will use the default settings.
        ClientConfigProfile profile =
                ClientConfigProfile.load(LoadClientConfigProfileOptions.newBuilder().build());

        WorkflowServiceStubsOptions serviceStubsOptions = profile.toWorkflowServiceStubsOptions();
        WorkflowClientOptions clientOptions = profile.toWorkflowClientOptions();

        WorkflowClient client =
                WorkflowClient.newInstance(
                        WorkflowServiceStubs.newServiceStubs(serviceStubsOptions), clientOptions);

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
