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
            new HashedModuleIdsPlugin(),
            // new TerserPlugin({
            //     cache: true,
            //     parallel: true,
            //     sourceMap: false, // Must be set to true if using source-maps in production
            //     terserOptions: {
            //         comments: false
            //         // https://github.com/webpack-contrib/terser-webpack-plugin#terseroptions
            //     }
            // })
        ]
    }
});