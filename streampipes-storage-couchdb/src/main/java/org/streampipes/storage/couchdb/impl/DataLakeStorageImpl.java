/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.streampipes.storage.couchdb.impl;

import org.streampipes.model.datalake.DataLakeMeasure;
import org.streampipes.storage.api.IDataLakeStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class DataLakeStorageImpl extends AbstractDao<DataLakeMeasure> implements IDataLakeStorage {

    public DataLakeStorageImpl() {
        super(Utils::getCouchDbDataLakeClient, DataLakeMeasure.class);
    }

    @Override
    public boolean storeDataLakeMeasure(DataLakeMeasure measure) {
        return persist(measure);
    }

    @Override
    public List<DataLakeMeasure> getAllDataLakeMeasures() {
        List<DataLakeMeasure> dataLakeMeasures = findAll();
        return dataLakeMeasures;
    }
}
