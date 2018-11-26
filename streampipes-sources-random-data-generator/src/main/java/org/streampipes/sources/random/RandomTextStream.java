/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sources.random;


import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;

public class RandomTextStream implements EventStreamDeclarer {

	@Override
	public SpDataStream declareModel(DataSourceDescription sep) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeStream() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isExecutable() {
		// TODO Auto-generated method stub
		return false;
	}

}
