/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import Prism from 'prismjs';

nagPrism.$inject = ['$compile'];


export default function nagPrism($compile) {
	return {
		restrict: 'A',
		transclude: true,
		scope: {
			source: '@'
		},
		link: function(scope, element, attrs, controller, transclude) {
			scope.$watch('source', function(v) {
				console.log(v);
				v = scope.escape(v);
				element.find("code").html(v);

				Prism.highlightElement(element.find("code")[0]);
			});

			scope.entityMap = {
				"&": "&amp;",
				"<": "&lt;",
				">": "&gt;",
				'"': '&quot;',
				"'": '&#39;',
				"/": '&#x2F;'
			};

			scope.escape = function(str) {
				return String(str).replace(/[&<>"'\/]/g, function (s) {
					return scope.entityMap[s];
				});
			}

			transclude(function(clone) {
				if (clone.html() !== undefined) {
					element.find("code").html(clone.html());
					$compile(element.contents())(scope.$parent);
				}
			});
		},
		template: "<code></code>"
	};
};
