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

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtTokenUtils {

  private static final Logger LOG = LoggerFactory.getLogger(JwtTokenUtils.class);

  public static String makeJwtToken(String subject,
                                    String tokenSecret,
                                    Date expirationDate) {

    SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));

    return Jwts
            .builder()
            .setSubject(subject)
            .setIssuedAt(new Date())
            .setExpiration(expirationDate)
            .signWith(key).compact();
  }

  public static String getUserIdFromToken(String tokenSecret,
                                          String token) {
    Claims claims = jwtParser(tokenSecret).parseClaimsJws(token).getBody();
    return claims.getSubject();
  }

  public static String getUserIdFromToken(String token,
                                          SigningKeyResolver resolver) {
    return jwtParser(resolver).parseClaimsJws(token).getBody().getSubject();
  }

  private static JwtParser jwtParser(String tokenSecret) {
    return Jwts.parserBuilder()
            .setSigningKey(tokenSecret.getBytes(StandardCharsets.UTF_8))
            .build();
  }

  private static JwtParser jwtParser(SigningKeyResolver resolver) {
    return Jwts.parserBuilder()
            .setSigningKeyResolver(resolver)
            .build();
  }

  public static boolean validateJwtToken(String jwtToken,
                                         SigningKeyResolver resolver) {
    return validateJwtToken(jwtParser(resolver), jwtToken);
  }

  public static boolean validateJwtToken(String tokenSecret,
                                         String jwtToken) {
    return validateJwtToken(jwtParser(tokenSecret), jwtToken);
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
