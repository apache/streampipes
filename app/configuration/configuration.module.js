import angular from 'angular';

import {ConfigurationCtrl} from './configuration.controller'
import {ConfigurationRestService} from './configuration-rest.service'
import {ConsulService} from "./directives/consul-service/consul-service.component";

export default angular.module('sp.configuration', [])
    .controller('ConfigurationCtrl', ConfigurationCtrl)
    .service('ConfigurationRestService', ConfigurationRestService)
    .component('consulService', ConsulService)
    .name;
