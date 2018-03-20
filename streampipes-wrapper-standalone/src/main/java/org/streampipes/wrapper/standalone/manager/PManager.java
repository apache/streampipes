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

package org.streampipes.wrapper.standalone.manager;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.dataformat.SpDataFormatManager;
import org.streampipes.messaging.SpProtocolDefinition;
import org.streampipes.messaging.SpProtocolManager;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;

import java.util.Optional;

public class PManager {

  public static <T extends TransportProtocol> Optional<SpProtocolDefinition<T>>
  getProtocolDefinition(T
                                protocol) {
    return SpProtocolManager.INSTANCE.findDefinition(protocol);
  }

  public static Optional<SpDataFormatDefinition> getDataFormat(TransportFormat format) throws
          SpRuntimeException {
    return SpDataFormatManager.INSTANCE.findDefinition(format);
  }
}
