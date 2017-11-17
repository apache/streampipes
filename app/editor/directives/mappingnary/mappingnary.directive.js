mappingPropertyNary.$inject = [];

export default function mappingPropertyNary() {

	return {
		restrict : 'E',
		templateUrl : 'app/editor/directives/mappingnary/mappingnary.tmpl.html',
		scope : {
			staticProperty : "=",
			displayRecommended: "="
		},
		link: function (scope) {

			scope.toggle = function(property, staticProperty) {
				if (scope.exists(property, staticProperty)) {
					remove(property, staticProperty);
				} else {
					add(property, staticProperty);
				}
			}

			scope.exists = function(property, staticProperty) {
				if (!staticProperty.properties.mapsTo) return false;
				return staticProperty.properties.mapsTo.indexOf(property.properties.elementId) > -1;
			}

			var add = function(property, staticProperty) {
				if (!staticProperty.properties.mapsTo) {
					staticProperty.properties.mapsTo = [];
				}
				staticProperty.properties.mapsTo.push(property.properties.elementId);
			}

			var remove = function(property, staticProperty) {
				var index = staticProperty.properties.mapsTo.indexOf(property.properties.elementId);
				staticProperty.properties.mapsTo.splice(index, 1);
			}

		}
	}

};
