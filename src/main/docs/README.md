## Why this project?

* Marketsmith costs 150$
* The hardest part is to find the stocks, other part is looking at Trading view and discipline
* Need a way to manage trading statistics
* Have much more flexibility in to the future
* Scratch an itch and learn a few technologies

## Project Goals

1. Able to screen for stocks
2. Get most of required information at once
3. Able to add new analysis
4. Easy to use UI

## 1. Able to screen stocks

* Screen by Relative Strength line Trend
* Screen by % spread in **n** days.

## 2. Get most of required information at once

* Sales and other information like growth. Also earnings rate

## 3. Able to add analysis

* In the future should be able to add simple analysis. More complicated ones can be moved to TradingView.

## 4. Easy to use UI

* Should have a nice UI. Not advanced, but usable.

## Non-Project Goals as of now

* Back-testing / strategy testing
* Advanced charting
* Intra-day updates
* AI based trading

## More Details

You can get all stock symbols from [here](https://stockanalysis.com/stocks/)
To be exact this [url](https://stockanalysis.com/_next/data/NcayYmKtpNDZ0pDPfBaPk/stocks.json)
You can get all stock symbols from US Market[here](https://www.nasdaq.com/market-activity/stocks/screener)
Via API is `https://api.nasdaq.com/api/screener/stocks?tableonly=true&limit=25&offset=0&download=true`

Blank check companies have no earnings
```sql
select symbol from fundamentals where data @> '{"ebitda": null}'
```

## Calling the API
```bash
http localhost:8080/analysis/tight-consolidation -v --timeout=900
```