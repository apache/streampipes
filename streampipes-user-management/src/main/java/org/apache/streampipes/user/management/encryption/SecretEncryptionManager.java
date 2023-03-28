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
package org.apache.streampipes.user.management.encryption;

import org.apache.streampipes.commons.environment.Environments;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;


public class SecretEncryptionManager {

  public static String encrypt(String property) {
    return getEncryptor().encrypt(property);
  }

  public static String decrypt(String property) {
    return getEncryptor().decrypt(property);
  }

  private static StringEncryptor getEncryptor() {
    var env = Environments.getEnvironment();
    StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
    encryptor.setPassword(env.getEncryptionPasscode().getValueOrDefault());
    encryptor.setIvGenerator(new RandomIvGenerator());

    return encryptor;
  }
}
