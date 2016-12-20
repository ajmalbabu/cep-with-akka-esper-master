
# cep-impl1-master

## Features implemented so far

1. Each sections below showcase an end-to-end scenario of how to use a specific technology stack. And there is a general section later that explains some of the best practices adopted in unit testing, spring boot usage etc.

### Basic setup and bring up the application.

1. This project runs using standard Spring boot and Maven with maven dependency management best practice implemented. It has dependency to REDIS, Cassandra & (Future - Postgres, Kafka) but one can start and checkout the application without having any of these external available in a local laptop. This helps developers to get started immediately and play around with the application without hassles.
2. Open the project in your editor of choice (IntelliJ is recommended) as a maven project.
3. **key** step. Two milestone release jar files from spring data is bundled along with the project. These milestone are needed to work with Cassandra 3.0 version. And these milestone will be general release in two months. Meanwhile these two files needs to be installed in your local maven repository manually by executing the below commands.
    1. These files are present in the milestone-repo folder go to that folder before executing the below commands. 
    2. mvn install:install-file -Dfile=spring-data-cassandra-1.5.0.M1.jar -DgroupId=org.springframework.data -DartifactId=spring-data-cassandra -Dversion=1.5.0.M1 -Dpackaging=jar
    3. mvn install:install-file -Dfile=spring-cql-1.5.0.M1.jar -DgroupId=org.springframework.data -DartifactId=spring-cql -Dversion=1.5.0.M1 -Dpackaging=jar
    4. Details on spring milestone release here http://stackoverflow.com/questions/38978491/spring-boot-1-4-spring-data-cassandra-1-4-2-is-incompatible-with-cassandra-3-0/38978826#38978826
3. The project will be imported as a Maven project, below single step configuration can start the application in a laptop.
4. Run `cep-api/src/main/java/com/cep/api/Application` 
    1. Then edit configuration and provide the following values according to the local path on your machine and rerun the application again
    ```
    -Xbootclasspath/p:C:\software\workspace\cep-impl1-master\cep-impl1-configuration
    -Dspring.profiles.active=local,isolate
    ```

### Fan-out using AKKA actors usage example.

1. This scenario showcase how to implement an end-to-end asynchronous request response scenario using spring REST & actors.
2. When a fan out generate random request is submitted a job gets executed asynchronously by actors and coordinator actor collects the results as and when generators send results to the coordinator. 
3. The above random-generate job when submitted will return a job ID back and that ID would be the ID of the coordinator actor who coordinates the results asynchronously. The ID gets stored in a cache with the complete actor information for later access. This actor could be a remote actor running on any remote machine. When a subsequent request appear for fetch the results (fan-out-random-generate-response). The actor details is fetched back from cache using the ID and can communicate with that actor for the results.
4. Hit the GET url http://127.0.0.1:8082/cep-api/v1/fan-out-random-generate?count=5 this will submit to generate 5 random numbers. 5 actors will be created and each one generates a random number and result will be send to a coordinator actor and the coordinator actor sends the result back to the caller. This call would return the ID for the submitted request.
5. Hit the GET url http://127.0.0.1:8082/cep-api/v1/fan-out-random-generate-response?id=8fa3bb58-8f5c-4d34-ad75-69d388088d1f This call would return the results from the previous generate submission.
    ```
        Where 'id' is the 'id' returned from the previous call
    ``` 
6. The ID is stored in a local in-memory cache using spring cache abstraction for local development. For production the cache could be replaced by REDIS. To locally use REDIS instead of in-memory cache, the instructions are provided below.
7. Checkout junit test case for this example as well.

### AKKA persistence usage example

1. This scenario showcase how to use AKKA persistence actors along with spring.
1. AKKA persistence actor example is implemented with below example. (persistence actors provide distributed actors with identity). A BankAccount persistent actor can be created by sending the following HTTP POST request
```
http://127.0.0.1:8082/cep-api/v1/bankAccount
with below json payload - make sure to set the HTTP header "Content-Type" to "application/json" in postman
{  
   "name":"Jon",
   "bankTransaction":{  
      "bankTransactionType":"DEPOSIT",
      "transactionAmount":100
   }
}
```
2. To add more money to above bank account, send the following HTTP PUT request.
```
http://127.0.0.1:8082/cep-api/v1/bankAccount
with below json payload - make sure to set the HTTP header "Content-Type" to "application/json" in postman 
{  
   "name":"Jon",
   "bankTransaction":{  
      "bankTransactionType":"DEPOSIT",
      "transactionAmount":100
   }
}
```
3. To retrieve the current balance perform REST GET request
```
http://127.0.0.1:8082/cep-api/v1/bankAccount?bankAccountName=Jon
```
4. Persistence actors are using LevelDB as persistent storage, it can be changed by running a local Cassandra and update/uncomment cep-akka.conf file to point to cassandra.
5. Persistence actors has a bunch of unit-test cases and those one uses in memory persistence storage. Checkout the test cases.
6. Refer to AKKA cluster shard later. 

