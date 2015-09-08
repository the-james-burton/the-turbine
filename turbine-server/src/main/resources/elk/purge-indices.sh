#!/bin/bash

curl -XDELETE 'http://localhost:9200/test-tick'
curl -XDELETE 'http://localhost:9200/test-instrument'
curl -XDELETE 'http://localhost:9200/instrument'
curl -XDELETE 'http://localhost:9200/quote'

