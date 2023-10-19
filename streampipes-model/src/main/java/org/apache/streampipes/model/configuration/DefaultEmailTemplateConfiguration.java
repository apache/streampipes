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

package org.apache.streampipes.model.configuration;

public class DefaultEmailTemplateConfiguration {

  public EmailTemplateConfig getDefaultTemplates() {
    return new EmailTemplateConfig(getTemplate());
  }

  @SuppressWarnings("all")
  private String getTemplate() {
    return "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "\n"
        + "    <meta charset=\"utf-8\">\n"
        + "    <meta content=\"ie=edge\" http-equiv=\"x-ua-compatible\">\n"
        + "    <title>###TITLE###</title>\n"
        + "    <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\n"
        + "    <style type=\"text/css\">\n"
        + "        body,\n"
        + "        table,\n"
        + "        td,\n"
        + "        a {\n"
        + "            -ms-text-size-adjust: 100%; /* 1 */\n"
        + "            -webkit-text-size-adjust: 100%; /* 2 */\n"
        + "        }\n"
        + "\n"
        + "        table,\n"
        + "        td {\n"
        + "            mso-table-rspace: 0pt;\n"
        + "            mso-table-lspace: 0pt;\n"
        + "        }\n"
        + "\n"
        + "        img {\n"
        + "            -ms-interpolation-mode: bicubic;\n"
        + "        }\n"
        + "\n"
        + "        a[x-apple-data-detectors] {\n"
        + "            font-family: inherit !important;\n"
        + "            font-size: inherit !important;\n"
        + "            font-weight: inherit !important;\n"
        + "            line-height: inherit !important;\n"
        + "            color: inherit !important;\n"
        + "            text-decoration: none !important;\n"
        + "        }\n"
        + "\n"
        + "        div[style*=\"margin: 16px 0;\"] {\n"
        + "            margin: 0 !important;\n"
        + "        }\n"
        + "\n"
        + "        body {\n"
        + "            width: 100% !important;\n"
        + "            height: 100% !important;\n"
        + "            padding: 0 !important;\n"
        + "            margin: 0 !important;\n"
        + "        }\n"
        + "\n"
        + "        table {\n"
        + "            border-collapse: collapse !important;\n"
        + "        }\n"
        + "\n"
        + "        a {\n"
        + "            color: #1b1464;\n"
        + "        }\n"
        + "\n"
        + "        img {\n"
        + "            height: auto;\n"
        + "            line-height: 100%;\n"
        + "            text-decoration: none;\n"
        + "            border: 0;\n"
        + "            outline: none;\n"
        + "        }\n"
        + "\n"
        + "    </style>\n"
        + "\n"
        + "</head>\n"
        + "<body style=\"background-color: #e9ecef;\">\n"
        + "\n"
        + "<div class=\"preheader\"\n"
        + "     style=\"display: none; max-width: 0; max-height: 0; overflow: hidden; font-size: 1px; line-height: 1px; color: #fff; opacity: 0;\">\n"
        + "    ###PREHEADER###\n"
        + "</div>\n"
        + "\n"
        + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n"
        + "\n"
        + "    <tr>\n"
        + "        <td align=\"center\" bgcolor=\"#e9ecef\">\n"
        + "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px;\" width=\"100%\">\n"
        + "                <tr>\n"
        + "                    <td align=\"center\" style=\"padding: 36px 24px;\" valign=\"top\">\n"
        + "                        <a href=\"###BASE_URL###\" style=\"display: inline-block;\" target=\"_blank\">\n"
        + "                            <img alt=\"Logo\" border=\"0\" src=\"###LOGO###\"\n"
        + "                                 style=\"display: block; width: 150px; max-width: 150px; min-width: 150px;\"\n"
        + "                                 width=\"150\">\n"
        + "                        </a>\n"
        + "                    </td>\n"
        + "                </tr>\n"
        + "            </table>\n"
        + "        </td>\n"
        + "    </tr>\n"
        + "\n"
        + "    <tr>\n"
        + "        <td align=\"center\" bgcolor=\"#e9ecef\">\n"
        + "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px;\" width=\"100%\">\n"
        + "                <tr>\n"
        + "                    <td align=\"left\" bgcolor=\"#ffffff\"\n"
        + "                        style=\"padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;\">\n"
        + "                        <h1 style=\"margin: 0; font-size: 32px; font-weight: 700; letter-spacing: -1px; line-height: 48px;\">\n"
        + "                            ###TITLE###</h1>\n"
        + "                    </td>\n"
        + "                </tr>\n"
        + "            </table>\n"
        + "        </td>\n"
        + "    </tr>\n"
        + "\n"
        + "    <tr>\n"
        + "        <td align=\"center\" bgcolor=\"#e9ecef\">\n"
        + "            ###INNER###\n"
        + "        </td>\n"
        + "    </tr>\n"
        + "    <tr>\n"
        + "        <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 24px;\">\n"
        + "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px;\" width=\"100%\">\n"
        + "                <tr>\n"
        + "                    <td align=\"center\" bgcolor=\"#e9ecef\"\n"
        + "                        style=\"padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666;\">\n"
        + "                        <p style=\"margin: 0;\">This mail was automatically sent from a local installation of Apache\n"
        + "                            StreamPipes - contact your administrator if you think you shouldn't receive this mail.</p>\n"
        + "                        <p>This mail is not sent by the Apache StreamPipes team.</p>\n"
        + "                        </p>\n"
        + "                    </td>\n"
        + "                </tr>\n"
        + "            </table>\n"
        + "        </td>\n"
        + "    </tr>\n"
        + "</table>\n"
        + "</body>\n"
        + "</html>";
  }
}
