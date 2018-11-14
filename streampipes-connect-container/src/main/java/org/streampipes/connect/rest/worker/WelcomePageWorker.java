/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.connect.rest.worker;


import org.rendersnake.HtmlCanvas;
import org.streampipes.connect.rest.AbstractContainerResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/")
public class WelcomePageWorker extends AbstractContainerResource {

    private String id;

    public WelcomePageWorker() {
        this.id = "Worker01";
    }

    public WelcomePageWorker(String id) {
        this.id = id;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getWelcomePageHtml() {
        return buildHtml();
    }

    private String buildHtml() {
        HtmlCanvas html = new HtmlCanvas();
        try {
            html
                    .head()
                    .title()
                    .content("StreamPipes Connector Worker Container")
                    ._head()
                    .body()
                    .h1().write("Worker Connector Container with ID: " + this.id)._h1()
                    ._body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html.toHtml();
    }
}
