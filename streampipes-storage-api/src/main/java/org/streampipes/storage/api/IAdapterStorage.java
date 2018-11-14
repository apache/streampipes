package org.streampipes.storage.api;

import org.streampipes.model.connect.adapter.AdapterDescription;

import java.util.List;

public interface IAdapterStorage {

    List<AdapterDescription> getAllAdapters();

    void storeAdapter(AdapterDescription adapter);

    void updateAdapter(AdapterDescription adapter);

    AdapterDescription getAdapter(String adapterId);

    void deleteAdapter(String adapterId);
}
