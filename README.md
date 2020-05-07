<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  -->
[![Github Actions](https://img.shields.io/github/workflow/status/apache/incubator-streampipes-extensions/Build%20and%20Deploy%20Extensions)](https://github.com/apache/incubator-streampipes-extensions/actions/)
# Apache StreamPipes

Apache StreamPipes (incubating) enables flexible modeling of stream processing pipelines by providing a graphical
 modeling
 editor on top of existing stream processing frameworks.

It leverages non-technical users to quickly define and execute processing pipelines based on an easily extensible 
toolbox of data sources, data processors and data sinks. StreamPipes has an exchangeable runtime execution layer and executes pipelines using one of the provided wrappers, e.g., for Apache Flink or Apache Kafka Streams.

Pipeline elements in StreamPipes can be installed at runtime - the built-in SDK allows to easily implement new 
pipeline elements according to your needs. Pipeline elements are standalone microservices that can run anywhere - centrally on your server, in a large-scale cluster or close at the edge.

Find the core project at [https://github.com/apache/incubator-streampipes](https://github.com/apache/incubator-streampipes)

Learn more about StreamPipes at [https://streampipes.apache.org/](https://streampipes.apache.org/)

Read the full documentation at [https://streampipes.apache.org/docs](https://streampipes.apache.org/docs)

### StreamPipes Pipeline Elements

This project provides a library of several pipeline elements that can be used within the Apache StreamPipes toolbox.

See [https://streampipes.apache.org/docs/docs/pipeline-elements/](https://streampipes.apache.org/docs/docs/pipeline-elements/) 
for an overview of currently available pipeline elements.

Contact us if you are missing some pipeline elements!

### Getting started

See the installation guide at [https://streampipes.apache.org/download.html](https://streampipes.apache.org/download.html)

It's easy to get started:
* Download the installer script from [https://github.com/apache/incubator-streampipes-installer](https://github.com/apache/incubator-streampipes-installer)
* Follow the installation guide at [https://streampipes.apache.org/docs/docs/user-guide-installation](https://streampipes.apache.org/docs/docs/user-guide-installation)
* Check the [tour](https://streampipes.apache.org/docs/docs/user-guide-tour) and build your first pipeline!

## Bugs and Feature Requests

If you've found a bug or have a feature that you'd love to see in StreamPipes, feel free to create an issue in our Jira:
[https://issues.apache.org/jira/projects/STREAMPIPES](https://issues.apache.org/jira/projects/STREAMPIPES)

## Get help

If you have any problems during the installation or questions around StreamPipes, you'll get help through one of our 
community channels:

- [Slack](https://slack.streampipes.org)
- [Mailing Lists](https://streampipes.apache.org/mailinglists.html)

And don't forget to follow us on [Twitter](https://twitter.com/streampipes)!

## Contribute

We welcome contributions to StreamPipes. If you are interested in contributing to StreamPipes, let us know! You'll
 get to know an open-minded and motivated team working together to build the next IIoT analytics toolbox.

Here are some first steps in case you want to contribute:
* Subscribe to our dev mailing list [dev-subscribe@streampipes.apache.org](dev-subscribe@streampipes.apache.org)
* Send an email, tell us about your interests and which parts of Streampipes you'd like to contribute (e.g., core or UI)!
* Ask for a mentor who helps you understanding the code base and guides you through the first setup steps
* Find an issue in our [Jira](https://issues.apache.org/jira/projects/STREAMPIPES) which is tagged with a _newbie_ tag
* Have a look at our developer wiki at [https://cwiki.apache.org/confluence/display/STREAMPIPES/Home](https://cwiki.apache.org/confluence/display/STREAMPIPES/Home) to learn more about StreamPipes development.

Have fun!

