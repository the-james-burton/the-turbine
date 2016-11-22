mate-terminal -e "bash -c \"~/Development/installs/elasticsearch/bin/elasticsearch; exec bash\""  
mate-terminal -e "bash -c \"~/Development/installs/kibana/bin/kibana; exec bash\""  
mate-terminal -e "bash -c \"~/Development/installs/logstash/bin/logstash -f ~/Development/projects/the-turbine/turbine/src/main/resources/elk/logstash-json.conf; exec bash\""  
mate-terminal -e "bash -c \"~/Development/installs/rabbitmq-server/sbin/rabbitmq-server; exec bash\""  



