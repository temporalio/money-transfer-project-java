// @@@SNIPSTART money-transfer-java-initiate-transfer
package moneytransferapp;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.envconfig.ClientConfigProfile;
import io.temporal.envconfig.LoadClientConfigProfileOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;

import java.io.IOException;
import java.util.UUID;
import java.util.Random;

public class TransferApp {
    private static final Random random = new Random();

    public static String randomAccountIdentifier() {
        String allowedChars = "ABCDEFGHJKMNPQRTUVWXY346789";
        StringBuilder accountId = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(allowedChars.length());
            accountId.append(allowedChars.charAt(index));
        }
        return accountId.toString();
    }

    public static void main(String[] args) throws IOException {

        // The Client communicates with the Temporal Service, starting the Workflow.
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

        // Workflow options configure Workflow stubs.
        // A WorkflowId prevents duplicate instances, which are removed.
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(Shared.MONEY_TRANSFER_TASK_QUEUE)
                .setWorkflowId("money-transfer-workflow")
                .build();

        // WorkflowStubs enable calls to methods as if the Workflow object is local
        // but actually perform a gRPC call to the Temporal Service.
        MoneyTransferWorkflow workflow = client.newWorkflowStub(MoneyTransferWorkflow.class, options);
        
        // Configure the details for this money transfer request
        String referenceId = UUID.randomUUID().toString().substring(0, 18);
        String fromAccount = randomAccountIdentifier();
        String toAccount = randomAccountIdentifier();
        int amountToTransfer = random.nextInt(15, 75);
        TransactionDetails transaction = new CoreTransactionDetails(fromAccount, toAccount, referenceId, amountToTransfer);

        // Perform asynchronous execution.
        // This process exits after making this call and printing details.
        WorkflowExecution we = WorkflowClient.start(workflow::transfer, transaction);

        System.out.println("MONEY TRANSFER PROJECT\n");
        System.out.printf("Initiating transfer of $%d from [Account %s] to [Account %s].\n\n",
                          amountToTransfer, fromAccount, toAccount);
        System.out.printf("[WorkflowID: %s]\n[RunID: %s]\n[Transaction Reference: %s]\n\n", we.getWorkflowId(), we.getRunId(), referenceId);
        System.exit(0);
    }
}
// @@@SNIPEND
