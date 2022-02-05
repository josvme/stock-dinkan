CREATE TABLE DayValues(
  id SERIAL PRIMARY KEY,
  symbol VARCHAR(30) NOT NULL,
  stime INTEGER NOT NULL,
  sopen FLOAT NOT NULL,
  sclose FLOAT NOT NULL,
  low FLOAT NOT NULL,
  high FLOAT NOT NULL,
  volume INTEGER NOT NULL,
  trade_count INTEGER NOT NULL,
  vwap FLOAT NOT NULL
);

CREATE UNIQUE INDEX DayValues__SymbolTime
ON DayValues(stime, symbol);
