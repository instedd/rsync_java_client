cdx-sync-client
===============

Welcome to cdx-sync-client. This repository contains library code to develop rsync-based file synchronization clients. 

This repository just contains client code, server code can be found [here](https://github.com/instedd/cdx-sync-server). 

# Building the code

You will need Apache Maven installed, and a JDK7 or newer. 

Just run:

```
mvn install
```

If you dont want to wait for tests, do:

```
mvn instal -Dmaven.test.skip=true
```

If you want to import into Eclipse, just run:

```
mvn eclipse:eclipse
```

# Running a sync app

This client contains also a simple application that does sync'ing. It is builded automatically during the package phase. Run it this way:

```
java -jar target/cdxsync src/test/resources/cdxsync.properties
```

