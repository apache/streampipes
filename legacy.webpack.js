'use strict';
const { AngularCompilerPlugin } = require('@ngtools/webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');

// import Webpack plugins
const cleanPlugin = require('clean-webpack-plugin');
const ngAnnotatePlugin = require('ng-annotate-webpack-plugin');
const webpack = require('webpack');
const BowerWebpackPlugin = require("bower-webpack-plugin");
var path = require('path');

// define Webpack configuration object to be exported
let config = {
    devtool: 'inline-source-map',
    //context: path.join(__dirname, 'src'),
    entry: {
        //polyfills: './polyfills.ts',
        app: './src/bootstrap.ts'
    },
    output: {
        path: path.join(process.cwd(), "dist"),
        filename: "[name].bundle.js",
        chunkFilename: "[id].chunk.js",
        crossOriginLoading: false
    },
    resolve: {
        alias: {
            'npm': path.join(__dirname, 'node_modules'),
            'legacy': path.join(__dirname, 'src', 'assets', 'lib'),
            "jquery-ui": path.join(__dirname, 'src', 'assets', 'lib', 'jquery-ui.min.js')
        }
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                loader: 'style!css'
            },
            {
                test: /.js$/,
                loader: 'babel-loader',
                query: {
                    presets: ['es2015']
                }
            },
            {
                test: /\.ts$/,
                loader: '@ngtools/webpack'
            },
            {
                test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)$/,
                loader: 'file-loader?name=assets/[name].[hash].[ext]'
            },
        ]
    },
    //devtool: 'source-map',
    devServer: {
        contentBase: `${__dirname}/src/`,
        port: 8082,
        proxy: {
            '/streampipes-backend': {
                target: 'http://localhost:8030',
                secure: false
            },
            '/visualizablepipeline': {
                target: 'http://localhost:5984',
                secure: false
            },
            '/dashboard': {
                target: 'http://localhost:5984',
                secure: false
            },
            '/pipeline': {
                target: 'http://localhost:5984',
                secure: false
            },
            '/streampipes/ws': {
                target: 'ws://ipe-koi04.fzi.de:61614',
                ws: true,
                secure: false
            }
        }
        //inline: true
    },
    plugins: [
        new AngularCompilerPlugin({
            "mainPath": "bootstrap.ts",
            "platform": 0,
            "hostReplacementPaths": {
                "environments\\environment.ts": "environments\\environment.ts"
            },
            "sourceMap": true,
            "tsConfigPath": "src\\tsconfig.app.json",
            "skipCodeGeneration": true,
            "compilerOptions": {}
        }),
        new HtmlWebpackPlugin({
            template: './src/index.html',
            filename: 'index.html'
        }),
        new CopyWebpackPlugin([
            {
                "context": "src",
                "to": "",
                "from": {
                    "glob": "src/assets/**/*",
                    "dot": true
                }
            },
            {
                "context": "src",
                "to": "",
                "from": {
                    "glob": "src/favicon.ico",
                    "dot": true
                }
            }
        ], {
            "ignore": [
                ".gitkeep",
                "**/.DS_Store",
                "**/Thumbs.db"
            ],
            "debug": "warning"
        }),
        //new cleanPlugin(['dist']),
        /*new ngAnnotatePlugin({
            add: true
        }),*/
        new webpack.ProvidePlugin({
            $: "jquery",
            jQuery: "jquery",
            "window.jQuery": "jquery",
        }),
        //new webpack.HotModuleReplacementPlugin(),
        //new webpack.OldWatchingPlugin()
        //new webpack.optimize.UglifyJsPlugin({
        //compress: {
        //warnings: false
        //}
        //})
    ]
};

module.exports = config;
