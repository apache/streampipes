import angular from 'npm/angular';

tutorialPage.$inject = [];
'use strict';

export default function tutorialPage() {

    return {
        restrict: 'E',
        templateUrl: 'app/tutorial/directives/tutorial-page.tmpl.html',
        scope: {
            page : "="
        },
        controller: function ($scope) {

        },
        link: function postLink(scope, element) {

        }
    }
};