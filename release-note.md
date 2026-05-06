# Release Note

## Problem

The Docker-built Sierra WAR could fail at startup with:

`Invalid resource name (algorithms/HIVDB_10.2.xml)`

The failure happened because `algorithms/versions.json` referenced HIVDB `10.2`, but the packaged `hivfacts-java` JAR inside `sierra.war` did not include `algorithms/HIVDB_10.2.xml`.

## Required Build Mode

Use the new Docker builder. Do not use the legacy builder.

Recommended command pattern:

```bash
docker buildx build --load -t hivdb/sierra:latest .
```

If you use Make targets, ensure BuildKit/buildx is enabled in your environment.

## Required Patch

Before building, apply the following diff in the `hivfacts` repo:

File:

`hivfacts/hivfacts-java/build.gradle`

Diff:

```diff
diff --git a/hivfacts-java/build.gradle b/hivfacts-java/build.gradle
index 913e050..15e133b 100644
--- a/hivfacts-java/build.gradle
+++ b/hivfacts-java/build.gradle
@@ -5,6 +5,8 @@ plugins {
 	id 'maven-publish'
 }
 
+import org.gradle.api.file.DuplicatesStrategy
+
 group = 'edu.stanford.hivdb.hivfacts'
 version = '2022.11'
 
@@ -13,6 +15,16 @@ java {
     targetCompatibility = JavaVersion.VERSION_25
 }
 
+def generatedResourcesDir = layout.buildDirectory.dir("generated/resources/main")
+
+sourceSets {
+	main {
+		resources {
+			setSrcDirs([generatedResourcesDir, 'src/main/resources'])
+		}
+	}
+}
+
 description = 'Amino acid / codon classification data of HIV-1 pol'
 
 dependencies {
@@ -31,7 +43,7 @@ repositories {
 
 task copyData(type: Copy, group: 'build') {
 	from '../data'
-	into 'src/main/resources/'
+	into generatedResourcesDir
 }
 
 jacocoTestReport {
@@ -41,13 +53,17 @@ jacocoTestReport {
 	}
 }
 
+processResources {
+	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
+}
+
 processResources.dependsOn copyData
-assemble.dependsOn copyData
 test.dependsOn copyData
 
 task sourcesJar(type: Jar) {
 	dependsOn classes
 	archiveClassifier = 'sources'
+	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
 	from sourceSets.main.allSource
 }
 ```

## Why This Patch Is Needed

- It stops the build from writing generated resources into `src/main/resources` during packaging.
- It moves copied HIV facts data into a generated build resource directory.
- It makes generated resources take precedence during packaging.
- It avoids Gradle duplicate-resource failures during `processResources` and `sourcesJar`.

## Build Steps

1. Apply the diff above in `hivfacts/hivfacts-java/build.gradle`.
2. Build Sierra with `docker buildx`.

Example:

```bash
docker buildx build --load -t hivdb/sierra:latest .
docker buildx build --load -t hivdb/sierra-dp:latest -f Dockerfile.DP .
```

## Verified Result

After applying the patch and rebuilding, the final image contains:

`WEB-INF/lib/hivfacts-java-2022.11.jar!/algorithms/HIVDB_10.2.xml`

This resolves the missing-resource startup failure.
