const fs = require('fs');

const file = process.argv[2];
if (!file) {
    console.error('Usage: node sort-translations.js <file>');
    process.exit(1);
}

if (!fs.existsSync(file)) {
    console.error(`File not found: ${file}`);
    process.exit(1);
}

let json;
try {
    json = JSON.parse(fs.readFileSync(file, 'utf8'));
} catch (err) {
    console.error(
        `Error: File "${file}" does not contain valid JSON: ${err.message}`,
    );
    process.exit(1);
}

const sorted = Object.keys(json)
    .sort((a, b) => a.localeCompare(b, 'en', { numeric: false }))
    .reduce((acc, key) => {
        acc[key] = json[key];
        return acc;
    }, {});

fs.writeFileSync(file, JSON.stringify(sorted, null, 2) + '\n');

try {
    fs.writeFileSync(file, JSON.stringify(sorted, null, 2) + '\n');
} catch (err) {
    console.error(
        `Failed to write sorted translations to "${file}":`,
        err && err.message ? err.message : err,
    );
    process.exit(1);
}
