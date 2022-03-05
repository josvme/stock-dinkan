## List of Tasks

[x] Create first migration to create day-wise stock data

[x] Download stock data and save to disk

[x] Implement Dummy Read and Write interface and implementation

[x] Parse downloaded files with circe

[x] Write downloaded files to DB

[x] Read data from DB

[x] Build a simple flat-base analyzer

[x] Download all stocks

[x] Read all stocks

[x] test flat-base with a test

[] Implement trend template

[x] Implement RSI calculator

[] Add more unittests

[x] Add flexible boolean combiner

[x] Unify timestamps to run analysis by comparing timestamps

[] Fix issue with pre-market gap ups and gap downs

[] Write an integration test

[] Build Cronjob server

[] Build an API

[] Build UI

## List of high-level tasks

### Bootstrapping

* Write messages to Kafka for consuming

### Writing Data to DB

* Read file and write to DB
    * As of now tables are based on time, say 1D tables, 1min table, 4 hr table etc. For now we only have 1D table

### Read from DB

* Read data from database

### Build an analyzer

* Write a simple flat-base analyzer.

### Build a UI

* Build a UI

### Optimize

* Download stock data for n days and save to DB.

## Technical Details

* Make scala project multi-project, rather than a single big project. This will help in running as cronjobs / pods

## Current Issues
* Too many blank companies on filtering
* No P/E or Earnings etc
* No SMA/EMA etc
* NO RS Line
* Cumbersome to use UI