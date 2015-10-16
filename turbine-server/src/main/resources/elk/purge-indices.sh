#!/bin/bash

curl -XDELETE 'http://localhost:9200/ticks'
curl -XDELETE 'http://localhost:9200/stocks'
curl -XDELETE 'http://localhost:9200/strategies'


