const { AngularCompilerPlugin } = require('@ngtools/webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
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
        test: /\.(sass|scss)$/,
        loader: ExtractTextPlugin.extract(['css-loader', 'sass-loader']),
      },
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
      sourceMap: true,
      tsConfigPath: path.join(__dirname, 'src', 'tsconfig.app.json'),
      skipCodeGeneration: true,
      compilerOptions: {},
    }),
    new ExtractTextPlugin({
      // define where to save the file
      filename: '[name].bundle.css',
      allChunks: true,
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
    new HtmlWebpackPlugin({
      template: 'src/index.html',
      chunks: ['polyfills', 'main', 'style'],
      chunksSortMode: 'manual',
    }),
  ],
  devServer: {
    port: 8082,
    proxy: {
      '/streampipes-connect': {
        target: 'http://localhost:8099',
        pathRewrite: { '^/streampipes-connect': '' },
        secure: false,
      },
      '/streampipes-backend': {
        target: 'http://ipe-koi04.fzi.de',
        secure: false,
      },
      '/visualizablepipeline': {
        target: 'http://ipe-koi04.fzi.de:5984',
        secure: false,
      },
      '/dashboard': {
        target: 'http://ipe-koi04.fzi.de:5984',
        secure: false,
      },
      '/pipeline': {
        target: 'http://ipe-koi04.fzi.de:5984',
        secure: false,
      },
      '/streampipes/ws': {
        target: 'ws://ipe-koi04.fzi.de',
        ws: true,
        secure: false,
      },
    },
  },
};
