// @@@SNIPSTART money-transfer-java-workflow-implementation
package moneytransferapp;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import io.temporal.common.RetryOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class MoneyTransferWorkflowImpl implements MoneyTransferWorkflow {
    private static final String WITHDRAW = "Withdraw";

    // RetryOptions specify how to automatically handle retries when Activities fail.
    private final RetryOptions retryoptions = RetryOptions.newBuilder()
        .setInitialInterval(Duration.ofSeconds(1)) // Wait 1 second before first retry
        .setMaximumInterval(Duration.ofSeconds(20)) // Do not exceed 20 seconds between retries
        .setBackoffCoefficient(2) // Wait 1 second, then 2, then 4, etc.
        .setMaximumAttempts(5) // Fail after 5 attempts
        .build();

    // ActivityOptions specify the limits on how long an Activity can execute before
    // being interrupted by the Orchestration service.
    private final ActivityOptions defaultActivityOptions = ActivityOptions.newBuilder()
        .setRetryOptions(retryoptions) // defined above
        .setStartToCloseTimeout(Duration.ofSeconds(2)) // Max execution time for single Activity
        .setScheduleToCloseTimeout(Duration.ofSeconds(5)) // Entire duration from scheduling to completion,
                                                          // including queue time.
        .build();

    private final Map<String, ActivityOptions> perActivityMethodOptions = new HashMap<String, ActivityOptions>() {{
        // A heartbeat time-out is a proof-of life indicator that an activity is still working.
        // This option says to wait for 5 seconds to hear a heartbeat. If one is not heard,
        // the Activity fails.
        put(WITHDRAW, ActivityOptions.newBuilder().setHeartbeatTimeout(Duration.ofSeconds(5)).build());
    }};

    // ActivityStubs enable calls to methods as if the Activity object is local,
    // but actually perform an RPC invocation.
    private final AccountActivity accountActivityStub = Workflow.newActivityStub(AccountActivity.class, defaultActivityOptions, perActivityMethodOptions);

    // The transfer method is the entry point to the Workflow.
    // Activity method executions can be orchestrated here or from within
    // other Activity methods.
    @Override
    public void transfer(TransactionDetails transaction) {
        String sourceAccountId = transaction.getSourceAccountId();
        String destinationAccountId = transaction.getDestinationAccountId();
        String transactionReferenceId = transaction.getTransactionReferenceId();
        int amountToTransfer = transaction.getAmountToTransfer();

        try {
            accountActivityStub.withdraw(sourceAccountId, transactionReferenceId, amountToTransfer);
        } catch (Exception e) {
            // If the withdrawal fails, for any exception
            System.out.printf("[%s] Withdrawal of $%d from account %s failed",
                transactionReferenceId, amountToTransfer, sourceAccountId);
            System.out.flush();

            // Transaction ends here
            return;
        }

        // Change this from true to false to force the deposit to fail
        boolean transferShouldSucceed = true;

        try {
            accountActivityStub.deposit(destinationAccountId, transactionReferenceId, amountToTransfer, transferShouldSucceed);

            // Successful. Transaction ends here
            System.out.printf("[%s] Transaction succeeded.\n", transactionReferenceId);
            System.out.flush();
            return;
        } catch (Exception e) {
            // If the deposit fails, for any exception
            System.out.printf("[%s] Deposit of $%d to account %s failed.\n",
                transactionReferenceId, amountToTransfer, destinationAccountId);
            System.out.flush();
        }

        // Continue by reversing transaction

        // Change this from true to false to force the recovery compensation to fail
        boolean refundShouldSucceed = true;

        try {
            // Refund the withdrawal
            System.out.printf("[%s] Refunding $%d to account %s.\n",
                transactionReferenceId, amountToTransfer, destinationAccountId);
            System.out.flush();
            accountActivityStub.deposit(sourceAccountId, transactionReferenceId, amountToTransfer, refundShouldSucceed);

            // Recovery successful. Transaction ends here
            System.out.printf("[%s] Refund to originating account was successful.\n",
                transactionReferenceId);
            System.out.printf("[%s] Transaction is complete. No transfer made.\n",
                transactionReferenceId);
            return;
        } catch (Exception e) {
            // A recovery mechanism can fail too. Handle any exception
            System.out.printf("[%s] Deposit of $%d to account %s failed. Did not compensate withdrawal.\n",
                transactionReferenceId, amountToTransfer, destinationAccountId);
            System.out.printf("[%s] Workflow failed.", transactionReferenceId);
            System.out.flush();

            // Rethrowing the exception causes a Workflow Task failure
            throw(e);
        }
    }
}
// @@@SNIPEND
