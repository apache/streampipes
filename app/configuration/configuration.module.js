import angular from 'npm/angular';

import ConfigurationCtrl from './configuration.controller'
import ConfigurationRestService from './configuration-rest.service'

export default angular.module('sp.configuration', [])
    .controller('ConfigurationCtrl', ConfigurationCtrl)
    .factory('ConfigurationRestService', ConfigurationRestService)
    .name;
