/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

export default function mdThemingProviderConfig($mdThemingProvider) {

    $mdThemingProvider.definePalette('streamPipesPrimary', {
        '50': '304269',
        '100': '304269',
        '200': '304269',
        '300': '304269',
        '400': '304269',
        '500': '304269',
        '600': '304269',
        '700': '003B3D',
        '800': '39b54a',
        '900': '39b54a',
        'A100': '39b54a',
        'A200': '39b54a',
        'A400': '39b54a',
        'A700': '39b54a',
        'contrastDefaultColor': 'light',    // whether, by default, text (contrast)
                                            // on this palette should be dark or light

        'contrastDarkColors': ['50', '100', //hues which contrast should be 'dark' by default
            '200', '300', '400', 'A100'],
    });

    $mdThemingProvider.definePalette('streamPipesAccent', {
        '50': 'DF5A49',
        '100': 'DF5A49',
        '200': '007F54',
        '300': '007F54',
        '400': '39B54A',
        '500': '45DA59',
        '600': '45DA59',
        '700': '45DA59',
        '800': '45DA59',
        '900': '1B1464',
        'A100': '1B1464',
        'A200': '1B1464',
        'A400': '1B1464',
        'A700': '1B1464',
        'contrastDefaultColor': 'light',    // whether, by default, text (contrast)
                                            // on this palette should be dark or light

        'contrastDarkColors': ['50', '100', //hues which contrast should be 'dark' by default
            '200', '300', '400', 'A100'],
    });

    $mdThemingProvider.theme('default')
        .primaryPalette('streamPipesPrimary')
        .accentPalette('streamPipesAccent')

}

mdThemingProviderConfig.$inject = ['$mdThemingProvider'];