# the-turbine [![Build Status](https://api.travis-ci.org/the-james-burton/the-turbine.svg?branch=master)](https://travis-ci.org/the-james-burton/the-turbine)

## What is it?

The turbine is a set of server-side components that perform automated technical analysis on stocks. It it not actually connected just yet to any real source of stock prices, instead, it currently generates random data so that the project can be further developed. This data is also analysed with common technical indicators and trading strategies and the results of that analysis are persisted into Elasticsearch for further analysis downstream.

> *DO NOT USE THIS APP FOR REAL TRADING !!*

## What does it looks like?

This is a server-side only app and thus has no UI. However, it is easily possible (by design) to view the data in Kibana. Simple dashboards look like this...

![kibana](https://github.com/the-james-burton/the-turbine/blob/master/docs/kibana.png "kibana")

It is broken up into modules...

* **engine-hall** the parent pom for the spring boot services.
  * **furnace** Responsible for providing real-time stock prices. At the moment, this just generates random data.
  * **condenser** Analyses the stock prices provided by the *furnace* and stores the results of that analysis into lasticsearch.
  * **inlet** Mocks external finance APIs from Google and Yahoo to allow easier offline development.
* **annex** the parent pom for non-spring boot libraries.
  * **fuel** Shared domain objects and configuration. Library only - not executable.
  * **inspector** Testing extensions such as custom Hamcrest matchers.

## How does it work?

The coarse sequence of events flows like this...

0. **The furnace creates stock 'ticks'** These 'tick' objects are simple and contain just a high, low, open and close price, plus a few co-ordinates (market/symbol) and a timestamp. These ticks are currently generated at random using a simple algorithm to ensure continuity in the data.
0. **The furnace publishes the ticks to rabbitMQ** and that is the job of the furnace finished! It will do more work when it is connected to a source of real market data.
0. **The condenser receives the ticks** via the rabbitMQ camel component.
0. **The condenser does the indicator and strategy analysis** This is the meat of the work done by the condenser. It runs a suite of indicators (such as Bollinger bands) and trading strategies (such as CCI correction) and creates suitable objects for them.
0. **The condenser publishes the ticks and analysis to elasticsearch and websockets (via rabbitMQ)** Once it has completed its work, the condenser will save the incoming ticks and the analysis results to elasticsearch, where generic tools such as Kibana can be used to chart the results. It will also publish them to websockets, via the webstomp plugin from RabbitMQ so that my *atacama* web client can receive them as an inbound message.

The architecture looks like this...

![architecture](https://github.com/the-james-burton/the-turbine/blob/master/docs/architecture.png "architecture")

Everything is sent around as JSON. This makes it easy to persist in elasticsearch and use in a web app. Note how the *condenser* app is not client-facing. This is a less common design trait. I decided to make it this way because...

0. **I didn't want to implement another bespoke elasticsearch API and client.** Why not just use the built-in REST API and factory client? Well, that's what I did.
0. **RabbitMQ supports stomp over websockets!** and spring boot lets you plug it right in as a websocket broker. This lets rabbitMQ take control of *all* the asynchronous messaging.

> NOTE: Issue #24 describes a limitation with the Spring websocket RabbitMQ relay that is described above. Using it means I can't use reactor 3! So for now I have gone back to using the built-in Spring websocker broker until Spring 5/Boot 2 is released which will use reactor 3.

I believe a main concern with this design is security. To secure the connections to elasticsearch, I would most likely put ApiMan or similar over the top. To secure the rabbitMQ webstomp connections I am not so sure. I need to do more research. However it is very easy to stop using rabbitMQ as websocket broker and use the native spring support instead.


## Do I need anything installed?

Yes, this is intended to be considered as an _enterprise_ app and it expects a number of services to be available on the host machine

### Elasticsearch 2.3.2

This DOES need to be 2.3.2 because of [#17483](https://github.com/elastic/elasticsearch/issues/17483) affecting 2.3.1.

0. Copy the contents of the [elasticsearch.yml](https://github.com/the-james-burton/the-turbine/blob/master/turbine-condenser/src/main/resources/elk/elasticsearch.yml) file into your elasticsearch.yml file to make sure that CORS support is enabled correctly to allow [atacama](https://github.com/the-james-burton/atacama) to connect.

### Kibana 4.5.0

It is not strictly necessary to install Kibana, but it will let you view the results without needing to get my atacama project running.

### RabbitMQ 3.6.x

Will also work with RabbitMQ 3.5.x and maybe lower versions too. Development is continuing with RabbitMQ 3.6.x so I advise you to use the same.

0. Install the [web-stomp](https://www.rabbitmq.com/web-stomp.html) RabbitMQ plugin to enable support for Stomp over Websockets within rabbitMQ. You can do this by running the following command:`./sbin/rabbitmq-plugins enable rabbitmq_web_stomp`
0. Configure CORS support in RabbitMQ by running the following command: `./sbin/rabbitmqctl set_permissions -p /localhost guest ".*" ".*" ".*"`

#### Optional extra config for SSL (not needed by default)

0. Copy the [rabbitmq.config](https://github.com/the-james-burton/the-turbine/blob/master/turbine-condenser/src/main/resources/rabbit/rabbitmq.config) file and the into a `etc/rabbitmq` directory inside your RabbitMQ install dir (or merge the contents). This may need to go somewhere else for you depending on how you installed RabbitMQ - check your RabbitMQ logs as they will tell you where it looked for the `rabbitmq.config` file.
0. Copy the [certificate.crt](https://github.com/the-james-burton/the-turbine/blob/master/turbine-condenser/src/main/resources/security/certificate.key) and [certificate.key](https://github.com/the-james-burton/the-turbine/blob/master/turbine-condenser/src/main/resources/security/certificate.key) files and the into the same `etc/rabbitmq` directory. You could generate your own certificates instead, if you wanted to. Some help is in this [README.md](https://github.com/the-james-burton/the-turbine/blob/master/turbine-condenser/src/main/resources/security/README.md) file.
0. Edit your copy of the `rabbitmq.config` file that you took above and update the properties that reference the certificate.* files so that they are prefixed with the full path to them on your system.

## How do I use it?

The only way at present to get it up and running is to clone this repo, build it then run it in eclipse using the provided launch configurations. Make sure you have java 8, maven, rabbitMQ (with the webstomp plugin), elasticsearch and kibana installed then run something like the following...

```bash
git clone https://github.com/the-james-burton/the-turbine/the-turbine.git the-turbine
cd the-turbine
mvn clean install
```

Then open the project in Eclipse and run the launch configurations in *ide/eclipse/launch*. You need to start the condenser and then the furnace. If working, you should see logging activity as the furnace generates random stock ticks and the condenser analyses them. You should then browse to your kibana dashboard and see if you have any data in the *turbine-\** indicies.

At some point I will provide an easier way of installing and running this app using spring boot packaging best practice, but it remains a development project for now.

### Help, I get 'insecure response' errors!

You may see this if you have turned on SSL/HTTPS. If oyu do, then you need to tell your browser to trust these HTTPS URLs. In chrome, you can do this by simply browsing to the URLs, clicking 'advanced' and then trusting them.

```
https://localhost:15671/stomp/info
https://localhost:48002/user
```

## What is it built on?

It is a multi-module maven project written in Java 8. Amongst others, it makes use of the following superb open source projects...

* **spring boot** I chose spring boot as my framework as it can superceed many bespoke parent POMs including my own.
* **apache camel** To provide muti-threaded EIP and easily connect to the other main technologies in this project.
* **Ta4J** The awesome 'technical analysis for java' project provides everything I need to do the hard work of actually analysing the stocks.

It communicates with two principal back end components...

* **elasticsearch** Using the spring-data-elasticsearch client. Elasticsearch is used as primary persistence in this project. There is no traditional RDBMS. Historic data can be fetched from elasticsearch for back-populating stock history if needed. Although my *atacama* client project communicates directly with elasticsearch, spring provides a very easy way to write a REST API to do similar if I need to.
* **rabbitMQ** As the AMQP backbone of the project, RabbitMQ is the interconnect between the *furnace* and the *condenser* and my *atacama* client (through the magic of the webstomp plugin).

## What new-ish things have been done?

* **replace spring-data-elasticsearch** I was using spring data elasticsearch. However, the rate of change of elasticsearch seems very high, so I took control back and just used the ES Java client directly. Robust testing was put in place to provide solid foundations for future dev.
* **spring-security** the entire rest API is now secured with spring-security. This gives me HTTP basic authentication, which I have wrapped in HTTPS with a self-signed certificate.
* **javaslang** I have started using this library in earnest in the new *turbine-inlet* module and it has been very successful indeed. I plan to extend use of it throught this project. I have learned a lot from using it.

## What is already done?

* **multi-module separation** There is reasonable separation of functionality into different modules. This will continue and be strengthened as I continue developing.
* **camel for all integration** Camel contols all flow and integration with external components.
* **unit tests** Not complete by any means, but JUnit, Mockito, Hamcrest and AssertJ are being used for effective unit testing.
* **api docs** Swagger is in place, delivered by the excellent springfox project. This gives me advanced APIs docs, although I don't see the API being very large for this project as 1) it is primarily a business application and 2) clients get the data from elasticsearch.

## What is going to be done soon?

* **turbine-inlet** A new module that will simulate the external finance APIs such as those provided by Google and Yahoo. It is initially another Spring Boot project using javaslang, but may be a possible candidate for a Haskell/Yesod implementation (to give me an excuse to learn those!).
* **authentication, authorisation and registration** Spring-security is now in place so more security services can be built on top.
* **evaluate [cyclops-react](https://github.com/aol/cyclops-react) as potential Apache Camel replacement for internal routes** This is a very interesting library indeed and may provide a next-generation way to write internal async processing streams.
* **evaluate [javaslang](https://github.com/javaslang/javaslang) for better functional programming**. This also appears to integrate into cyclops-react. It should help me get better at functional programming in general.
* **more indicators** A more complete range of indicators as provided by Ta4J will be implemented.
* **more strategies** I will implement more trading strategies with the help of stockcharts chart school.
* **more refactoring** I tend to deliver early, let code evolve and refactor later as patterns emerge. This works well when trying new technologies, which I am doing all the time in the project.
* **multithreading** As this project expands and more stocks are added, performance will be addressed. I expect camel to provide a lot of the scalability via SEDA routes and other asynchronous patterns.
* **rest API tests** Spring testing is already used in some places and I will try and ensure that more of the code is tested.
* **integration tests** Spring support for integration testing is done for some controllers, but not all.
* **client interface** The skeleton is in place, but there are currently no methods for the client to control the server.
* **packaging** Being able to easily deploy this software to a server is something I want to provide. I will follow spring boot best practice where possible.
* **javadoc** Although this is an application and not library code, I still want to have a minimum level of documentation to make sure I understand it when I come back from any future break I decide to take.
* **semantic logging into elasticsearch** As well as the data, I want the entire application to log into elasticsearch too. This may be accompanied by a GrayLog2 implementation too.
* **logo and branding**

## What is going to be done in the long term?

* **DevOps** When functionally complete and stable, I want to ensure that this project is easily deployable into nginx, perhaps wrapped in a docker container and scaled via OpenShift. I would also like to look at a CI/CD pipeline that fabric8 could provide. This is a chunky piece of work and is likely to be combined into one effort alongside my associated *turbine* project when I feel everything is sufficiently complete.
* **config server** I want to have a look at externalising the configuration into a configuration server of some sort, perhaps spring-cloud-config-server.
* **connect to real market data** Funnily enough, this is not a priority just now as I want to focus on core functionality. I can do that just fine with fake data for now.
* **pricing** A very long term goal is to offer pricing for various instruments using JQuantLib. This opens up some interesting possibilities for further modeling, simulation and (in my wildest of dreams) maybe even arbitrage.
* **google protocol buffers** Maybe. Just maybe. Weak link seems to be elasticsearch - I would still have to provide JSON format for it.

## What is unlikely to be done?

* **replace spring boot** It is unlikely that I will replace spring boot in this project as it does so much for me just now. Having said that, things like [dropwizard](https://github.com/dropwizard/dropwizard) and [jodd](https://github.com/oblac/jodd) do look fascinating.
* **distributed processing** Things like Apache Spark look very interesting, but they remain beyond the horizon at the moment. I would rather have a look at Docker/Kubernetes first as that may be a more interesting and generalised scalability option.

## Where did the names come from?

The name was originally coined when this project was going to be a simple messaging performance test suite. However, it has since expanded into a full technical analysis suite. We can try and force the name *turbine* into a backronym, something like 'Technical Understanding Reached By Implementing New Executables', if we must. The names of the subprojects are meant to evoke interpretations of parts of an electricity generating turbine, although it doesn't really hold up to a pedantic analysis of the metaphor.

## Notes and issues...

I currently have a problem with elasticsearch sometimes hanging and not restarting after my laptop resumes from suspend (linux mint 18.1 xfce). No logs, nothing just hanging. After hacking about with the elasticsearch.sh I got this error out of it...

```
2017-03-30 18:12:03,009 main ERROR Could not register mbeans java.security.AccessControlException: access denied ("javax.management.MBeanTrustPermission" "register")
```

I *think* the solution might be [this suggestion](https://groups.google.com/forum/#!msg/hector-users/A9ybod1Ox_A/AH9-PcfZdlQJ), which is to add this line inside the `grant{}` block in the active `java.policy` file. For me, this is `/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/java.policy`...

```
grant {
  permission javax.management.MBeanTrustPermission "register";
}
```
