/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.extensions.management.connect.adapter.model.generic;

import org.apache.streampipes.extensions.api.connect.IFormat;
import org.apache.streampipes.extensions.api.connect.IParser;
import org.apache.streampipes.extensions.api.connect.IProtocol;
import org.apache.streampipes.extensions.api.connect.exception.AdapterException;
import org.apache.streampipes.extensions.api.connect.exception.ParseException;
import org.apache.streampipes.extensions.management.connect.adapter.Adapter;
import org.apache.streampipes.extensions.management.connect.adapter.AdapterRegistry;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericAdapter<T extends AdapterDescription> extends Adapter<T> {

  private static final Logger logger = LoggerFactory.getLogger(Adapter.class);
  protected IProtocol protocol;

  public GenericAdapter(T adapterDescription) {
    super(adapterDescription);
  }

  public GenericAdapter(T adapterDescription, boolean debug) {
    super(adapterDescription, debug);
  }

  public GenericAdapter() {
    super();
  }

  public abstract GenericAdapterDescription getAdapterDescription();

  public abstract void setProtocol(IProtocol protocol);

  @Override
  public void startAdapter() throws AdapterException {

    GenericAdapterDescription adapterDescription = getAdapterDescription();

    IParser parser = getParser(adapterDescription);
    IFormat format = getFormat(adapterDescription);

    ProtocolDescription protocolDescription = ((GenericAdapterDescription) adapterDescription).getProtocolDescription();

    IProtocol protocolInstance = this.protocol.getInstance(protocolDescription, parser, format);
    this.protocol = protocolInstance;

    //TODO remove
    EventSchema eventSchema = adapterDescription.getEventSchema();
    this.protocol.setEventSchema(eventSchema);

    logger.debug("Start adatper with format: " + format.getId() + " and " + protocol.getId());

    protocolInstance.run(adapterPipeline);
  }


  @Override
  public GuessSchema getSchema(AdapterDescription adapterDescription) throws AdapterException, ParseException {
    IParser parser = getParser((GenericAdapterDescription) adapterDescription);
    IFormat format = getFormat((GenericAdapterDescription) adapterDescription);

    ProtocolDescription protocolDescription = ((GenericAdapterDescription) adapterDescription).getProtocolDescription();

    IProtocol protocolInstance = this.protocol.getInstance(protocolDescription, parser, format);

    logger.debug("Extract schema with format: " + format.getId() + " and " + protocol.getId());

    return protocolInstance.getGuessSchema();
  }

  private IParser getParser(GenericAdapterDescription adapterDescription) throws AdapterException {
    if (adapterDescription.getFormatDescription() == null) {
      throw new AdapterException("Format description of Adapter ist empty");
    }
    return AdapterRegistry.getAllParsers().get(adapterDescription.getFormatDescription().getAppId())
        .getInstance(adapterDescription.getFormatDescription());
  }

  private IFormat getFormat(GenericAdapterDescription adapterDescription) {
    return AdapterRegistry.getAllFormats().get(adapterDescription.getFormatDescription().getAppId())
        .getInstance(adapterDescription.getFormatDescription());
  }

}
