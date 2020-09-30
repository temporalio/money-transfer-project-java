package moneytransferapp;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import io.temporal.common.RetryOptions;
import java.time.Duration;

// @@@SNIPSTART money-transfer-project-template-java-workflow-implementation
public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {

    RetryOptions retryoptions =  RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(1))
            .setMaximumInterval(Duration.ofSeconds(100))
            .setBackoffCoefficient(2)
            .setMaximumAttempts(0)
            .build();

    private final ActivityOptions options = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .setRetryOptions(retryoptions)
            .build();

    // An ActivityStub converts the parameters that are passed to it so that the params can be passed to the server.
    private final AccountActivity account = Workflow.newActivityStub(AccountActivity.class, options);

    // The transfer method is the entry point to the Workflow.
    // Activity method executions can be orchestrated here or from within other Activity methods.
    @Override
    public void transfer(String fromAccountId, String toAccountId, String referenceId, double amount) {
        account.withdraw(fromAccountId, referenceId, amount);
        account.deposit(toAccountId, referenceId, amount);
    }
}
// @@@SNIPEND