### isolate profile explained.

1. System can use REDIS (for cache) and Cassandra (as database, coming more on this later). But it can work without having both of these instance available and it will use an in-memory cache and none of the cassandra database access code will not work but application will come up and all other parts are available for access. Its possible because system is configured by default to run with **isolate** profile in local laptop and this is set during system startup look at start up parameters. This mode can be removed by taking out **isolate** profile during startup once REDIS and Cassandra are installed. See the steps later. 

### REDIS installation

1. REDIS for microsoft windows - download and install MSI  https://github.com/MSOpenTech/redis/releases this would automatically start REDIS as a windows service at localhost:6379. To change any of the default configuration read the documentation that comes with installation "Windows Service Documentation.docx"
2. REDIS can be accessed using Jedis/lettuce API Jedis jar is packaged along with the application and a redisTemplate with name 'primaryRedisTemplate' is configured and ready to use. 

### Cassandra example usage.

1. This scenario showcase how to access data from real Cassandra.
2. Data is stored in cassandra to do that, Install cassandra 3.0.8 from http://www.planetcassandra.org/archived-versions-of-datastaxs-distribution-of-apache-cassandra/ start cassandra as windows service.
3. Open Cassandra CQL shell create a keyspace and table and insert data as below
```
    CREATE KEYSPACE cep  WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
    use cep;
    create table flight (
      flightKey varchar primary key,
      carrierCode varchar,
      flightNumber int,
      flightDate date,
      departureAirport varchar,
      arrivalAirport varchar,
      departureTime timestamp,
      arrivalTime timestamp,
      confirmationNumbers Set<varchar>,
      aircraftTailNumber varchar
    );
    insert into flight (flightKey , carrierCode,flightNumber, flightDate, departureAirport,arrivalAirport,departureTime,arrivalTime,confirmationNumbers, aircraftTailNumber  )
    values ('WN-101-2016:01:01-MSP-DAL','WN', 101, '2016-01-01','MSP' ,'DAL', '2016-01-01 09:00:00', '2016-01-01 12:00:00',{'CZ67YT'}, 'AC1');
    select * from flight;
```
4. Cassandra end point can be tested by issuing a HTTP get request http://127.0.0.1:8082/cep-api/v1/flight
5. Once Cassandra & REDIS is running in the above PORT, restart the Application by removing "isolate" profile in the start-up JVM arguments. Now Spring would connect to REDIS & Cassandra severs instead of in-memory cache & with  real column families. A log would appear in the application log that says connecting to REDIS cache.

### AKKA cluster shard using Cassandra persistence.
1. The samples we saw earlier in AKKA persistence usage, all such actors are created by taking advantage of AKKA cluster & cluster shard.
2. When application runs on single node AKKA cluster comes up and all the bank account persistence actors are created in this cluster node using cluster shard. 
3. To run multi-node cluster and enable cluster shard to enable creation on sharded nodes.
    1. Make sure to enable cassandra persistence storage; uncomment cassandra section in **cep.akka.conf** and comment the level-db section. Also disable isolate profile from spring startup.
    1. Change the cep-configuration/cep-akka.conf file following property manually **node.host** and **node.port** to the port number of specific cluster node. Also change spring jetty ports **server.port** & **management.port** in spring /application*env.yaml file. 
    2. Or provide the above values during application start-up using java **-D** option for e.g. **-Dnode.host=127.0.0.1** **-Dnode.port=2552** e.g. of a complete value is here **-Dnode.host=127.0.0.1 -Dnode.port=2552 -Dserver.port=8084 -Dmanagement.port=8085**
    3. To change the seed nodes update cep-configuration/cep-akka.conf **node.seed-nodes** section.

### Cassandra persistence configuration useful notes
1. The Correct Github URL for this project is https://github.com/akka/akka-persistence-cassandra 
2. A sample Cassandra akka conf is present here, which can be overridden for the only needed values. https://github.com/akka/akka-persistence-cassandra/blob/v0.17/src/main/resources/reference.conf
3. If old messages need to be deleted best option is 
    1. Login to CQL shell and execute below commands
    2. use akka;
    3. truncate messages;
    4. use akka_snapshot;
    5. truncate snapshots;
    6. All CQL used by Cassandra storage can be found here https://github.com/krasserm/akka-persistence-cassandra/blob/cassandra-3.x/src/main/scala/akka/persistence/cassandra/journal/CassandraStatements.scala#L16-L31
    7. Default table names are here https://github.com/krasserm/akka-persistence-cassandra
