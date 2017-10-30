import angular from 'npm/angular';

import spServices from '../services/services.module'

import AppFileDownloadCtrl from './app-file-download.controller'

export default angular.module('sp.appfiledownload', [spServices])
    .controller('AppFileDownloadCtrl', AppFileDownloadCtrl)
    .name;
