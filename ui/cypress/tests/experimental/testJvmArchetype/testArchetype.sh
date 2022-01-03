#!/bin/sh

mvn archetype:generate -DarchetypeGroupId=org.apache.streampipes \
	-DarchetypeArtifactId=streampipes-archetype-extensions-jvm -DarchetypeVersion=0.69.0-SNAPSHOT \
	-DgroupId=org.streampipes.test -DartifactId=automated-test -DclassNamePrefix=AutomatedTest -DpackageName=test -DinteractiveMode=false

cd automated-test
mvn clean package

cd ..
docker-compose build
docker-compose up -d
sleep 30

cd backend
docker-compose up -d
sleep 30

cd ../../../../../
ls
npx cypress run --spec 'cypress/tests/experimental/testJvmArchetype/testJvmArchetype.ts'
ls
cd cypress/tests/experimental/testJvmArchetype/backend/
docker-compose down -v

cd ..
docker-compose down -v

rm -r automated-test
