import angular from 'npm/angular';

import ConfigurationCtrl from './configuration.controller'

export default angular.module('sp.configuration', [])
    .controller('ConfigurationCtrl', ConfigurationCtrl)
    .name;
