// @@@SNIPSTART money-transfer-java-activity-implementation
package moneytransferapp;

import io.temporal.activity.*;

public class AccountActivityImpl implements AccountActivity {
    // Mock up the withdrawal of an amount of money from the source account
    @Override
    public void withdraw(String accountId, String referenceId, int amount) {
        System.out.printf(
                "\nWithdrawing $%d from account %s.\n[ReferenceId: %s]\n",
                amount, accountId, referenceId
        );
    }

    // Mock up the deposit of an amount of money from the destination account
    @Override
    public void deposit(String accountId, String referenceId, int amount, boolean activityShouldSucceed) {
        System.out.printf(
                "\nDepositing $%d into account %s.\n[ReferenceId: %s]\n",
                amount, accountId, referenceId
        );

        if (!activityShouldSucceed) {
            System.out.println("Deposit failed");
            throw Activity.wrap(new RuntimeException("Simulated Activity error during deposit of funds"));
        }
    }
}
// @@@SNIPEND
