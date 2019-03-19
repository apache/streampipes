## StreamPipes Maven Archetype for custom Data Sources

### Usage

mvn archetype:generate                                  \
			-DarchetypeGroupId=org.streampipes                \
			-DarchetypeArtifactId=streampipes-archetype-pe-sources         \
			-DarchetypeVersion=0.60.2-SNAPSHOT                \
			-DgroupId=my.groupId \
			-DartifactId=my-source
			-DclassNamePrefix=MySource
			-DpackageName=mypackagename
			
### Variables

* classNamePrefix: Will be used as a prefix to name your controller & parameter classes
* packageName: Will be used as the package name

