package transfermoneyapp;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;

// @@@SNIPSTART java-project-template-workflow-implementation
public class TransferMoneyWorkflowImpl implements TransferMoneyWorkflow {

    private final ActivityOptions options =
            ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .build();

    // An ActivityStub converts the parameters that are passed to it, so that the params can be passed to the server.
    // The first param identifies the Activity code to be executed
    // The second parameter identifies any options that need to be applied to the code's execution.
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
