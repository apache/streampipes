import angular from 'npm/angular';

import ConfigurationCtrl from './configuration.controller'
import ConfigurationRestService from './configuration-rest.service'
import consulService from "./directives/consul-service/consul-service.component";

export default angular.module('sp.configuration', [])
    .controller('ConfigurationCtrl', ConfigurationCtrl)
    .factory('ConfigurationRestService', ConfigurationRestService)
    .component('consulService', consulService())
    .name;
