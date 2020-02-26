/*
Copyright 2020 FZI Forschungszentrum Informatik

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

package org.apache.streampipes.rest.impl.datalake;


import org.apache.streampipes.rest.impl.dashboard.AbstractDashboard;
import org.apache.streampipes.storage.api.IDashboardStorage;

import javax.ws.rs.Path;

@Path("/v3/users/{username}/datalake/dashboard")
public class DataLakeDashboard extends AbstractDashboard {

    protected IDashboardStorage getDashboardStorage() {
        return getNoSqlStorage().getDataExplorerDashboardStorage();
    }
}
