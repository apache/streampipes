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

package org.apache.streampipes.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.WeakKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtTokenValidator {

  private static final Logger LOG = LoggerFactory.getLogger(JwtTokenValidator.class);

  public static boolean validateJwtToken(String jwtToken,
                                         SigningKeyResolver resolver) {
    return validateJwtToken(JwtTokenUtils.jwtParser(resolver), jwtToken);
  }

  public static boolean validateJwtToken(String tokenSecret,
                                         String jwtToken) {
    return validateJwtToken(JwtTokenUtils.jwtParser(tokenSecret), jwtToken);
  }

  private static boolean validateJwtToken(JwtParser parser,
                                          String jwtToken) {
    try {
      parser.parseClaimsJws(jwtToken);
      return true;
    } catch (MalformedJwtException ex) {
      LOG.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      LOG.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      LOG.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      LOG.error("JWT claims are empty.");
    } catch (WeakKeyException ex) {
      LOG.error("Weak Key");
    }
    return false;
  }
}
