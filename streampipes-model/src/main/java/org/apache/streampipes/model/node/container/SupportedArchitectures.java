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
package org.apache.streampipes.model.node.container;

import org.apache.streampipes.vocabulary.StreamPipes;

public class SupportedArchitectures {

    private static final String ARCHITECTURE_NAMESPACE = StreamPipes.NS + "architecture#";
    public static final String AMD64 = ARCHITECTURE_NAMESPACE + "x86_64";
    public static final String ARM32 = ARCHITECTURE_NAMESPACE + "arm32";
    public static final String AARCH64 = ARCHITECTURE_NAMESPACE + "aarch64";

    public static String amd64(){
        return AMD64;
    }

    public static String arm32(){
        return ARM32;
    }

    public static String aarch64(){
        return AARCH64;
    }
}
