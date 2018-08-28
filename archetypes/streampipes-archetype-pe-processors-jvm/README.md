## StreamPipes Maven Archetype for Standalone Processors

### Usage

mvn archetype:generate                                  \
			-DarchetypeGroupId=org.streampipes                \
			-DarchetypeArtifactId=streampipes-archetype-pe-processors-jvm          \
			-DarchetypeVersion=0.0.1-SNAPSHOT                \
			-DgroupId=my.test.groupId \
			-DartifactId=my-test-artifact-id
			-DclassNamePrefix=MyProcessor
			-DpackageName=mypackagename
			
### Variables

* classNamePrefix: Will be used as a prefix to name your controller & parameter classes
* packageName: Will be used as the package name

