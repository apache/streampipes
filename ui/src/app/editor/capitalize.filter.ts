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

capitalize.$inject = [];

export default function capitalize() {
	return function(input) {
		if (input!=null) {
			input = input.replace(/_/g, " ");
			var stringArr = input.split(" ");
			var result = "";
			var cap = stringArr.length;
			for(var x = 0; x < cap; x++) {
				stringArr[x] = stringArr[x].toLowerCase();
				if(x === cap - 1) {
					result += stringArr[x].substring(0,1).toUpperCase() + stringArr[x].substring(1);
				} else {
					result += stringArr[x].substring(0,1).toUpperCase() + stringArr[x].substring(1) + " ";
				}
			}
			return result;
		}
	}
};
