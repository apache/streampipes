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

package org.streampipes.container.embedded.init;

import org.streampipes.container.init.ModelSubmitter;
import org.streampipes.container.model.PeConfig;
import org.streampipes.container.util.ConsulUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public abstract class ContainerModelSubmitter extends ModelSubmitter implements ServletContextListener {

//
//    /**
//     * This Method needs to be implemented to instantiate an client container
//     * Use the DeclarersSingleton to register the declarers
//     */
    public abstract void init();

    public void init(PeConfig peConfig) {
        ConsulUtil.registerPeService(
                peConfig.getId(),
                peConfig.getHost(),
                peConfig.getPort()
        );
    }

    @Override
    public void contextInitialized(ServletContextEvent arg) {
        init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
