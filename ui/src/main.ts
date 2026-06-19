import { provideZoneChangeDetection } from '@angular/core';
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

// needed so that maplibre attaches to leaflet upon startup
import 'leaflet';
import '@maplibre/maplibre-gl-leaflet';
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';

import * as echarts from 'echarts';
import * as transform from 'echarts-simple-transform';
import { ValueDistributionTransform } from './app/core-ui/echarts-transform/value-distribution.transform';
import { HistogramTransform } from './app/core-ui/echarts-transform/histogram.transform';
import { RoundValuesTransform } from './app/core-ui/echarts-transform/round-values.transform';
import { MapTransform } from './app/core-ui/echarts-transform/map.transform';
import { PieAggregateTransform } from './app/core-ui/echarts-transform/pie-aggregate.transform';

echarts.registerTransform(transform.aggregate);
echarts.registerTransform(ValueDistributionTransform);
echarts.registerTransform(HistogramTransform);
echarts.registerTransform(RoundValuesTransform);
echarts.registerTransform(MapTransform);
echarts.registerTransform(PieAggregateTransform);

import 'jquery';

bootstrapApplication(AppComponent, {
    ...appConfig,
    providers: [provideZoneChangeDetection(), ...appConfig.providers],
}).catch(err => console.error(err));
