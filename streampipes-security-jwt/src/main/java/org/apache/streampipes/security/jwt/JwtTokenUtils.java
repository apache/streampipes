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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;

import java.nio.charset.StandardCharsets;

public class JwtTokenUtils {

  public static String getUserIdFromToken(String tokenSecret,
                                          String token) {
    Claims claims = jwtParser(tokenSecret).parseClaimsJws(token).getBody();
    return claims.getSubject();
  }

  public static String getUserIdFromToken(String token,
                                          SigningKeyResolver resolver) {
    return jwtParser(resolver).parseClaimsJws(token).getBody().getSubject();
  }

  public static Claims getClaimsFromToken(String token,
                                          SigningKeyResolver resolver) {
    return jwtParser(resolver).parseClaimsJws(token).getBody();
  }

  public static JwtParser jwtParser(String tokenSecret) {
    return Jwts.parserBuilder()
        .setSigningKey(tokenSecret.getBytes(StandardCharsets.UTF_8))
        .build();
  }

  public static JwtParser jwtParser(SigningKeyResolver resolver) {
    return Jwts.parserBuilder()
        .setSigningKeyResolver(resolver)
        .build();
  }

}
