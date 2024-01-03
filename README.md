# About
A sample Spring Boot 3 and Infinispan 14 integration. A multiple instances of Infinispan is installed, and connecting to each other to form a cluster.

The goal is to replicate a multiple `putIfAbsent` on a replicated cache in Infinspan 14. 

## Custom Log4j to enable HotRod Access Log
```xml
    <!-- Set to TRACE to enable access logging for Hot Rod requests -->
    <Logger name="org.infinispan.HOTROD_ACCESS_LOG" additivity="false" level="TRACE">
      <AppenderRef ref="HR-ACCESS-FILE"/>
    </Logger>
```

## Running Infinispan
Run the first instance
```
$ docker run -p 11222:11222 -v "/tmp/log4j2.xml":/opt/infinispan/server/conf/log4j2.xml \ 
        -e USER=admin -e PASS=password \ 
        --add-host=HOST:192.168.56.1 \
         infinispan/server:14.0.2.Final
```

Run the second instance
```
$ docker run -p 11223:11222 -v "/tmp/log4j2.xml":/opt/infinispan/server/conf/log4j2.xml \ 
        -e USER=admin -e PASS=password \ 
        --add-host=HOST:192.168.56.1 \
         infinispan/server:14.0.2.Final
```

Run the third instance
```
$ docker run -p 11224:11222 -v "/tmp/log4j2.xml":/opt/infinispan/server/conf/log4j2.xml \ 
        -e USER=admin -e PASS=password \ 
        --add-host=HOST:192.168.56.1 \
         infinispan/server:14.0.2.Final
```

## Creating Cache
create `user-cache` with below configuration
```xml
<?xml version="1.0"?>
<replicated-cache name="user-cache" mode="SYNC" remote-timeout="30000" statistics="true">
	<encoding>
		<key media-type="text/plain"/>
		<value media-type="text/plain"/>
	</encoding>
	<locking concurrency-level="1000" isolation="READ_COMMITTED" acquire-timeout="60000" striping="false"/>
	<transaction mode="NON_XA" auto-commit="true" stop-timeout="30000" locking="OPTIMISTIC" reaper-interval="30000" complete-timeout="30000" notifications="true"/>
	<state-transfer timeout="30000"/>
</replicated-cache>
```