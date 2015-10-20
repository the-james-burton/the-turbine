#!/bin/bash

curl -XDELETE 'http://localhost:9200/turbine-ticks'
curl -XDELETE 'http://localhost:9200/turbine-stocks'
curl -XDELETE 'http://localhost:9200/turbine-strategies'


