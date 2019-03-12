const merge = require('webpack-merge');
const baseConfig = require('./webpack.config.base.js');

module.exports = merge(baseConfig, {
    mode: "development",
    devServer: {
        port: 8082,
        proxy: {
            '/streampipes-connect': {
                target: 'http://localhost:8099',
                pathRewrite: { '^/streampipes-connect': '' },
                secure: false,
            },
            '/streampipes-backend': {
                target: 'http://localhost:8030',
                secure: false,
            },
            '/visualizablepipeline': {
                target: 'http://localhost:5984',
                secure: false,
            },
            '/dashboard': {
                target: 'http://localhost:5984',
                secure: false,
            },
            '/pipeline': {
                target: 'http://localhost:5984',
                secure: false,
            },
            '/streampipes/ws': {
                target: 'ws://localhost:61614',
                ws: true,
                secure: false,
            },
        },
    },
});