const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const { HashedModuleIdsPlugin } = require('webpack');
const TerserPlugin = require('terser-webpack-plugin');

const merge = require('webpack-merge');
const baseConfig = require('./webpack.config.base.js');

module.exports = merge(baseConfig, {
    mode: 'production',
    optimization: {
        // noEmitOnErrors: true,
        // runtimeChunk: 'single',
        // splitChunks: {
        //     cacheGroups: {
        //         default: {
        //             chunks: 'async',
        //             minChunks: 2,
        //             priority: 10
        //         },
        //         common: {
        //             name: 'common',
        //             chunks: 'async',
        //             minChunks: 2,
        //             enforce: true,
        //             priority: 5
        //         },
        //         vendors: false,
        //         vendor: false
        //     }
        // },
        minimizer: [
            // new HashedModuleIdsPlugin(),
            new TerserPlugin({
                terserOptions: {
                    ecma: undefined,
                    warnings: false,
                    parse: {},
                    compress: {},
                    mangle: false, // Note `mangle.properties` is `false` by default.
                    module: false,
                    output: null,
                    toplevel: false,
                    nameCache: null,
                    ie8: false,
                    keep_classnames: true,
                    keep_fnames: true,
                    safari10: false,
                },
            }),
        ]
    }
});