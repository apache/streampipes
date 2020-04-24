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

yaml = require('js-yaml');
fs = require('fs');
mustache = require('mustache');

console.log('Pre-Build started.');

// Get Branch Name from GitLab-CI
let branchName = process.env.CI_COMMIT_REF_NAME;

// If Branch Name is not valid, use "def"
if (branchName === undefined) {
    console.log('Environment Variable invalid. Using Config for dev-Branch.');
    branchName = 'dev';
} else {
    console.log('Environment Variable valid. Using Config for ' + branchName + '-Branch.');
}

// Check if Config-File for current Branch exists, otherwise use "def"
if (!fs.existsSync('deployment/' + branchName + '/config.yml', 'utf8')) {
    console.log('Could not find Config for ' + branchName + '-Branch. Using Config for dev-Branch.');
    branchName = 'rel';
}

// Read Config-File and check if it is valid
let config = {};
try {
    config = yaml.safeLoad(fs.readFileSync('deployment/' + branchName + '/config.yml', 'utf8'));
} catch (error) {
    console.log('Invalid Config-File. Pre-Build failed.');
    process.exit(1);
}

// Read Modules-File and check if it is valid
let modules = {};
try {
    modules = yaml.safeLoad(fs.readFileSync('deployment/modules.yml', 'utf8'));
} catch (error) {
    console.log('Invalid Modules-File. Pre-Build failed.');
    process.exit(1);
}

// Add active Modules to Template-Variable
let modulesActive = {modulesActive: []};
for (let module of config.modules) {
    modulesActive['modulesActive'].push({
        module: module,
        ng5: modules[module]['ng5'],
        ng1_templateUrl: modules[module]['ng1_templateUrl'],
        ng5_moduleName: modules[module]['ng5_moduleName'],
        ng5_component: modules[module]['ng5_component'],
        ng5_componentPath: modules[module]['ng5_componentPath'],
        path: modules[module]['path'],
        link: modules[module]['link'],
        url: modules[module]['url'],
        title: modules[module]['title'],
        icon: modules[module]['icon'],
        admin: modules[module]['admin'],
        description: modules[module]['description'],
        homeImage: modules[module]['homeImage']
    });
    console.log('Active Angular ' + (modules[module]['ng5']===true?5:1) + ' Module: ' + module);
}

modulesActive.containsPipeline = function() {
    return function(cv, render) {
        if (render(cv).includes(":pipeline") != -1) {
            return "params: {pipeline: null},"
        }

        return "";
    }
};

// Create necessary JavaScript-Files from Template and move to respective Directory
fs.writeFileSync('src/app/app.module.ts', mustache.render(fs.readFileSync('deployment/app.module.mst', 'utf8').toString(), modulesActive));
fs.writeFileSync('src/app/appng5.module.ts', mustache.render(fs.readFileSync('deployment/appng5.module.mst', 'utf8').toString(), modulesActive));
fs.writeFileSync('src/app/core/state.config.ts', mustache.render(fs.readFileSync('deployment/state.config.mst', 'utf8').toString(), modulesActive));
fs.writeFileSync('src/app/layout/toolbar.controller.ts', mustache.render(fs.readFileSync('deployment/toolbar.controller.mst', 'utf8').toString(), modulesActive));
fs.writeFileSync('src/app/home/home.service.ts', mustache.render(fs.readFileSync('deployment/home.service.mst', 'utf8').toString(), modulesActive));


// Move Images
fs.writeFileSync('src/assets/img/login/logo.png', fs.readFileSync(config['login']['logo']));
console.log('Moved: logo.png');
fs.writeFileSync('src/assets/img/sp/sp-logo-right-white.png', fs.readFileSync(config['login']['logo-right']));
console.log('Moved: sp-logo-right-white.png');

console.log('Pre-Build finished.');