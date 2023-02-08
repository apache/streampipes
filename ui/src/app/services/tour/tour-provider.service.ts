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

import createPipelineTourConstants from './create-pipeline-tour.constants';
import dashboardTourConstants from './dashboard-tour.constants';
import adapterTourConstants from './adapter-tour.constants';
import adapterTour2Constants from './adapter-tour-2.constants';
import adapterTour3Constants from './adapter-tour-3.constants';
import { Injectable } from '@angular/core';

@Injectable()
export class TourProviderService {
    guidedTours: any;

    // This is needed to configure the time in cypress test cases
    time: any;

    constructor() {
        this.guidedTours = [];
        this.guidedTours.push(createPipelineTourConstants.createPipelineTour);
        this.guidedTours.push(dashboardTourConstants.dashboardTour);
        this.guidedTours.push(adapterTourConstants.adapterTour);
        this.guidedTours.push(adapterTour2Constants.adapterTour);
        this.guidedTours.push(adapterTour3Constants.adapterTour);
        this.time = 500;
    }

    getAvailableTours() {
        return this.guidedTours;
    }

    getTourById(tourId) {
        return this.guidedTours.find(tour => {
            return tour.id === tourId;
        });
    }

    // This is needed to configure the time in cypress test cases
    setTime(newTime) {
        this.time = newTime;
    }

    getTime() {
        return this.time;
    }
}
