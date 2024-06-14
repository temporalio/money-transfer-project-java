##  make -f ../Makefile
##  make run -f ../Makefile
##  make worker -f ../Makefile

.PHONY: build notests clean worker run serve servedb stop

build:
	@mvn clean install -Dorg.slf4j.simpleLogger.defaultLogLevel=info 2>/dev/null

notests:
	@mvn clean install -DskipTests -Dorg.slf4j.simpleLogger.defaultLogLevel=info 2>/dev/null

clean:
	@mvn clean -q -Dmaven.logging.level=0

worker:
	@mvn compile exec:java -Dexec.mainClass="moneytransferapp.MoneyTransferWorker" -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

run:
	@mvn compile exec:java -Dexec.mainClass="moneytransferapp.TransferApp" -Dorg.slf4j.simpleLogger.defaultLogLevel=warn

serve:
	@nohup `command -v temporal` server start-dev --log-level=never &

servedb:
	`command -v temporal` server start-dev --log-level=never --db-filename=/Users/ericasadun/Desktop/temporal.db

stop:
	@pkill temporal

