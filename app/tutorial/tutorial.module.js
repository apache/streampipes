import angular from 'npm/angular';

import TutorialCtrl from './tutorial.controller'
import slick from 'npm/slick-carousel';
import slickCarousel from 'npm/angular-slick-carousel';
import spServices from '../services/services.module';
import tutorialPage from './directives/tutorial-page.directive';

export default angular.module('sp.tutorial', [spServices, 'slickCarousel'])
    .controller('TutorialCtrl', TutorialCtrl)
    .directive('tutorialPage', tutorialPage)
    .name;