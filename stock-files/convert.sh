#!/bin/bash
cat stockslist.json | jq '[.pageProps.stocks[].s]'  > stocks.json