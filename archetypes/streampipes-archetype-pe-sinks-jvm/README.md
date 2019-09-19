<!--
  ~ Copyright 2018 FZI Forschungszentrum Informatik
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ you may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  -->

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