4. If messages need to be deleted in actor and remove from journal, to avoid replay, follow this approach.  http://doc.akka.io/docs/akka/2.4.9/scala/persistence.html#Message_deletion

### Kafka example usage and installation on windows

1. This section show-case how to use Kafka producer and consumer. 
2. To install Kafka on windows first Install 7-zip
3. unzip kafka binary from https://www.apache.org/dyn/closer.cgi?path=/kafka/0.10.0.0/kafka_2.11-0.10.0.0.tgz
4. unzip it start zookeeper and kafka and assume the installation folder is C:\software\kafka_2.11-0.10.0.0
5. start zookeeper
```
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
```
6. start kafka server
```
bin\windows\kafka-server-start.bat config\server.properties
```
7. Create a topic named 'flightEventTopic'
```
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic flightEventTopic
```
8. List the created topic
```
bin\windows\kafka-topics.bat --list flightEventTopic --zookeeper localhost:2181
```
9. Submit the below HTTP POST URL to submit a flight message into the system, this message from API layer will be send to the above Kafka Topic and there is another listener that listens for this message and prints it on the console.
```
http://127.0.0.1:8082/cep-api/v1/flight
with below json payload - make sure to set the HTTP header "Content-Type" to "application/json" in postman
{  
   "flightKey":"200-2016-01-01-DAL-SFO",
   "flightMessage": "<FightCancel>bla bla bla content..../FightCancel>"
}
```
10. A message appears of producing and consuming the message.
11. **Note** Spring kafka client (http://docs.spring.io/spring-kafka/docs/1.1.0.M1/reference/html/index.html) is tried out but it seems too complex to use, first they don't have enough documentation in samples. But tried and made it to work but removed that from codebase. Direct Kafka API is straight-forward. Spring tries to take the same approach as JMS listener for Kafka and that seems like a leaky abstraction to fit Kafka into it. Currently at-mos-once client example is implemented using Kafka clients, other at-least-once and exactly-once can be easily implemented using the examples from https://github.com/ajmalbabu/kafka-clients
12. Kafka unit and integration testing - Refer to **Other best practice** section.

### Other best practices

1. Supports spring Profiles concept, default profile can be extended with "local", "dev", "qa" or "prod" profiles.
    1. Spring profile configuration are supplied in the JVM startup argument. For local laptop development in isolation mode, provide the following profiles "local, isolate" as explained earlier in startup arguments.  All the default spring beans (which does not have any @Profile annotation) gets included in start-up and any beans with @Profile("local") & @Profile("isolate") gets included as well. 
    2. The corresponding configuration file for local profile file is cep-configuration/application-local.yaml file.
    3. If certain bean need to be only activated during a certain profile execution it can be controlled via @Profile annotation. Such an example is available in CacheManagerConfiguration.java
    4. Refer to isolate profile explained section above for laptop based isolate development.
2. Supports TDD using Spring and Junit along with integration test cases using spring integration test support to test end to end. Example test classes are available in the "test" folder. 
    1. Another important unit test feature available is to avoid duplicating of source code & resources file during unit testing. If module A depends on module B and module B's test folder has source code and configuration file needed for B's unit testing, all such configuration and code in test from module B is available to module A unit testing without duplicating those files into module A test folder. This is possible by declaring "test-jar" at "test" scope dependency in module A. An example can be found at cep-api's pom file for a dependency into cep-service module.  
    2. Cassandra unit testing is challenging as the cassandra-unit test libraries is inactive. All Cassandra testing is performed using mockito by mocking Dao code. Look at FlightServiceTest.java
    3. Kafka end to end testing is performed using an embedded Kafka server - For an end to end example refer to FlightEventTest and the comments in that test case.
    4. **mvn clean test** only runs unit test where as **mvn clean install** or **mvn clean verify** runs integration tests. Integration tests take longer to run, also sometimes it fails for no specific reasib (example kafka embedded test) so run again.
3. Logging is controlled through slf4j and logback. There is a default logback.xml that provides console and file appender. The logback file also supports MDC: transactionId to assign a unique transactionId for a request, this is controlled through 'transactionId' parameter. Caller can pass a HTTP header with 'transactionId' as key and a value, if none is provided, cep would create a new transactionId for that request and use that during logging wherever that request thread goes.
4. Actors run on arbitrary threads, hence the transactionId that is passed along to actor need to be set at beginning of actor execution to get MDC feature. Plumbing piece are put together to convert transactionId that is settable from actor. Refer to RandomService.java & RandomGeneratorActor.java for an example of how to set the transactionId correctly before actors are invoked. 
5. AKKA is customized to work with Spring, so that AKKA actors are created as spring bean and hence spring properties can be injected to AKKA actors. Core classes to achieve this are in cep-common/actor package refer to SpringActorProducer, SpringExtension, ParameterInjector & Parameters. 
6. A default AKKA configuration is provided in 'cep.akka.conf' file this file controls the AKKA logging and thread pools used by actors created by cep. 
7. Provide these additional parameters for log file location and to use file appender, by default console appender is used in laptop and these values does not matter, these values will be read by LoggerStartupListener.java and converted back to variables referred within logback.xml
    ```
        -Dlog.dir=C:\temp
        -Dlog.name=cep
    ```

### Modules
1. `cep-api` - [Readme](./cep-api/README.md)
2. `cep-common` - [Readme](./cep-common/README.md)
3. `cep-configuration` - [Readme](./cep-configuration/README.md)
4. `cep-service` - [Readme](./cep-service/README.md)

### Todo

1. Cleanup maven - remove unused artifacts - DONE
2. Implement isolate profile for local and unit-testing support in all these below  without having hard dependency on any of the below for local development. - DONE
3. Add Cassandra. - DONE
4. Add REDIS as Cache storage - DONE
5. Add Kafka. - DONE  
6. Add unit & integration test support for Kafka by using embedded kafka - DONE
7. Implement AKKA Cluster & cluster sharding. - DONE
8. Add Akka persistence with Junit Testing. - DONE
9. Change Persistence storage to Cassandra instead of Level-db when needed - DONE
10. Consideration for out of order processing - e.g. flight cancel came first and then came flight time update. (depend on the timestamp of message?). 


#### Other Todo

1. Implement timeout for remote calls database, cache, redis, cassandra, kafka etc.
2. Implement throttling using netflix hysterix.
3. Remove default serializers in AKKA.
4. When flight event is published to Kafka and if Kafka is not running at that time, it is blocking the REST request.
5. TODO Assign a thread-pool for Kafka listeners and use threads from there instead of using random threads. FlightEventListener.java
6. Version change handling, if persistence actor structure changes for a release, follow the version handling approaches discussed in the anomaly detector architecture document.
7. Add mechanism to close old/unused actors in each generate re-submission
8. Use Kafka storage instead of Cassandra? If Cassandra is used, to clean up/remove anomaly persistence actor, send a message that would trigger delete entries up to last one and send a ShutDown message to itself on completeion of the previous step of delete (http://doc.akka.io/docs/akka/current/scala/persistence.html#Safely_shutting_down_persistent_actors).
9. Use Gemfire instead of Cassandra for storing aggregates.
10. Update of passenger on flight for the confirmation number updates can be controlled through a singleton actor to avoid concurrency issues. Read more @ TODO on PassengerOnFlightEventListener.java 


### Clustering with cluster sharding TODO
=========================================

1. If there are 6 nodes, make sure at least 4 (more than 1/2 of total nodes) are available to form a cluster. This avoid split brain. But a three way split can kill the whole cluster.
2. configure  akka.cluster.min-nr-of-members = 4 in the above case.
3. Each member node listens for the Cluster.registerOnMemberUp & possibly Down messages.
    1. If it receives a node Up messages, it checks if the total number of nodes in its cluster is < 4 then ignore the message does not start actor system, does not start EntityRegistar, does NOT open up the api end point available such as any HTTP or Messaging end point.
    2. If it receives a node Up messages, it checks if the total number of nodes in its cluster is >= 4 then it starts if needed actor system, starts EntityRegistrar actor which in turn would start registered actors, and opens the api end point available for traffic such as any HTTP or Messaging end point.
    3. If it receives a node down messages, it checks if the total number of nodes in its cluster is >= 4 then ignore the down message.
    4. If it receives a node down messages, it checks if the total number of nodes in its cluster is < 4 then shuts down its actor system and shuts down EntityRegistrar its the api end point as NOT available - end point such as any HTTP or Messaging end point.

Roland discussion - https://groups.google.com/forum/#!searchin/akka-user/split$20brain/akka-user/UBSF3QQnGaM/JzFOzQc8NigJ
