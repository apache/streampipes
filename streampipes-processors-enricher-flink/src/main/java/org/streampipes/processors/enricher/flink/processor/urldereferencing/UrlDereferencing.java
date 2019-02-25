/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.processors.enricher.flink.processor.urldereferencing;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.apache.http.conn.HttpHostConnectException;
import org.streampipes.logging.api.Logger;
import org.streampipes.model.graph.DataProcessorInvocation;

import java.net.URL;
import java.util.Map;

public class UrlDereferencing implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

    private String urlString;
    private String appendHtml;
    private Logger logger;

    public UrlDereferencing(String urlString, String appendHtml, DataProcessorInvocation graph) {
        this.urlString = urlString;
        this.appendHtml = appendHtml;
        this.logger = graph.getLogger(UrlDereferencing.class);
    }

    @Override
    public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
        HttpResponse<String> response;

        try {
            response = Unirest.get(in.get(urlString).toString()).asString();
            String body = response.getBody();

            in.put(appendHtml, body);
        } catch (Exception e) {
            logger.error("Error while fetching data from URL: " + urlString);
            in.put(appendHtml, "Error while fetching data from URL: " + urlString);
        }

        out.collect(in);
    }
}
