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

import * as angular from 'angular';

function filter(soType) {
    return function (eventProperties) {
        var result = [];
        angular.forEach(eventProperties, function (eventProperty) {
            if (eventProperty.properties.domainProperties && eventProperty.properties.domainProperties.indexOf(soType) > -1) {
                result.push(eventProperty)
            }
        });
        return result;
    };
}


function nu() {

    return function (eventProperties) {
        var result = [];
        angular.forEach(eventProperties, function (eventProperty) {
            if (eventProperty.properties == 'http://www.w3.org/2001/XMLSchema#float') {
                result.push(eventProperty)
            }
            else if (eventProperty.properties.runtimeType == 'http://www.w3.org/2001/XMLSchema#integer') {
                result.push(eventProperty)
            }
            else if (eventProperty.properties.runtimeType == 'http://www.w3.org/2001/XMLSchema#double' ||
                (eventProperty.properties.runtimeType == 'http://www.w3.org/2001/XMLSchema#float')) {
                result.push(eventProperty)
            } else if (eventProperty.properties.domainProperties && eventProperty.properties.domainProperties.indexOf('http://schema.org/Number') > -1) {
                result.push(eventProperty)
            }
        });
        return result;
    };

    //var result = [];

    //result.push(filter('http://schema.org/Number'))
    //result.push(runtimeTypeFilter('http://www.w3.org/2001/XMLSchema#float'))
    //result.push(runtimeTypeFilter('http://www.w3.org/2001/XMLSchema#integer'))
    //result.push(runtimeTypeFilter('http://www.w3.org/2001/XMLSchema#double'))


    //return uniqueArraybyId(result, runtimeName);
}

function geoLat() {
    return filter('http://www.w3.org/2003/01/geo/wgs84_pos#lat');
}

function geoLng() {
    return filter('http://www.w3.org/2003/01/geo/wgs84_pos#long');
}

function soNumber() {
    return filter('http://schema.org/Number');
};

function image() {
    return filter("https://image.com");
}

function soDateTime() {
    return filter('http://schema.org/DateTime');
};

export default {
    nu: nu,
    soNumber: soNumber,
    soDateTime: soDateTime,
    geoLat: geoLat,
    geoLng: geoLng,
    image: image
}
