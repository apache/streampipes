/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.storage.rdf4j.ontology;


import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

public class QueryExecutor {

  private Repository repository;

  public QueryExecutor(Repository repository) {
    this.repository = repository;
  }

  public TupleQueryResult executeQuery(String sparqlQuery) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
    RepositoryConnection connection = repository.getConnection();
    TupleQuery tupleQuery;
    TupleQueryResult result;
    tupleQuery = connection.prepareTupleQuery(
            QueryLanguage.SPARQL, sparqlQuery);
    result = tupleQuery.evaluate();

    connection.close();
    return result;
  }

  public void executeUpdate(String sparqlUpdate) throws UpdateExecutionException, RepositoryException, MalformedQueryException {
    RepositoryConnection connection = repository.getConnection();
    Update tupleQuery;
    tupleQuery = connection.prepareUpdate(QueryLanguage.SPARQL, sparqlUpdate);
    tupleQuery.execute();

    connection.close();
  }

}
