import * as angular from 'angular';

export class SpButtonDirective {

    $mdButtonInkRipple: any;
    $mdTheming: any;
    $mdAria: any;
    $mdInteraction: any;
    spRaised: any;
    restrict: any;
    transclude: any;
    template: any;
    replace: any;
    postLink: any;


    constructor($mdButtonInkRipple, $mdTheming, $mdAria, $mdInteraction) {
        this.$mdButtonInkRipple = $mdButtonInkRipple;
        this.$mdTheming = $mdTheming;
        this.$mdAria = $mdAria;
        this.$mdInteraction = $mdInteraction;
        this.restrict = 'EA';
        this.transclude = true;
        this.replace = true;
        this.template = this.getTemplate();
        this.postLink = this.link;
    }

    isAnchor(attr) {
        return angular.isDefined(attr.href) || angular.isDefined(attr.ngHref) || angular.isDefined(attr.ngLink) || angular.isDefined(attr.uiSref);
    }

    getTemplate() {
        return '<button class="sp-button" ng-transclude></button>';
    }

    link(scope, element, attr) {
        this.$mdTheming(element);

        if ('spButtonBlue' in attr) {
            element.addClass('sp-button-blue');
        }

        if ('spButtonGray' in attr) {
            element.addClass('sp-button-gray');
        }

        if ('spButtonFlat' in attr) {
            element.addClass('sp-button-flat');
        }

        if ('spButtonIcon' in attr) {
            element.addClass('sp-button-icon');
        }

        if ('spButtonSmallPadding' in attr) {
            element.addClass('sp-button-small-padding');
        }

        this.$mdButtonInkRipple.attach(scope, element);
        // //
        // // // Use async expect to support possible bindings in the button label
        this.$mdAria.expectWithoutText(element, 'aria-label');


        // disabling click event when disabled is true
        element.on('click', e => {
            if (attr.disabled === true) {
                e.preventDefault();
                e.stopImmediatePropagation();
            }
        });

        if (!element.hasClass('md-no-focus')) {

            element.on('focus', () => {

                // Only show the focus effect when being focused through keyboard interaction or programmatically
                if (!this.$mdInteraction.isUserInvoked() || this.$mdInteraction.getLastInteractionType() === 'keyboard') {
                    element.addClass('md-focused');
                }

            });

            element.on('blur', function() {
                element.removeClass('md-focused');
            });
        }

    }
}

SpButtonDirective.$inject=['$mdButtonInkRipple', '$mdTheming', '$mdAria', '$mdInteraction'];