const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const { HashedModuleIdsPlugin } = require('webpack');

const merge = require('webpack-merge');
const baseConfig = require('./webpack.config.base.js');

module.exports = merge(baseConfig, {
    mode: 'production',
    optimization: {
        noEmitOnErrors: true,
        runtimeChunk: 'single',
        splitChunks: {
            cacheGroups: {
                default: {
                    chunks: 'async',
                    minChunks: 2,
                    priority: 10
                },
                common: {
                    name: 'common',
                    chunks: 'async',
                    minChunks: 2,
                    enforce: true,
                    priority: 5
                },
                vendors: false,
                vendor: false
            }
        },
        minimizer: [
            new HashedModuleIdsPlugin(),
            new UglifyJSPlugin({
                sourceMap: true,
                cache: true,
                parallel: true,
                uglifyOptions: {
                    safari10: true,
                    output: {
                        ascii_only: true,
                        comments: false,
                        webkit: true,
                    },
                    compress: {
                        pure_getters: true,
                        passes: 3,
                        inline: 3,
                    }
                }
            })
        ]
    }
});