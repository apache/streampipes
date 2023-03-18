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

#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.config;

public class ConfigKeys {
  public final static String HOST = "SP_HOST";
  public final static String PORT = "SP_PORT";
  public final static String SERVICE_NAME = "SP_SERVICE_NAME";
  public final static String FLINK_HOST = "SP_FLINK_HOST";
  public final static String FLINK_PORT = "SP_FLINK_PORT";
  public static final String DEBUG = "SP_FLINK_DEBUG";
  public static final String FLINK_JAR_FILE_LOC = "SP_FLINK_JAR_FILE_LOC";
}
