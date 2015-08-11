#!/bin/bash

# Initialize first run
if [[ -e /.firstrun ]]; then
    /scripts/first_run.sh
fi



# Start CouchDB
echo "Starting CouchDB..."
/usr/local/bin/couchdb
