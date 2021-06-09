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
package org.apache.streampipes.performance;

import Test.GenericTest;
import Test.Test;

public class TestFactory {


    public static Test getFromEnv(){
        switch (System.getenv("TestType")){
            case "Deployment":
                return getDeploymentTest();
            case "Latency":
                return getLatencyTest();
            case "Migration":
                return getMigrationTest();
            case "Reconfiguration":
                return getReconfigurationTest();
            default:
                throw new RuntimeException("No test configuration found.");
        }
    }


    public static Test getDeploymentTest(){
        return new GenericTest(getPipelineName(), true, false,
                false, 0, 1000);
    }

    public static Test getLatencyTest(){
        return new GenericTest(getPipelineName(), true, false,
                false, 0, 30000);
    }

    public static Test getMigrationTest(){
        return new GenericTest(getPipelineName(), true, true,
                false, 15000, 10000);
    }

    public static Test getReconfigurationTest(){
        return new GenericTest(getPipelineName(), true, false,
                true, 15000, 10000);
    }

    //Helpers
    private static String getPipelineName(){
        String pipelineName = System.getenv("PipelineName");
        if (pipelineName==null) throw new RuntimeException("No Pipeline Name provided.");
        return pipelineName;
    }

}
