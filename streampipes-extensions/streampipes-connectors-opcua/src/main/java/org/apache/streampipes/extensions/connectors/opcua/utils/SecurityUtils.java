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

package org.apache.streampipes.extensions.connectors.opcua.utils;

import org.apache.streampipes.model.Tuple2;

import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;

import java.util.List;

public class SecurityUtils {

  public static List<Tuple2<String, String>> getAvailableSecurityModes() {
    return List.of(
        new Tuple2<>("None", MessageSecurityMode.None.name()),
        new Tuple2<>("Sign", MessageSecurityMode.Sign.name()),
        new Tuple2<>("Sign & Encrypt", MessageSecurityMode.SignAndEncrypt.name())
    );
  }

  public static List<SecurityPolicy> getAvailableSecurityPolicies() {
    return List.of(
        SecurityPolicy.None,
        SecurityPolicy.Basic128Rsa15,
        SecurityPolicy.Basic256,
        SecurityPolicy.Basic256Sha256,
        SecurityPolicy.Aes128_Sha256_RsaOaep,
        SecurityPolicy.Aes256_Sha256_RsaPss
    );
  }
}
