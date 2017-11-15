import angular from 'npm/angular';

import ConsulConfigurationCtrl from './consul-configuration.controller'

export default angular.module('sp.consulConfiguration', [])
    .controller('ConsulConfigurationCtrl', ConsulConfigurationCtrl)
    .name;
