package org.apache.streampipes.security.jwt;

import io.jsonwebtoken.*;
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
