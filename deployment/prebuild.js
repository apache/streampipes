yaml = require('js-yaml');
fs   = require('fs');

try {
    console.log(process.env.BRANCH_NAME)
    const config = yaml.safeLoad(fs.readFileSync('deployment/' + process.env.BRANCH_NAME + '/config.yml', 'utf8'));
    console.log('It works');
} catch (e) {
    console.log(e);
}