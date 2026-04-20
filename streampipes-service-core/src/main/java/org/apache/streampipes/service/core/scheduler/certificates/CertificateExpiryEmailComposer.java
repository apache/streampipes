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

package org.apache.streampipes.service.core.scheduler.certificates;

import org.apache.streampipes.model.opcua.Certificate;

import java.util.List;
import java.util.Map;

public class CertificateExpiryEmailComposer {

  public String composeMessage(Map<Integer, List<Certificate>> expiringCertificates) {
    if (expiringCertificates == null || expiringCertificates.isEmpty()) {
      return "";
    }

    StringBuilder html = new StringBuilder();

    for (var entry : expiringCertificates.entrySet()) {
      Integer days = entry.getKey();
      List<Certificate> certs = entry.getValue();

      if (certs == null || certs.isEmpty()) {
        continue;
      }

      html.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;\">")
          .append("<tr>")
          .append("<td bgcolor=\"#ffffff\" style=\"padding:24px; font-family:Helvetica,Arial,sans-serif; font-size:16px; line-height:24px;\">")
          .append("<p style=\"margin:0 0 12px 0;\"><strong>")
          .append("Following certificates expire in ")
          .append(days)
          .append(" days:")
          .append("</strong></p>")
          .append("<ul style=\"margin:0; padding-left:20px;\">");

      for (Certificate cert : certs) {
        String issuer = extractIssuer(cert);
        html.append("<li style=\"margin-bottom:6px;\">")
            .append(escapeHtml(issuer))
            .append("</li>");
      }

      html.append("</ul>")
          .append("</td>")
          .append("</tr>")
          .append("</table>");
    }

    return html.toString();
  }

  private String extractIssuer(Certificate cert) {
    if (cert == null) {
      return "unknown issuer";
    }
    String issuer = cert.getIssuerDn();
    return (issuer == null || issuer.isBlank()) ? "unknown issuer" : issuer;
  }

  private String escapeHtml(String value) {
    return value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }
}
