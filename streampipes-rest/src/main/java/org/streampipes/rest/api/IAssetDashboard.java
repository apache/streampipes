/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.rest.api;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.streampipes.model.client.assetdashboard.AssetDashboardConfig;

import java.io.InputStream;

import javax.ws.rs.core.Response;

public interface IAssetDashboard {

  Response getAssetDashboard(String dashboardId);

  Response getAllDashboards();

  Response storeAssetDashboard(AssetDashboardConfig dashboardConfig);

  Response deleteAssetDashboard(String dashboardId);

  Response storeDashboardImage(InputStream uploadedInputStream,
                               FormDataContentDisposition fileDetail);

  Response getDashboardImage(String imageName);
}
