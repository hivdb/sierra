# Sierra Project - HIVDB Genotypic Resistance Interpretation Program

<img alt="Sierra project" src="https://cms.hivdb.org/prod/images/slide-sierra.png" width="200">

[![JAVA CI with Gradle](https://github.com/hivdb/sierra/actions/workflows/gradle.yml/badge.svg)](https://github.com/hivdb/sierra/actions/workflows/gradle.yml)
[![CodeQL](https://github.com/hivdb/sierra/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/hivdb/sierra/actions/workflows/codeql-analysis.yml)<!--
[![codecov](https://codecov.io/gh/hivdb/sierra/branch/master/graph/badge.svg)](https://codecov.io/gh/hivdb/sierra)-->
[![donation](https://img.shields.io/badge/Donate-Stanford_Giving-green.svg)][donation]

## Prerequisites

Here're the full prerequisites of Sierra project:

1. JDK 11 (tested with Oracle Java; maybe also work with OpenJDK);
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
    docker run -it --publish=8080:8080 hivdb/sierra dev

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

### NucAmino on AWS Lambda

The sequence alignment program NucAmino is very compute-intensive. By default,
Sierra runs sequence alignment on local NucAmino instances. In the case of a
few thousands of sequences, the CPU load can easily become very high during the
process.

In the version 2.2.8, we introduce a new way to run NucAmino remotely on AWS
Lambda. AWS Lambda is an event-driven, serverless computing platform provided
by Amazon as a part of the Amazon Web Services. This platform allows user to
take the advantage of [MapReduce](https://en.wikipedia.org/wiki/MapReduce) to
process a large amount of data. The Sierra 2.2.8 is able to dispatch alignment
tasks to several Lambda instances and aggregate the results.

#### Usage

To enable this feature in your Sierra instance, you need first to create an AWS
function from a zip file created for NucAmino:

```bash
git clone https://github.com/hivdb/nucamino.git
cd nucamino
make aws_lambda_zip
```

Please follow the [README file][nucamino-github] of NucAmino for prerequisites.
The zip file should be located at `build/nucamino-aws-lambda.zip`.

Please follow the [AWS Lambda Document][lambda-create] to create a new Python
3.6 function. You can use any name and qualifier for this function.

To run a Sierra instance using the remote NucAmino, following system
environments are required/recommended:

```bash
# specify the AWS Lambda function name and qualifier (both required)
NUCAMINO_AWS_LAMBDA="<FUNCTION_NAME>:<FUNCTION_QUALIFIER>"

# specify AWS Region using `~/.aws/config` or this variable
AWS_REGION="us-west-1"

# specify AWS credentials using `~/.aws/credentials` or these variables
AWS_ACCESS_KEY_ID=
AWS_SECRET_ACCESS_KEY=
```

If you are using the Docker instance to run Sierra:

```bash
docker run \
  -it \
  -e "NUCAMINO_AWS_LAMBDA=<FUNCTION_NAME>:<FUNCTION_QUALIFIER>"
  -e "AWS_REGION=us-west-1"
  -e "AWS_ACCESS_KEY_ID=<AWS_ACCESS_KEY_ID>"
  -e "AWS_SECRET_ACCESS_KEY=<AWS_SECRET_ACCESS_KEY>"
  --publish=8080 hivdb/sierra prod
```

You can also use AWS IAM to provide further access control. Please search
IAM documents for further information.

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
[lambda-create]: https://docs.aws.amazon.com/lambda/latest/dg/get-started-create-function.html
