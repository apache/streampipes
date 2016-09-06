import angular from 'npm/angular';

import spServices from '../services/services.module'

import RegisterCtrl from './register.controller'
import SsoCtrl from './sso.controller'
import LoginCtrl from './login.controller'
import SetupCtrl from './setup.controller'

export default angular.module('sp.login', [spServices])
	.controller('RegisterCtrl', RegisterCtrl)
	.controller('SsoCtrl', SsoCtrl)
	.controller('LoginCtrl', LoginCtrl)
	.controller('SetupCtrl', SetupCtrl)
	.name;
