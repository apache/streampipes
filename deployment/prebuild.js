yaml = require('js-yaml');
fs = require('fs');

console.log('Pre-Build started.');

let branchName = process.env.CI_COMMIT_REF_NAME;

if (branchName === undefined) {
    console.log('Environment Variable invalid. Using Config for dev-Branch.');
    branchName = 'dev';
} else {
    console.log('Environment Variable valid. Using Config for ' + branchName + '-Branch.');
}

if (!fs.existsSync('deployment/' + branchName + '/config.yml', 'utf8')) {
    console.log('Could not find Config for ' + branchName + '-Branch. Using Config for dev-Branch.');
    branchName = 'dev';
}

let config = {};
try {
    config = yaml.safeLoad(fs.readFileSync('deployment/' + branchName + '/config.yml', 'utf8'));
} catch (error) {
    console.log('Invalid Config-File. Pre-Build failed.');
    process.exit(1);
}

fs.writeFileSync('img/login/background.png', fs.readFileSync(config['login']['backgroundImage']));
fs.writeFileSync('img/login/logo.png', fs.readFileSync(config['login']['logo']));



console.log('Pre-Build finished.');