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
    branchName = 'dev';
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
        path: modules[module]['path'],
        templateUrl: modules[module]['templateUrl'],
        controller: modules[module]['controller'],
        link: modules[module]['link'],
        url: modules[module]['url'],
        title: modules[module]['title'],
        icon: modules[module]['icon'],
        admin: modules[module]['admin']
    });
    console.log('Active Module: ' + module)
}

// Create necessary JavaScript-Files from Template and move to respective Directory
fs.writeFileSync('app/app.module.js', mustache.render(fs.readFileSync('deployment/app.module.mst', 'utf8').toString(), modulesActive));
fs.writeFileSync('app/core/state.config.js', mustache.render(fs.readFileSync('deployment/state.config.mst', 'utf8').toString(), modulesActive));
fs.writeFileSync('app/layout/app.controller.js', mustache.render(fs.readFileSync('deployment/app.controller.mst', 'utf8').toString(), modulesActive));

// Move Images
fs.writeFileSync('img/login/background.png', fs.readFileSync(config['login']['backgroundImage']));
console.log('Moved: background.png');
fs.writeFileSync('img/login/logo.png', fs.readFileSync(config['login']['logo']));
console.log('Moved: logo.png');
fs.writeFileSync('img/sp/sp-logo-right-white.png', fs.readFileSync(config['login']['logo-right']));
console.log('Moved: sp-logo-right-white.png');

console.log('Pre-Build finished.');