customizeDialog.$inject = [];

export default function customizeDialog() {

    return {
        restrict: 'E',
        templateUrl: 'app/editor/directives/customize/customize-dialog.tmpl.html',
        scope: {
            staticProperty: "=",
            selectedElement: "="
        },
        link: function (scope) {


        }
    }

};
