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

package org.apache.streampipes.connect.adapters.generic.protocol.set;

import org.apache.streampipes.connect.adapters.generic.Mock;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;

public class HttpProtocolTest {

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(Mock.PORT);


//    @Test
//    public void getDataFromEndpointTest() {
//
//        String expected = "Expected String";
//
//        stubFor(get(urlEqualTo("/"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withBody(expected)));
//
//
//        HttpProtocol httpProtocol = new HttpProtocol(null, null, Mock.HOST + "/");
//
//        InputStream data = httpProtocol.getDataFromEndpoint();
//
//        String resultJson = "";
//
//        try {
//            resultJson = IOUtils.toString(data, "UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        assertEquals(expected, resultJson);
//    }


//    @Test
//    public void getNElementsTest() throws AdapterException {
//
//        stubFor(get(urlEqualTo("/"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withBody("Example response")));
//
//
//        HttpProtocol httpProtocol = new HttpProtocol(new TestParser(""), new TestFormat(), Mock.HOST + "/");
//
//        List<Map<String, Object>> result = httpProtocol.getNElements(1);
//
//        assertEquals(1, result.size());
//        assertEquals("value", result.get(0).get("key"));
//    }
//
//    private class TestParser extends Parser {
//        private byte[] data;
//        public TestParser(String data) {
//            this.data = data.getBytes();
//        }
//
//        @Override
//        public Parser getInstance(FormatDescription formatDescription) {
//            return null;
//        }
//
//        @Override
//        public void parse(InputStream data, EmitBinaryEvent emitBinaryEvent) {
//            emitBinaryEvent.emit(this.data);
//        }
//
//        @Override
//        public EventSchema getEventSchema(List<byte[]> oneEvent) {
//            return null;
//        }
//    }
//
//    private class TestFormat extends Format {
//
//        @Override
//        public Format getInstance(FormatDescription formatDescription) {
//            return null;
//        }
//
//        @Override
//        public FormatDescription declareModel() {
//            return null;
//        }
//
//        @Override
//        public String getId() {
//            return null;
//        }
//
//        @Override
//        public Map<String, Object> parse(byte[] object) {
//            Map<String, Object> result = new HashMap<>();
//            result.put("key", "value");
//            return result;
//        }
//    }

}