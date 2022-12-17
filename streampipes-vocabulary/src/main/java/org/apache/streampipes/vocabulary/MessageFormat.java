/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.vocabulary;

public class MessageFormat {

  private static final String SEPA_NAMESPACE = "http://sepa.event-processing.org/sepa#";

  public static final String JSON = SEPA_NAMESPACE + "json";
  public static final String AVRO = SEPA_NAMESPACE + "avro";
  public static final String FST = SEPA_NAMESPACE + "fst";
  public static final String SMILE = SEPA_NAMESPACE + "smile";
  public static final String CBOR = SEPA_NAMESPACE + "cbor";
  public static final String XML = SEPA_NAMESPACE + "xml";
  public static final String THRIFT = SEPA_NAMESPACE + "thrift";

}
