// @@@SNIPSTART money-transfer-java-activity-implementation
package moneytransferapp;

import io.temporal.activity.*;
import io.temporal.failure.ApplicationFailure;

public class AccountActivityImpl implements AccountActivity {
    // Mock up the withdrawal of an amount of money from the source account
    @Override
    public void withdraw(String accountId, String referenceId, int amount) {
        System.out.printf("\nWithdrawing $%d from account %s.\n[ReferenceId: %s]\n", amount, accountId, referenceId);
        System.out.flush();
    }

    // Mock up the deposit of an amount of money from the destination account
    @Override
    public void deposit(String accountId, String referenceId, int amount) {
        // Demo-only failure injection, driven by the DEMO_FAILURE env var on the
        // Worker. Unset/off leaves behavior unchanged.
        String demoFailure = System.getenv("DEMO_FAILURE");
        demoFailure = demoFailure == null ? "" : demoFailure.toLowerCase();

        if (demoFailure.equals("transient") && Activity.getExecutionContext().getInfo().getAttempt() < 3) {
            // Reuse the existing simulated-failure path for the first two attempts;
            // the error is retryable, so Temporal retries and the activity succeeds
            // on attempt 3 -> the Workflow recovers and COMPLETEs.
            System.out.println("Deposit failed");
            System.out.flush();
            throw Activity.wrap(new RuntimeException("Simulated Activity error during deposit of funds"));
        } else if (demoFailure.equals("permanent")) {
            // Reuse the simulated-failure message, but make it non-retryable so the
            // Workflow's refund compensation (saga rollback) runs instead of retrying.
            System.out.println("Deposit failed");
            System.out.flush();
            throw ApplicationFailure.newNonRetryableFailure(
                "Simulated Activity error during deposit of funds", "DepositFailure");
        }

        System.out.printf("\nDepositing $%d into account %s.\n[ReferenceId: %s]\n", amount, accountId, referenceId);
        System.out.flush();
    }

    // Mock up a compensation refund to the source account
    @Override
    public void refund(String accountId, String referenceId, int amount) {
        boolean activityShouldSucceed = true;

        if (!activityShouldSucceed) {
            System.out.println("Refund failed");
            System.out.flush();
            throw Activity.wrap(new RuntimeException("Simulated Activity error during refund to source account"));
        }

        System.out.printf("\nRefunding $%d to account %s.\n[ReferenceId: %s]\n", amount, accountId, referenceId);
        System.out.flush();
   }
}
// @@@SNIPEND
