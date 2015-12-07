# the-turbine [![Build Status](https://api.travis-ci.org/the-james-burton/the-turbine.svg?branch=master)](https://travis-ci.org/the-james-burton/the-turbine)

![architecture](https://github.com/the-james-burton/the-turbine/blob/master/docs/architecture.png "architecture")

## What is it?

The turbine is a set of server-side components that perform automated technical analysis on stocks. It it not actually connected just yet to any real source of stock prices, instead, it currently generates random data so that the project can be further developed.

## What does it looks like?

*(Screenshots of Kibana to follow)*

It is broken up into three main modules...

* **furnace** Responsible for providing real-time stock prices. At the moment, this just generates random data.
* **condenser** Analyses the stock prices provided by the *furnace* and stores the results of that analysis into elasticsearch.
* **fuel** Shared domain objects and configuration. Library only - not executable
* **engine-hall** Just the parent pom and any other miscellany.

## How does it work?

0. **The furnace creates stock 'ticks'** These 'tick' objects are simple and contain just a high, low, open and close price, plus a few co-ordinates (market/symbol) and a timestamp. These ticks are currently generated at random using a simple algorithm to ensure continuity in the data.
0. **The furnace publishes the ticks to rabbitMQ** and that is the job of the furnace finished! It will do more work when it is connected to a source of real market data.
0. **The condenser receives the ticks** via the rabbitMQ camel component.
0. **The condenser does the indicator and strategy analysis** This is the meat of the work done by the condenser. It runs a suite of indicators (such as Bollinger bands) and trading strategies (such as CCI correction) and creates suitable objects for them.
0. **The condenser publishes the ticks and analysis to elasticsearch and websockets (via rabbitMQ)** Once it has completed its work, the condenser will save the incoming ticks and the analysis results to elasticsearch, where generic tools such as Kibana can be used to chart the results. It will also publish them to websockets, via the webstomp plugin from RabbitMQ so that my *atacama* web client can receive them as an inbound message.

## How do I use it?

The only way at present to get it up and running is to clone this repo, build it then run it in eclipse using the provided launch configurations. Make sure you have java 8, maven, rabbitMQ (with the webstomp plugin), elasticsearch and kibana installed then run something like the following...

```bash
git clone https://github.com/the-james-burton/the-turbine/the-turbine.git the-turbine
cd the-turbine
mvn clean install
```

Then open the project in Eclipse and run the launch configurations in *ide/eclipse/launch*. You need to start the condenser and then the furnace. If working, you should see logging activity as the furnace generates random stock ticks and the condenser analyses them. You should then browse to your kibana dashboard and see if you have any data in the *turbine-\** indicies.

At some point I will provide an easier way of installing and running this app using spring boot packaging best practice, but it remains a development project for now.

## What is it built on?

It is a multi-module maven project written in Java 8. Amongst others, it makes use of the following superb open source projects...

* **spring boot** I chose spring boot as my framework as it can superceed many bespoke parent POMs including my own.
* **apache camel** To provide muti-threaded EIP and easily connect to the other main technologies in this project.
* **Ta4J** The awesome 'technical analysis for java' project provides everything I need to do the hard work of actually analysing the stocks.

It communicates with two principal back end components...

* **elasticsearch** Using the spring-data-elasticsearch client. Elasticsearch is used as primary persistence in this project. There is no traditional RDBMS. Historic data can be fetched from elasticsearch for back-populating stock history if needed. Although my *atacama* client project communicates directly with elasticsearch, spring provides a very easy way to write a REST API to do similar if I need to.
* **rabbitMQ** As the AMQP backbone of the project, RabbitMQ is the interconnect between the *furnace* and the *condenser* and my *atacama* client (through the magic of the webstomp plugin).

## What is already done?

* **multi-module separation** There is reasonable separation of functionality into different modules. This will continue and be strengthened as I continue developing.
* **camel for all integration** Camel contols all flow and integration with external components.
* **unit tests** Not complete by any means, but JUnit, Mockito and Hamcrest are being used for effective unit testing.
* **api docs** Swagger is in place, delivered by the excellent springfox project. This gives me advanced APIs docs, although I don't see the API being very large for this project as 1) it is primarily a business application and 2) clients get the data from elasticsearch.

## What is going to be done soon?

* **more indicators** A more complete range of indicators as provided by Ta4J will be implemented.
* **more strategies** I will implement more trading strategies with the help of stockcharts chart school.
* **more refactoring** I tend to deliver early, let code evolve and refactor later as patterns emerge. This works well when trying new technologies, which I am doing all the time in the project.
* **multithreading** As this project expands and more stocks are added, performance will be addressed. I expect camel to provide a lot of the scalability via SEDA routes and other asynchronous patterns.
* **mvc tests** Spring testing is already used in some places and I will try and ensure that more of the code is tested.
* **integration tests** Spring support for integration testing is done for some controllers, but not all.
* **client interface** The skeleton is in place, but there are currently no methods for the client to control the server.
* **packaging** Being able to easily deploy this software to a server is something I want to provide. I will follow spring boot best practice where possible.
* **replace spring-data-elasticsearch?** That project appears to be falling behind and does not support ES 2.0. I may therefore be forced to look at alternatives, such as JEST.
* **javadoc** Although this is an application and not library code, I still want to have a minimum level of documentation to make sure I understand it when I come back from any future break I decide to take.
* **semantic logging into elasticsearch** As well as the data, I want the entire application to log into elasticsearch too. This may be accompanied by a GrayLog2 implementation too.
* **logo and branding**

## What is going to be done in the long term?

* **DevOps** When functionally complete and stable, I want to ensure that this project is easily deployable into nginx, perhaps wrapped in a docker container and scaled via OpenShift. I would also like to look at a CI/CD pipeline that fabric8 could provide. This is a chunky piece of work and is likely to be combined into one effort alongside my associated *turbine* project when I feel everything is sufficiently complete.
* **config server** I want to have a look at externalising the configuration into a configuration server of some sort, perhaps spring-cloud-config-server.
* **connect to real market data** Funnily enough, this is not a priority just now as I want to focus on core functionality. I can do that just fine with fake data for now.
* **pricing** A very long term goal is to offer pricing for various instruments using JQuantLib. This opens up some interesting possibilities for further modeling, simulation and (in my wildest of dreams) maybe even arbitrage.

## What is unlikely to be done?

* **replace spring boot** It is unlikely that I will replace spring boot in this project as it does so much for me just now.
* **distributed processing** Things like Apache Spark look very interesting, but they remain beyond the horizon at the moment. I would rather have a look at Docker/Kubernetes first as that may be a more interesting and generalised scalability option.

## Where did the names come from?

The name was originally coined when this project was going to be a simple messaging performance test suite. However, it has since expanded into a full technical analysis suite. We can try and force the name *turbine* into a backronym, something like 'Technical Understanding Reached By Implementing New Executables', if we must. The names of the subprojects are meant to evoke interpretations of parts of an electricity generating turbine, although it doesn't really hold up to a pedantic analysis of the metaphor.
