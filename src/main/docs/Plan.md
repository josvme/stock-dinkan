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

[x] Add connection pooling and parallelism to Analyzer (Got from 10min to 5min)

[x] Find leading sectors daily, weekly, monthly

[x] Implement RS rating across all stocks

[x] Get RS rating value between 0 and 100, 2*c/c63 + c/c126

[x] Build an API

[x] Build UI - Embed TV (https://www.tradingview.com/widget/advanced-chart/)

[x] Tryout ClickHouse. Not really worth it as we need to fetch all data. And it is just ~1.5X faster than postgres when fetching all data. It might be faster with specialized analysis

[x] Trading view widget data is delayed by 15min. Not ideal, but is also fine.

[x] Implement green colored bar in TL. It is very helpful in finding leaders.

[x] Analysis for finding high volume up moves.

[] Implement launchpad around MAs

[] Write an analysis for multiple pocket pivots before earnings.

[] Implement efficient download of fundamentals based on earnings rate and update it.

[] Introduce some efficient update of prices based on prices (say only stock over $5 will get updated everyday)

[] Design a better UI

[] Implement a better UI

[] Improve API

[] Get RS rating value between 0 and 100, 2*c/c63 + c/c126 + c/c189 + c/c252

[] Write analysis where volume reduces a lot and price skates the 50DMA and 21DMA

[] Do some data analysis to question CANSLIMs and other assumptions

[] Cup and handle finder

[] Tight flag finder

[] Compute leading groups

[] Fix issue with pre-market gap ups and gap downs

[] Write an integration test

[] Build Cronjob server

[] Add more unittests

[] Deploy to GKE via terraform / pulumi

## Get earnings date via yfinance
The `obj.info` makes 5 calls and caches the results. So new data access, doesn't result in new requests.
The `obj.earnings_history` just makes 1 call. It includes estimate, reported and surprises. So we can compute earnings growth.
```python
>>> import yfinance as yf
>>> obj = yf.Ticker('MSFT')
>>> obj.earnings_history
```

## Green lines in Trading View Volume (TL)
* If up days volume / down day volume over last 20 days. If over 1.5 it is under accumulation.
* If current volume is 100% above average volume.

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
* Stock splits