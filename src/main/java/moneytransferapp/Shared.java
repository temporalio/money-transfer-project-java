// @@@SNIPSTART money-transfer-java-shared
package moneytransferapp;

public interface Shared {
    // Reads TEMPORAL_TASK_QUEUE when set — e.g. a test harness isolating each run on its
    // own queue — otherwise the shared default, so a copy-paste user is unaffected.
    static final String MONEY_TRANSFER_TASK_QUEUE =
            System.getenv().getOrDefault("TEMPORAL_TASK_QUEUE", "MONEY_TRANSFER_TASK_QUEUE");
}
// @@@SNIPEND
