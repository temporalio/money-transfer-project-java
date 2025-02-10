// @@@SNIPSTART money-transfer-java-worker
package moneytransferapp;

import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import java.io.IOException;

public class MoneyTransferWorker {

    public static void main(String[] args) throws IOException {

        // Call a utility method that will return a Temporal Client that is 
        // configured based on the presence of environment variables
        WorkflowClient client = ClientProvider.getClient();

        WorkerFactory factory = WorkerFactory.newInstance(client);

        Worker worker = factory.newWorker(Shared.MONEY_TRANSFER_TASK_QUEUE);

        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
        worker.registerActivitiesImplementations(new AccountActivityImpl());

        factory.start();
    }
}
// @@@SNIPEND
