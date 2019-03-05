## StreamPipes Maven Archetype for Standalone Processors

### Usage

mvn archetype:generate                                  \
			-DarchetypeGroupId=org.streampipes                \
			-DarchetypeArtifactId=streampipes-archetype-pe-processors-jvm          \
			-DarchetypeVersion=0.60.2-SNAPSHOT               \
			-DgroupId=my.groupId \
			-DartifactId=my-processor-jvm
			-DclassNamePrefix=MyProcessor
			-DpackageName=mypackagename
			
### Variables

* classNamePrefix: Will be used as a prefix to name your controller & parameter classes
* packageName: Will be used as the package name

