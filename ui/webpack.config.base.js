/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

const { AngularCompilerPlugin } = require('@ngtools/webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const path = require('path');


module.exports = {
    entry: {
        polyfills: './src/polyfills.ts',
        main: './src/main.ts',
        style: './src/scss/main.scss',
    },
    output: {
        path: path.join(process.cwd(), 'dist'),
        publicPath: '/',
        filename: '[name].bundle.js',
        crossOriginLoading: false,
    },
    module: {
        rules: [
            {
                test: /\.ts$/,
                loader: '@ngtools/webpack',
            },
            {
                test: /\.html$/,
                loader: 'raw-loader',
            },
            {
                test: /\.css$/,
                loaders: ['to-string-loader', 'css-loader'],
            },
            {
                test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)$/,
                loader: 'file-loader?name=assets/[name].[hash].[ext]',
            },
            {
                test: /\.(scss|sass)$/,
                use: [
                    { loader: 'style-loader' },
                    { loader: 'css-loader', options: { sourceMap: true } },
                    { loader: 'sass-loader', options: { sourceMap: true } }
                ],
                include: path.join(__dirname, 'src',  'scss')
            },
            {
                test: /\.(scss|sass)$/,
                use: [
                    'to-string-loader',
                    { loader: 'css-loader', options: { sourceMap: true } },
                    { loader: 'sass-loader', options: { sourceMap: true } }
                ],
                include: path.join(__dirname, 'src', 'app')
            }
        ],
    },
    resolve: {
        alias: {
            npm: path.join(__dirname, 'node_modules'),
            legacy: path.join(__dirname, 'src', 'assets', 'lib'),
            'jquery-ui': path.join(
                __dirname,
                'src',
                'assets',
                'lib',
                'jquery-ui.min.js'
            ),
        },
        extensions: ['.ts', '.js'],
    },
    plugins: [
        new AngularCompilerPlugin({
            mainPath: 'main.ts',
            platform: 0,
            sourceMap: false,
            tsConfigPath: path.join(__dirname, 'src', 'tsconfig.app.json'),
            skipCodeGeneration: false,
            compilerOptions: {},
        }),
        new CopyWebpackPlugin(
            [
                {
                    to: '',
                    context: 'src/',
                    from: {
                        glob: 'assets/**/*',
                        dot: true,
                    },
                },
                {
                    to: '',
                    context: 'src/',
                    from: {
                        glob: '/favicon.ico',
                        dot: true,
                    },
                },
            ],
            {
                ignore: ['.gitkeep', '**/.DS_Store', '**/Thumbs.db'],
                debug: 'warning',
            }
        ),
        new MiniCssExtractPlugin(),
        new HtmlWebpackPlugin({
            template: 'src/index.html',
            chunks: ['polyfills', 'main', 'style'],
            chunksSortMode: 'manual',
        }),
    ],

};
