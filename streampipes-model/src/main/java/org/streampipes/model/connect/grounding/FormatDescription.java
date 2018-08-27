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

package org.streampipes.model.connect.grounding;


import org.streampipes.empire.annotations.Namespaces;
import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.NamedStreamPipesEntity;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.util.Cloner;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


@Namespaces({"sp", "https://streampipes.org/vocabulary/v1/"})
@RdfsClass("sp:FormatDescription")
@Entity
public class FormatDescription extends NamedStreamPipesEntity {



    @OneToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.ALL})
    @RdfProperty("sp:config")
    List<StaticProperty> config;

    public FormatDescription() {
        super();
        this.config = new ArrayList<>();
    }

    public FormatDescription(String uri, String name, String description) {
        super(uri, name, description);
        this.config = new ArrayList<>();
    }

    public FormatDescription(String uri, String name, String description, List<StaticProperty> config) {
        super(uri, name, description);
        this.config = config;
    }

    public FormatDescription(FormatDescription other) {
        super(other);
        this.config = new Cloner().staticProperties(other.getConfig());
    }


    public void addConfig(StaticProperty sp) {
        this.config.add(sp);
    }

    public List<StaticProperty> getConfig() {
        return config;
    }

    public void setConfig(List<StaticProperty> config) {
        this.config = config;
    }








//   public static void main(String... args) {
//        FormatDescription f = new FormatDescription("", "","");
//        FreeTextStaticProperty fts = new FreeTextStaticProperty("internal_name_value", "label_value",
//                "description value");
//        FreeTextStaticProperty fts1 = new FreeTextStaticProperty("internal_name_value1", "label_value1",
//                "description value1");
//        f.addConfig(fts);
////        f.addConfig(fts1);
//
//        ProtocolDescription p = new ProtocolDescription("", "","");
//        FreeTextStaticProperty fts2 = new FreeTextStaticProperty("internal_name_value2", "label_value2",
//                "description value2");
//
//        p.addConfig(fts2);
//
//        AdapterDescription a = new AdapterDescription();
//        a.setFormatDescription(f);
//        a.setProtocolDescription(p);
//
//        JsonLdTransformer jsonLdTransformer = new JsonLdTransformer();
//        try {
//            System.out.println(Utils.asString(jsonLdTransformer.toJsonLd(a)));
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InvalidRdfException e) {
//            e.printStackTrace();
//        } catch (RDFHandlerException e) {
//            e.printStackTrace();
//        }
//
//    }


    @Override
    public String toString() {
        return "FormatDescription{" +
                "config=" + config +
                '}';
    }
}
