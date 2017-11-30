yaml = require('js-yaml');
fs   = require('fs');

try {
    console.log(process.env.CI_COMMIT_REF_NAME)
    const config = yaml.safeLoad(fs.readFileSync('deployment/' + process.env.CI_COMMIT_REF_NAME + '/config.yml', 'utf8'));
    console.log('It works');
} catch (e) {
    console.log(e);
}