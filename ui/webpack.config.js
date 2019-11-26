module.exports = (env) => {
    return require(`./webpack.config.${env}.js`)
}