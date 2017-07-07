package de.fzi.cep.sepa.rest.api;

import de.fzi.cep.sepa.kpi.KpiRequest;

import javax.ws.rs.core.Response;

/**
 * Created by riemer on 03.10.2016.
 */
public interface IKpiPipeline {

    Response addKpi(KpiRequest kpiRequest);

    Response deleteKpi(String kpiId);

    Response updateKpi(KpiRequest kpiRequest);
}
