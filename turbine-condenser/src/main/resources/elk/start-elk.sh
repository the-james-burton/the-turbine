#!/bin/bash

~/Development/installs/elasticsearch/bin/elasticsearch
~/Development/installs/kibana/bin/kibana 

~/Development/installs/logstash/bin/logstash -f ~/Development/projects/the-turbine/turbine/src/main/resources/elk/logstash-json.conf
