const { AngularCompilerPlugin } = require('@ngtools/webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const path = require('path');

module.exports = {
    entry: {
        'app': './src/bootstrap.ts'
    },
    output: {
        path: path.join(process.cwd(), "dist"),
        publicPath: '/',
        filename: "[name].bundle.js",
        chunkFilename: "[id].chunk.js",
        crossOriginLoading: false
    },
    module: {
        rules: [
            {
                test: /\.ts$/,
                loader: '@ngtools/webpack'
            },
            {
                test: /\.html$/,
                loader: 'raw-loader'
            },
            {
                test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)$/,
                loader: 'file-loader?name=assets/[name].[hash].[ext]'
            }
        ]
    },
    resolve: {
        alias: {
            'npm': path.join(__dirname, 'node_modules'),
            'legacy': path.join(__dirname, 'src', 'assets', 'lib'),
            "jquery-ui": path.join(__dirname, 'src', 'assets', 'lib', 'jquery-ui.min.js')
        },
        extensions: ['.ts', '.js']
    },
    plugins: [
        new AngularCompilerPlugin({
            "mainPath": "bootstrap.ts",
            "platform": 0,
            "sourceMap": true,
            "tsConfigPath": path.join(__dirname, 'src', 'tsconfig.app.json'),
            "skipCodeGeneration": true,
            "compilerOptions": {}
        }),
        new CopyWebpackPlugin([
            {
                to: "",
                context: "src/",
                from: {
                    glob: "assets/**/*",
                    dot: true
                }
            },
            {
                to: "",
                context: "src/",
                from: {
                    glob: "/favicon.ico",
                    dot: true
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
        new HtmlWebpackPlugin({
            template: 'src/index.html'
        })
    ],
    devServer: {
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
                target: 'ws://localhost:61614',
                ws: true,
                secure: false
            }
        }
    }
};