collectionStaticProperty.$inject = [];

export default function collectionStaticProperty() {

    return {
        restrict: 'E',
        templateUrl: 'app/editor/directives/collection/collection.tmpl.html',
        scope: {
            staticProperty: "="
        },
        link: function (scope) {
            
            scope.addMember = function(sp) {
                sp.properties.members.push(angular.copy(sp.properties.members[0]));
            }

            scope.removeMember = function(sp, index) {
                sp.properties.members.splice(index, 1);
            }
        }
    }

};
