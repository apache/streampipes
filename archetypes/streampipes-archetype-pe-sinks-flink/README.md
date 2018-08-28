## StreamPipes Maven Archetype for Flink-based Sinks

### Usage

mvn archetype:generate                                  \
			-DarchetypeGroupId=org.streampipes                \
			-DarchetypeArtifactId=streampipes-archetype-pe-sinks-flink         \
			-DarchetypeVersion=0.55.3-SNAPSHOT                \
			-DgroupId=my.test.groupId \
			-DartifactId=my-test-artifact-id
			-DclassNamePrefix=MySink
			-DpackageName=mypackagename
			
### Variables

* classNamePrefix: Will be used as a prefix to name your controller & parameter classes
* packageName: Will be used as the package name

