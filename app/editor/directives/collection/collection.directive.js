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
                console.log(sp);
                sp.properties.members.push(angular.copy(sp.properties.members[0]));
                sp.properties.members[sp.properties.members.length-1].properties.elementName =
                    sp.properties.members[sp.properties.members.length-1].properties.elementName.concat(makeId());
                console.log(sp);
                console.log(makeId);
            }

            scope.removeMember = function(sp, index) {
                sp.properties.members.splice(index, 1);
            }

            var makeId = function() {
                var text = "";
                var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

                for (var i = 0; i < 5; i++)
                    text += possible.charAt(Math.floor(Math.random() * possible.length));

                return text;
            }

        }
    }

};
