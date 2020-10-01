package moneytransferapp;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.util.UUID;

// @@@SNIPSTART money-transfer-project-template-java-workflow-initiator
public class InitiateMoneyTransfer {

    public static void main(String[] args) throws Exception {

        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
        WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(Shared.MONEY_TRANSFER_TASK_QUEUE)
                // A WorkflowId prevents this it from having duplicate instances, remove it to duplicate.
                .setWorkflowId("money-transfer-workflow")
                .build();
        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
        WorkflowClient client = WorkflowClient.newInstance(service);
        // WorkflowStubs enable calls to methods as if the Workflow object is local, but actually perform an RPC.
        MoneyTransferWorkflow workflow = client.newWorkflowStub(MoneyTransferWorkflow.class, options);
        String referenceId = UUID.randomUUID().toString();
        String fromAccount = "001-001";
        String toAccount = "002-002";
        double amount = 18.74;
        // Uncomment for synchronous execution. This process waits for the Workflow to complete.
        // workflow.transfer(fromAccount, toAccount, referenceId, amount);
        // Asynchronous execution. This process will exit after making this call.
        WorkflowClient.start(workflow::transfer, fromAccount, toAccount, referenceId, amount);
        System.out.printf("Transfer of $%f from account %s to account %s is processing", amount, fromAccount, toAccount);
        System.exit(0);
    }
}
// @@@SNIPEND
