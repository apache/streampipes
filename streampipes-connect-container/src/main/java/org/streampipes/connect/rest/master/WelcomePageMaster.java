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

package org.streampipes.connect.rest.master;


import org.rendersnake.HtmlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.connect.exception.AdapterException;
import org.streampipes.connect.management.master.AdapterMasterManagement;
import org.streampipes.connect.management.master.IAdapterMasterManagement;
import org.streampipes.connect.rest.AbstractContainerResource;
import org.streampipes.model.connect.adapter.AdapterDescription;
import org.streampipes.storage.couchdb.impl.AdapterStorageImpl;
import org.streampipes.storage.couchdb.utils.CouchDbConfig;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;


@Path("/")
public class WelcomePageMaster extends AbstractContainerResource {

	private Logger logger = LoggerFactory.getLogger(WelcomePageMaster.class);

	private IAdapterMasterManagement adapterMasterManagement;

	public WelcomePageMaster() {
		this.adapterMasterManagement = new AdapterMasterManagement();
	}

	public WelcomePageMaster(IAdapterMasterManagement adapterMasterManagement) {
		this.adapterMasterManagement = adapterMasterManagement;
	}
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getWelcomePageHtml() {
		return buildHtml();
	}

	private String buildHtml() {
		HtmlCanvas html = new HtmlCanvas();
		try {
			HtmlCanvas tmp = html
					.head()
					.title()
					.content("StreamPipes Connector Master Container")
					._head()
					.body()
					.h1().write("Connector Master Container")._h1()
					.h2().write("All Running Adapters")._h2()
					.ol();

			tmp = getAllRunningAdapters(tmp);

			html = tmp._ol()
					._body();
		} catch (IOException e) {
			logger.error("Error in SP Connect Master Container: ", e);
		}

		return html.toHtml();
	}

	private HtmlCanvas getAllRunningAdapters(HtmlCanvas canvas) throws IOException {

		List<AdapterDescription> allAdapterDescriptions = null;
		try {
			allAdapterDescriptions = this.adapterMasterManagement.getAllAdapters(new AdapterStorageImpl());
		} catch (AdapterException e) {
			logger.error("Could not connect to couchdb on URL: " + CouchDbConfig.INSTANCE.getHost(), e);
			canvas.li().write("Error while connecting to CouchDB on Host: " + CouchDbConfig.INSTANCE.getHost())._li();
			return canvas;
		}

		for (AdapterDescription ad : allAdapterDescriptions) {
			canvas.li().write(ad.getAdapterId())._li();
		}

		return canvas;
	}

	public void setAdapterMasterManagement(IAdapterMasterManagement adapterMasterManagement) {
		this.adapterMasterManagement = adapterMasterManagement;
	}
}
