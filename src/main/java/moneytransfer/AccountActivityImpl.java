// @@@SNIPSTART money-transfer-java-activity-implementation
package moneytransferapp;

public class AccountActivityImpl implements AccountActivity {
    // Mock up the withdrawal of an amount of money from the source account
    @Override
    public void withdraw(String accountId, String referenceId, double amount) {
        System.out.printf(
                "\nWithdrawing $%.2f from account %s.\n[ReferenceId: %s]\n",
                amount, accountId, referenceId
        );
    }

    // Mock up the deposit of an amount of money from the destination account
    @Override
    public void deposit(String accountId, String referenceId, double amount) {
        System.out.printf(
                "\nDepositing $%.2f into account %s.\n[ReferenceId: %s]\n",
                amount, accountId, referenceId
        );

        // TO SIMULATE AN ACTIVITY ERROR: Uncomment the following line
        // throw Activity.wrap(new RuntimeException("Simulated Activity error"));
    }
}
// @@@SNIPEND
