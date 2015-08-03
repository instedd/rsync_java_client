[![Build Status](https://travis-ci.org/instedd/rsync_java_client.svg?branch=master)](https://travis-ci.org/instedd/rsync_java_client)

rsync_java_client
======

Welcome to rsync_java_client. This repository contains library code to develop rsync-based file synchronization clients.

# Building the code

You will need Apache Maven installed, and a JDK8 or newer.

Just run:

```
mvn install
```

If you dont want to wait for tests, do:

```
mvn install -Dmaven.test.skip=true
```

If you want to import into Eclipse, just run:

```
mvn eclipse:eclipse
```
