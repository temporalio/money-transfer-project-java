# Money transfer project: Java

Learn how the pieces of a Temporal application work together.
Follow the [Run your first app tutorial](https://docs.temporal.io/docs/java/run-your-first-app-tutorial) to learn more about Temporal Workflows.

Note: This project uses [Snipsync](https://github.com/temporalio/snipsync) comment wrappers to automatically keep code snippets up to date within our documentation.

## Building, cleaning, and other tasks 

```
build:
    mvn clean install -Dorg.slf4j.simpleLogger.defaultLogLevel=info 2>/dev/null

clean:
    mvn clean -q -Dmaven.logging.level=0

worker:
    mvn compile exec:java -Dexec.mainClass="moneytransferapp.MoneyTransferWorker" -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

run:
    mvn compile exec:java -Dexec.mainClass="moneytransferapp.TransferApp" -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

serve:
    `command -v temporal` server start-dev --log-level=never &

stop-server:
    pkill temporal
```
