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

let branchName = 'dev';
let programArgs = process.argv;
// If build config is not valid, use "dev"
if (programArgs.length <= 2) {
    console.log('No build config specified. Using default config for dev.');
    branchName = 'dev';
} else {
    branchName = programArgs[2].trim();
    console.log('Build config specified. Using config for ' + branchName);
}

// Check if Config-File for current Branch exists, otherwise use "def"
if (!fs.existsSync(`deployment/${branchName}/config.yml`)) {
    console.log(
        `Could not find config for ${branchName}. Using config for dev.`,
    );
    branchName = 'dev';
}

// Read Config-File and check if it is valid
let config = {};
try {
    config = yaml.load(
        fs.readFileSync(`deployment/${branchName}/config.yml`, 'utf8'),
    );
} catch (error) {
    console.log('Invalid Config-File. Pre-Build failed.');
    process.exit(1);
}

// Read Modules-File and check if it is valid
let modules = {};
try {
    modules = yaml.load(fs.readFileSync('deployment/modules.yml', 'utf8'));
} catch (error) {
    console.log('Invalid Modules-File. Pre-Build failed.');
    process.exit(1);
}

// Add active Modules to Template-Variable
let modulesActive = { modulesActive: [] };
for (let module of config.modules) {
    modulesActive['modulesActive'].push({
        module: module,
        componentImport: modules[module]['componentImport'],
        moduleName: modules[module]['moduleName'],
        component: modules[module]['component'],
        componentPath: modules[module]['componentPath'],
        path: modules[module]['path'],
        link: modules[module]['link'],
        pageNames: modules[module]['pageNames'],
        privileges: modules[module]['privileges'],
        url: modules[module]['url'],
        title: modules[module]['title'],
        icon: modules[module]['icon'],
        admin: modules[module]['admin'],
        description: modules[module]['description'],
        showStatusBox: modules[module]['showStatusBox'],
        statusBox: modules[module]['statusBox'],
    });
    console.log('Active Angular Module: ' + module);
}

// Create necessary JavaScript-Files from Template and move to respective Directory
fs.writeFileSync(
    'src/app/app.module.ts',
    mustache.render(
        fs.readFileSync('deployment/app.module.mst', 'utf8').toString(),
        modulesActive,
    ),
);
fs.writeFileSync(
    'src/app/home/home.service.ts',
    mustache.render(
        fs.readFileSync('deployment/home.service.mst', 'utf8').toString(),
        modulesActive,
    ),
);
fs.writeFileSync(
    'src/app/app-routing.module.ts',
    mustache.render(
        fs.readFileSync('deployment/app-routing.module.mst', 'utf8').toString(),
        modulesActive,
    ),
);
fs.writeFileSync(
    'src/app/core/components/base-navigation.component.ts',
    mustache.render(
        fs
            .readFileSync('deployment/base-navigation.component.mst', 'utf8')
            .toString(),
        modulesActive,
    ),
);
fs.writeFileSync(
    'src/app/app-overview/apps.ts',
    fs.readFileSync('deployment/apps.ts'),
);
fs.writeFileSync(
    'src/app/services/app.constants.ts',
    fs.readFileSync('deployment/app-constants.ts', 'utf8'),
);

// Move Images
fs.writeFileSync(
    'src/assets/img/sp/logo.png',
    fs.readFileSync(config['login']['logo']),
);
console.log('Moved: logo.png');

fs.writeFileSync(
    'src/assets/img/sp/logo-navigation.png',
    fs.readFileSync(config['login']['logo-navigation']),
);
console.log('Moved: sp-logo-navigation.png');

fs.writeFileSync(
    'src/assets/img/favicon/favicon-96x96.png',
    fs.readFileSync(config['login']['favicon']),
);
console.log('Moved: favicon');

fs.writeFileSync(
    'src/scss/_variables.scss',
    fs.readFileSync('deployment/_variables.scss', 'utf8'),
);
console.log('Moved: styling variables file');

fs.writeFileSync(
    'src/proxy.conf.json',
    fs.readFileSync('deployment/proxy.conf.json', 'utf8'),
);
console.log('Moved: webpack dev config');

if (process.env.THEME_LOC !== undefined) {
    console.log(`Using custom-provided theme ${process.env.THEME_LOC}`);
    fs.writeFileSync(
        'src/scss/_variables.scss',
        fs.readFileSync(process.env.THEME_LOC, 'utf8'),
    );
}

if (process.env.LOGO_HEADER_LOC !== undefined) {
    console.log(
        `Using custom-provided header logo ${process.env.LOGO_HEADER_LOC}`,
    );
    fs.writeFileSync(
        'src/assets/img/sp/logo.png',
        fs.readFileSync(process.env.LOGO_HEADER_LOC),
    );
}

if (process.env.LOGO_NAV_LOC !== undefined) {
    console.log(
        `Using custom-provided navbar logo ${process.env.LOGO_NAV_LOC}`,
    );
    fs.writeFileSync(
        'src/assets/img/sp/logo-navigation.png',
        fs.readFileSync(process.env.LOGO_NAV_LOC),
    );
}

if (process.env.FAVICON_LOC !== undefined) {
    console.log(`Using custom-provided favicon ${process.env.FAVICON_LOC}`);
    fs.writeFileSync(
        'src/assets/img/favicon/favicon-96x96.png',
        fs.readFileSync(process.env.FAVICON_LOC),
    );
}

if (process.env.CONSTANTS_FILE !== undefined) {
    console.log(
        `Using custom-provided constants file ${process.env.CONSTANTS_FILE}`,
    );
    fs.writeFileSync(
        'src/app/services/app.constants.ts',
        fs.readFileSync(process.env.CONSTANTS_FILE, 'utf8'),
    );
}

console.log('Moving translations from deployment/i18n to assets/i18n');

const sourceDir = 'deployment/i18n';
const destDir = 'src/assets/i18n';

if (!fs.existsSync(destDir)) {
    fs.mkdirSync(destDir, { recursive: true });
}

fs.readdirSync(sourceDir).forEach(file => {
    fs.writeFileSync(
        `${destDir}/${file}`,
        fs.readFileSync(`${sourceDir}/${file}`, 'utf8'),
    );
});
console.log('Pre-Build finished.');
