import angular from 'npm/angular';

import VisualizationNewCtrl from './visualizations-new.controller';

export default angular.module('sp.visualizationNew', [])
	.controller('VizCtrl', VisualizationNewCtrl)
 	.name;
