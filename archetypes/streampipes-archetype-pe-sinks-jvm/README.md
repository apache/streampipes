## StreamPipes Maven Archetype for Standalone Sinks

### Usage

mvn archetype:generate                                  \
			-DarchetypeGroupId=org.streampipes                \
			-DarchetypeArtifactId=streampipes-archetype-pe-sinks-jvm          \
			-DarchetypeVersion=0.60.2-SNAPSHOT                \
			-DgroupId=my.groupId \
			-DartifactId=my-sink-jvm
			-DclassNamePrefix=MySink
			-DpackageName=mypackagename
			
### Variables

* classNamePrefix: Will be used as a prefix to name your controller & parameter classes
* packageName: Will be used as the package name

