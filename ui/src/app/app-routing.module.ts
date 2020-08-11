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

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {LoginComponent} from "./login/components/login/login.component";
import {SetupComponent} from "./login/components/setup/setup.component";
import {StreampipesComponent} from "./core-v2/components/streampipes/streampipes.component";
import {EditorComponent} from "./editor/editor.component";
import {PipelinesComponent} from "./pipelines/pipelines.component";
import {ConnectComponent} from "./connect/connect.component";
import {DashboardComponent} from "./dashboard/dashboard.component";
import {DataExplorerComponent} from "./data-explorer/data-explorer.component";
import {AppOverviewComponent} from "./app-overview/app-overview.component";
import {AddComponent} from "./add/add.component";
import {ConfigurationComponent} from "./configuration/configuration.component";
import {PipelineDetailsComponent} from "./pipeline-details/pipeline-details.component";
import {StandaloneDashboardComponent} from "./dashboard/components/standalone/standalone-dashboard.component";

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'setup', component: SetupComponent },
  { path: 'standalone/:dashboardId', component: StandaloneDashboardComponent },
  { path: '', component: StreampipesComponent, children: [
      { path: '', component: HomeComponent },
      { path: 'add', component: AddComponent },
      { path: 'app-overview', component: AppOverviewComponent },
      { path: 'connect', component: ConnectComponent },
      { path: 'configuration', component: ConfigurationComponent },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'dataexplorer', component: DataExplorerComponent },
      { path: 'editor', component: EditorComponent },
      { path: 'pipelines', component: PipelinesComponent },
      { path: 'pipeline-details', component: PipelineDetailsComponent }
    ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }