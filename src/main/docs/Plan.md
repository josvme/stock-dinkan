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

[x] Implement RSI calculator

[x] Add flexible boolean combiner

[x] Unify timestamps to run analysis by comparing timestamps

[x] Implement trend template from mark minervini

[x] Implement fundamental data from yfinance (Python)

[x] Implement stock data from yahoo finance as data from Alpaca is not split adjusted

[x] Write tests for SyncLatestDataWithYahooFinance as it doesn't work well on Sundays

[] Write analysis where volume reduces a lot and price skates the 50DMA and 21DMA

[] Do some data analysis to question CANSLIMs and other assumptions

[] Cup and handle finder

[] Tight flag finder

[] Compute leading groups

[] Get RS rating value between 0 and 100, 2*c/c63 + c/c126 + c/c189 + c/c252

[] Fix issue with pre-market gap ups and gap downs

[] Write an integration test

[] Build Cronjob server

[] Build an API

[] Build UI

[] Add more unittests

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