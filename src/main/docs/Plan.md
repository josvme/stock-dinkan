## List of Tasks
[x] Create first migration to create day-wise stock data

[x] Download stock data and save to disk

[x] Implement Dummy Read and Write interface and implementation

[x] Parse downloaded files with circe

[x] Write downloaded files to DB

[x] Read data from DB

[x] Build a simple flat-base analyzer

[] Write an integration test

[x] Download all stocks 

[] Run a test on all of them

[] test flat-base with a test

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
