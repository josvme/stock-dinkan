CREATE TABLE Fundamentals(
  id SERIAL PRIMARY KEY,
  symbol VARCHAR(30) NOT NULL,
  data JSONB NOT NULL,
  updated_time BIGINT NOT NULL
);

CREATE UNIQUE INDEX Fundamentals__Symbol
ON Fundamentals(symbol);
