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

package org.apache.streampipes.user.management.util;

import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.RandomStringGenerator;

import java.security.SecureRandom;

public class SecureStringGenerator {
  public String generateSecureString(int length) {
    // filter for characters allowed in URLs
    CharacterPredicate includeChars = ch -> (
        (ch >= 'a' && ch <= 'z')
            || (ch >= 'A' && ch <= 'Z')
            || (ch >= '0' && ch <= '9')
            || ch == '-' || ch == '_' || ch == '.'
            || ch == '!' || ch == '*' || ch == '(' || ch == ')'
            || ch == ':' || ch == '@' || ch == '='
            || ch == '+' || ch == '$' || ch == ','
    );
    // allowing all ASCII-characters from decimal id 33 to 125
    // see https://www.cs.cmu.edu/~pattis/15-1XX/common/handouts/ascii.html for full list
    var pwdGenerator = new RandomStringGenerator.Builder().usingRandom(new SecureRandom()::nextInt)
        .withinRange(33, 125)
        .filteredBy(includeChars)
        .build();
    return pwdGenerator.generate(length);
  }
}
