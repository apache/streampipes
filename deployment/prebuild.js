yaml = require('js-yaml');
fs   = require('fs');

try {
    const branchName = process.env.CI_COMMIT_REF_NAME;
    const config = yaml.safeLoad(fs.readFileSync('deployment/' + branchName + '/config.yml', 'utf8'));
    console.log('Valid Config-File');
    console.log('Current Branch: ' + branchName);

    fs.writeFileSync('img/login/background.png', fs.readFileSync(config['loginBackgroundImage']));
    fs.writeFileSync('img/login/logo.png', fs.readFileSync(config['logo']));
    console.log('Moved Images')

} catch (e) {
    console.log(e);
}