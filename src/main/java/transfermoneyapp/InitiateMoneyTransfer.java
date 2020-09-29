package transfermoneyapp;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import java.util.UUID;

// @@@SNIPSTART java-project-template-workflow-initiator
public class InitiateMoneyTransfer {

    public static void main(String args[]) throws Exception {
        // WorkflowServiceStubs is a gRPC stubs wrapper
        // That talks to the local Docker instance of the Temporal server.
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(Shared.TRANSFER_MONEY_TASK_QUEUE)
                .setWorkflowId("transfer-money-workflow-001")
                .build();
        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
        WorkflowClient client = WorkflowClient.newInstance(service);
        // WorkflowFlowStub converts the parameters that are passed to it so that they can be passed to the server.
        TransferMoneyWorkflow workflow = client.newWorkflowStub(TransferMoneyWorkflow.class, options);

        String referenceId = UUID.randomUUID().toString();
        String fromAccount = "001-001";
        String toAccount = "002-002";
        double amount = 1.23;

        // Synchronous execution. This process waits for the Workflow to complete.
        // workflow.transfer(fromAccount, toAccount, referenceId, amount);

        // Asynchronous execution. This process will exit after making this call.
        WorkflowClient.start(workflow::transfer, fromAccount, toAccount, referenceId, amount);

        System.out.printf("Transfer of $%f from account %s to account %s is processing", amount, fromAccount, toAccount);
        System.exit(0);
    }
}
// @@@SNIPEND
