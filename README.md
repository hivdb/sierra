# Sierra Project - HIVDB Genotypic Resistance Interpretation Program

![Sierra project][sierra-logo]

[![Travis CI](https://api.travis-ci.org/arodcasanova/sierra.svg?branch=develop)](https://travis-ci.org/arodcasanova/sierra)
[![codecov](https://codecov.io/gh/arodcasanova/sierra/branch/develop/graph/badge.svg)](https://codecov.io/gh/arodcasanova/sierra)
[![donation](https://img.shields.io/badge/Donate-Stanford_Giving-green.svg)][donation]

## Prerequisites

Here're the full prerequisites of Sierra project:

1. JDK 8 (tested with Oracle Java; maybe also work with OpenJDK);
2. Apache Tomcat for starting an HTTP server. If you just need to make small
   modifications and run the tests, you may not need Tomcat.
3. NucAmino Alignment Program. It is an open source protein-DNA alignment
   program written by Philip Tzou (philiptz(at)stanford.edu) of the Stanford
   HIVDB team. The paper of this program is available at
   [here][nucamino-paper]. One can retrieve the source code and binary file
   from [the GitHub repository][nucamino-github].

Following system environment is required for running sequence alignment:

- `NUCAMINO_PROGRAM`: path to the binary NucAmino program

## Dependency Lists

Full dependency list of each sub-project can be found in `build.gradle` file
under each sub-project directory. Here's an unfinished list of main
dependencies:

- [Apache Commons Lang][commons-lang]
- [Apache Commons Math][commons-math]
- [Apache Commons IO][commons-io]
- [Apache Log4j][log4j]
- [Google Guava][guava]
- [Google Gson][gson]
- [protonpack][protonpack]
- [GraphQL-Java][graphql-java]
- [MySQL Connector/J][connector-j] (dev only)
- [c3p0][c3p0] (dev only)
- [junit][junit] (dev only)

Note: the `DrugResistance` sub-project also depends on an open source project
named [ASI][asi-github] created by us and [Frontier Science][fstrf]. The
binary jar file is located in `DrugResistance/lib` directory and is configured
to be installed with other dependencies when runing Gradle.


## Installation

### Start Sierra with Docker

Docker images are released publicly for each version of Sierra since 2.2.6.
To pull and start a Sierra instance:

    docker pull hivdb/sierra:latest
    docker run -it --publish=8080 hivdb/sierra dev

After started the instance, the local Sierra web service is accessable
through this URL:

    http://localhost:8080/WebApplications/rest/graphql


### Development with Eclipse IDE

Sierra project uses Gradle to manage the dependencies, build and test. The
easiest way to install the whole project is through Eclipse. Here're the steps:

1. Use Git to clone this repository to a local path;
2. In Eclipse, click "File" > "Import...", expand the "Gradle" group, select
   "Gradle Project", then click "Next >";
3. Eclipse may show a welcome dialog before it starting the import wizard.
   After the welcome dialog, type sierra local path from step #1 into
   "Project root directory". Click "Next >";
4. Use all default configurations until finish dialog. Click "Finish" then you
   have installed sierra in Eclipse.

There may be an issue that you can't find "Gradle Project" in step #2. In this
case you can install the latest "Buildship Gradle Integration" with "Help" >
"Eclipse Marketplace...". then you can import a Gradle project in Eclipse.

### Console installation

Gradle Wrapper is shipped with this repository. One can easily install
dependencies from Bash or Windows command line.

#### Bash

```bash
cd path/to/sierra
# initialize the hiv-genotyper submodule (first setup only)
git submodule init && git submodule update
# any gradle command will trigger downloading of dependencies
./gradlew assemble
```

#### Windows command line (batch)

```winbatch
cd path\to\sierra
# initialize the hiv-genotyper submodule (first setup only)
git submodule init
git submodule update
gradlew.bat assemble
```

## Donation

If you find Sierra useful and wish to donate to the HIVDB team, you can do
so through [Stanford Make a Gift][donation] form. Your contribution will be
greatly appreciated.


[sierra-logo]: http://hivdb.stanford.edu/DR/webservices/images/Sierra.gif
[nucamino-paper]: https://hivdb.stanford.edu/pages/pdf/Tzou.2017.BMCBioinformatics.pdf
[nucamino-github]: https://github.com/hivdb/nucamino
[deployment]: https://github.com/hivdb/hivdb-deployment
[gradle]: http://gradle.org/
[homebrew]: http://brew.sh/
[commons-lang]: https://commons.apache.org/proper/commons-lang/
[commons-math]: https://commons.apache.org/proper/commons-math/
[commons-io]: https://commons.apache.org/proper/commons-io/
[log4j]: http://logging.apache.org/log4j/
[connector-j]: https://dev.mysql.com/downloads/connector/j/
[guava]: https://github.com/google/guava
[c3p0]: http://www.mchange.com/projects/c3p0/
[gson]: https://github.com/google/gson
[protonpack]: https://github.com/poetix/protonpack
[junit]: http://junit.org/junit4/
[graphql-java]: https://github.com/graphql-java/graphql-java
[asi-github]: https://github.com/FrontierScience/asi_interpreter
[fstrf]: https://www.fstrf.org/
[donation]: https://giving.stanford.edu/goto/shafergift
