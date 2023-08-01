/**
 * boilerpipe
 * <p>
 * Copyright (c) 2009, 2014 Christian Kohlschütter
 * <p>
 * The author licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kohlschutter.boilerpipe.sax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * A very simple HTTP/HTML fetcher, really just for demo purposes.
 */
public class HTMLFetcher {
  private HTMLFetcher() {
  }

  private static final Pattern PAT_CHARSET = Pattern.compile("charset=([^; ]+)$");

  /**
   * Fetches the document at the given URL, using {@link URLConnection}.
   *
   * @param url
   * @return
   * @throws IOException
   */
  public static HTMLDocument fetch(final URL url) throws IOException {
    final URLConnection conn = url.openConnection();
    final String ct = conn.getContentType();

    if (ct == null || !(ct.equals("text/html") || ct.startsWith("text/html;"))) {
      throw new IOException("Unsupported content type: " + ct);
    }

    Charset cs = Charset.forName("Cp1252");
    if (ct != null) {
      Matcher m = PAT_CHARSET.matcher(ct);
      if (m.find()) {
        final String charset = m.group(1);
        try {
          cs = Charset.forName(charset);
        } catch (UnsupportedCharsetException e) {
          // keep default
        }
      }
    }

    InputStream in = conn.getInputStream();

    final String encoding = conn.getContentEncoding();
    if (encoding != null) {
      if ("gzip".equalsIgnoreCase(encoding)) {
        in = new GZIPInputStream(in);
      } else {
        System.err.println("WARN: unsupported Content-Encoding: " + encoding);
      }
    }

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    byte[] buf = new byte[4096];
    int r;
    while ((r = in.read(buf)) != -1) {
      bos.write(buf, 0, r);
    }
    in.close();

    final byte[] data = bos.toByteArray();

    return new HTMLDocument(data, cs);
  }
}
