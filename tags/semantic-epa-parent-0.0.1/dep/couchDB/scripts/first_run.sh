#!/bin/bash
USER=${COUCHDB_USERNAME:-admin}
PASS=${COUCHDB_PASSWORD:-admin}
#DB=${COUCHDB_DBNAME:-}

# Start CouchDB service
/usr/local/bin/couchdb -b
while ! nc -vz localhost 5984; do sleep 1; done

# Create User
echo "Creating user: \"$USER\"..."
curl -X PUT http://127.0.0.1:5984/_config/admins/$USER -d '"'${PASS}'"'

# Create Database
echo "Creating database: users"
curl -X PUT http://$USER:$PASS@127.0.0.1:5984/users
echo "Creating database: connection"
curl -X PUT http://$USER:$PASS@127.0.0.1:5984/connection
echo "Creating database: pipeline"
curl -X PUT http://$USER:$PASS@127.0.0.1:5984/pipeline

# Add documents to the db
curl -H 'Content-Type: application/json' -X POST http://127.0.0.1:5984/users -d '{"username": "riemer@fzi.de", "password": "1234", "roles": "user"}'


curl -H 'Content-Type: application/json' \
		 -X POST http://admin:admin@127.0.0.1:5984/connection \
		-d '{
		 "_id": "_design/connection",
		"language": "javascript",
		"views": {
			"frequent": {
				"map": "function(doc) { if(doc.from && doc.to) { emit([doc.from, doc.to] , 1 ); } }",
				"reduce": "function (key, values) { return sum(values); }"
			}
		}
		}'

curl -H 'Content-Type: application/json' \
		 -X POST http://admin:admin@127.0.0.1:5984/users \
		-d '{
   "_id": "_design/users",
	   "language": "javascript",
	    "views": {
		       "password": {
				            "map": "function(doc) { if(doc.username && doc.password) { emit(doc.username, doc.password); } }"
									       }
											    }
												}'




# Stop CouchDB service
/usr/local/bin/couchdb -d

echo "========================================================================"
echo "CouchDB User: \"$USER\""
echo "CouchDB Password: \"$PASS\""
if [ ! -z "$DB" ]; then
    echo "CouchDB Database: \"$DB\""
fi
echo "========================================================================"

rm -f /.firstrun
