Updating CFLint library

1. After updating repository, run 'mvn clean package' inside the repository's root folder. (This builds the runnable jar)
2. Deploy 'target/CFLint-x.x.x.jar' to Artifactory inside 'libs-release-local'.
3. After updating the artifact, change the build.gradle CFLint version 
	(e.g. "runtime group: 'com.cflint', name: 'CFLint', version: 'x.x.x'").

No other changes should be needed inside the build.gradle file.