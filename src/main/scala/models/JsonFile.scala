package models

case class JsonFile(open: Map[Long, Float],
                    high: Map[Long, Float],
                    low: Map[Long, Float],
                    close: Map[Long, Float],
                    volume: Map[Long, Int],
                    trade_count: Map[Long, Int],
                    vwap: Map[Long, Float])
