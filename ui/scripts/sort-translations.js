const fs = require('fs');

const file = process.argv[2];
if (!file) {
    console.error('Usage: node sort-translations.js <file>');
    process.exit(1);
}

const json = JSON.parse(fs.readFileSync(file, 'utf8'));

const sorted = Object.keys(json)
    .sort((a, b) => a.localeCompare(b))
    .reduce((acc, key) => {
        acc[key] = json[key];
        return acc;
    }, {});

fs.writeFileSync(file, JSON.stringify(sorted, null, 2) + '\n');
