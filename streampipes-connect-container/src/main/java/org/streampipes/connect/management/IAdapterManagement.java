package org.streampipes.connect.management;

import org.streampipes.model.modelconnect.AdapterSetDescription;
import org.streampipes.model.modelconnect.AdapterStreamDescription;

public interface IAdapterManagement {

    String invokeStreamAdapter(AdapterStreamDescription adapterStreamDescription);

    String stopStreamAdapter(AdapterStreamDescription adapterStreamDescription);

    String invokeSetAdapter (AdapterSetDescription adapterSetDescription);

    String stopSetAdapter (AdapterSetDescription adapterSetDescription);
}
