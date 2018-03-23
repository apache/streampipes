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

package org.streampipes.logging.impl;

import org.streampipes.logging.api.Logger;
import org.slf4j.LoggerFactory;

public class PeLogger implements Logger {

    private org.slf4j.Logger LOG;

    private String prefix;



    //public PeLogger(Class clazz, String correspondingPipeline, String peUri, PeConfig peConfig){
    public PeLogger(Class clazz, String correspondingPipeline, String peUri){
        this.prefix =  "USERLOG" + " - "
                      // + "serviceName: " + peConfig.getName() + " - "
                       + "correspondingPipeline: " + correspondingPipeline + " - "
                       + "peURI: " + peUri + " - ";

        LOG = LoggerFactory.getLogger(clazz);
    }

    public void info(String s) {
        LOG.info(prefix + s);
    }

    public void trace(String s) {
        LOG.trace(prefix + s);
    }

    public void debug(String s) {
        LOG.debug(prefix + s);
    }

    public void error(String s) {
        LOG.error(prefix + s);
    }

    public void warn(String s) {
        LOG.warn(prefix + s);
    }

}
