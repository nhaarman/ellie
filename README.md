# Ellie [![Build Status](https://travis-ci.org/nhaarman/ellie.svg?branch=0.0.3)](https://travis-ci.org/nhaarman/ellie) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.nhaarman.ellie/core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.nhaarman.ellie/core) [![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/nhaarman/ellie?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![In Progress](https://badge.waffle.io/nhaarman/ellie.svg?label=in-progress&title=In Progress)](http://waffle.io/nhaarman/ellie)
[![Ready for Release](https://badge.waffle.io/nhaarman/ellie.svg?label=next-release&title=Ready for Release)](http://waffle.io/nhaarman/ellie)

Compile-time active record ORM for Android.

Ellie is a heavily modified fork from [Ollie](https://github.com/pardom/ollie), with a few important modifications:

 - Calls to the main `Ellie` class are not static;
 - Moved database logic out of Model classes into ModelRepository classes;

Download
--------

Grab via Maven:

```xml
<dependency>
  <groupId>com.nhaarman.ellie</groupId>
  <artifactId>core</artifactId>
  <version>x.x.x</version>
</dependency>
<dependency>
  <groupId>com.nhaarman.ellie</groupId>
  <artifactId>compiler</artifactId>
  <version>x.x.x</version>
  <optional>true</optional>
</dependency>
```

or Gradle:

```groovy
compile 'com.nhaarman.ellie:core:x.x.x'
provided 'com.nhaarman.ellie:compiler:x.x.x'
```

Build
-----

To build:

```
$ git clone git@github.com:nhaarman/ellie.git
$ cd ellie/
$ ./gradlew build
```

Debugging:

Add the following to ~/.gradle/gradle.properties, and run `./gradlew daemon`.

```
org.gradle.daemon=true
org.gradle.jvmargs=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
```

Next, in IntelliJ, add a Remote Run Configuration and start debugging. When building the project (`gradlew build`), the debugger will halt at breakpoints in the annotation
processor.

License
=======

    Copyright 2014 Michael Pardo
    Copyright 2014 Niek Haarman

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
