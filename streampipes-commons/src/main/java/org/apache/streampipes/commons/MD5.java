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

package org.apache.streampipes.commons;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 Tools
 */
public final class MD5 {
  /**
   * Encodes a string
   *
   * @param str String to encode
   * @return Encoded String
   */
  public static String crypt(String str) {
    if (str == null || str.length() == 0) {
      throw new IllegalArgumentException("String to encrypt cannot be null or zero length");
    }

    StringBuilder hexString = new StringBuilder();

    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(str.getBytes());
      byte[] hash = md.digest();

      for (byte b : hash) {
        if ((0xff & b) < 0x10) {
          hexString.append("0").append(Integer.toHexString((0xFF & b)));
        } else {
          hexString.append(Integer.toHexString(0xFF & b));
        }
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return hexString.toString();
  }

}
