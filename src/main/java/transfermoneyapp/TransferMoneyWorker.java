package transfermoneyapp;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

// @@@SNIPSTART java-hello-world-sample-worker
public class TransferMoneyWorker {

    public static void main(String args[]) {
        // WorkflowServiceStubs is a gRPC stubs wrapper
        // That talks to the local Docker instance of the Temporal server.
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowClient client = WorkflowClient.newInstance(service);
        // Worker factory is used to create Workers for specific Task Queues
        WorkerFactory factory = WorkerFactory.newInstance(client);
        // Create a Worker that listens to a Task Queue
        Worker worker = factory.newWorker(Shared.TRANSFER_MONEY_TASK_QUEUE);
        // This Worker hosts both Workflow and Activity implementations
        // Register the Workflow with the Worker
        // Workflows are stateful. So you need a type to create instances.
        worker.registerWorkflowImplementationTypes(TransferMoneyWorkflowImpl.class);
        // Register the Activity with the Worker
        // Activities are stateless and thread safe, so a shared instance is used.
        worker.registerActivitiesImplementations(new AccountActivityImpl());
        // Start listening to the Task Queue.
        factory.start();
    }
}
// @@@SNIPEND
