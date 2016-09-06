'use strict';

// import Webpack plugins
const cleanPlugin = require('clean-webpack-plugin');
const ngAnnotatePlugin = require('ng-annotate-webpack-plugin');
const webpack = require('webpack');
const BowerWebpackPlugin = require("bower-webpack-plugin");

// define Webpack configuration object to be exported
let config = {
	context: `${__dirname}/app`,
	entry: 
		'./app.module.js',
	output: {
		path: `${__dirname}/`,
		filename: 'bundle.js'
	},
	resolve: {
		alias: {
			'npm': `${__dirname}/node_modules`
		}
	},
	module: {
		loaders: [
			{
				test: /\.css$/,
				loader: 'style!css'
			},
			{ 
				test   : /.js$/,
				loader : 'babel-loader',
				query: {
					presets: ['es2015']
				}
			}
		]
	},
	devServer: {
		contentBase: `${__dirname}/`,
		port: 8081
		//inline: true
	},
	plugins: [
		new cleanPlugin(['dist']),
		new ngAnnotatePlugin({
			add: true
		}),
	]
};

module.exports = config;
