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

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.client.StreamPipesCredentials;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.pipeline.PipelineOperationStatus;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;

import java.util.List;

public class SineSignal {

    public static void main(String ... args) {

        StreamPipesCredentials credentials = StreamPipesCredentials
                .from("wiener@fzi.de", "lh2pcpVm2WhRy04t8kXxXZAK");

        // Create an instance of the StreamPipes client
        StreamPipesClient client = StreamPipesClient
                .create("ipe-zwergwal-01.fzi.de", 80, credentials, true);

        List<Pipeline> pipelines = client.pipelines().all();
        Pipeline pipeline = pipelines.stream()
                .filter(p -> p.getName().equals("jet"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Pipeline not found"));


        for (int i=0; i<100; i++) {
            double y = square(i);

            pipeline.getSepas().forEach(p -> p.getStaticProperties().stream()
                    .filter(FreeTextStaticProperty.class::isInstance)
                    .map(FreeTextStaticProperty.class::cast)
                    .filter(FreeTextStaticProperty::isReconfigurable)
                    .forEach(sp -> {
                        if (sp.getInternalName().equals("i-am-reconfigurable")) {
                            sp.setValue(Float.toString((float) y));
                        }
                    }));

            System.out.println("Trying to reconfigure with value: " + y);
            PipelineOperationStatus message = client.pipelines().reconfigure(pipeline);
            System.out.println(message.getTitle());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static double sine(int i){
        double a = i / 10d;
        return 3 * Math.sin(a);
    }

    private static int triangle(int i) {
        return  Math.abs((i % 6) - 3);
    }

    private static int square(int i) {
        return (i % 6) < 3 ? 3 : 0;
    }
}
