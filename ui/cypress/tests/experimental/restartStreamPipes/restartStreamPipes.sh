#!/bin/sh

cd ../../../../../installer/compose/
docker-compose up -d
sleep 30

cd ../../ui
npx cypress run --spec 'cypress/tests/experimental/restartStreamPipes/restartStreamPipes1.ts'

cd ../installer/compose/
docker-compose down
docker-compose up -d
sleep 30

cd ../../ui
npx cypress run --spec 'cypress/tests/experimental/restartStreamPipes/restartStreamPipes2.ts'

cd ../installer/compose/
docker-compose down -v

