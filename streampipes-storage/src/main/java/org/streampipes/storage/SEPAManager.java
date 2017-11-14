package org.streampipes.storage;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.model.SpDataStream;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.model.graph.DataProcessorDescription;


public enum SEPAManager {
	
	INSTANCE;

	List<DataProcessorDescription> storedSEPAs;
	List<DataSourceDescription> storedSEPs;
	List<SpDataStream> storedSpDataStreams;
	
	
	SEPAManager()
	{
		this.storedSEPAs = new ArrayList<DataProcessorDescription>();
		this.storedSEPs = new ArrayList<DataSourceDescription>();
		this.storedSpDataStreams = new ArrayList<SpDataStream>();
	}
	
	public List<DataProcessorDescription> getStoredSEPAs() {
		return storedSEPAs;
	}
	public void setStoredSEPAs(List<DataProcessorDescription> storedSEPAs) {
		this.storedSEPAs = storedSEPAs;
	}
	public List<DataSourceDescription> getStoredSEPs() {
		return storedSEPs;
	}
	public void setStoredSEPs(List<DataSourceDescription> storedSEPs) {
		this.storedSEPs = storedSEPs;
	}
	
	public boolean addSEPA(DataProcessorDescription SEPA)
	{
		return storedSEPAs.add(SEPA);
	}
	
	public boolean addSEP(DataSourceDescription SEP)
	{
		return storedSEPs.add(SEP);
	}

	public List<SpDataStream> getStoredSpDataStreams() {
		return storedSpDataStreams;
	}

	public void setStoredSpDataStreams(List<SpDataStream> storedSpDataStreams) {
		this.storedSpDataStreams = storedSpDataStreams;
	}
	
	public List<DataSourceDescription> getSEPById(String id)
	{
		List<DataSourceDescription> result = new ArrayList<DataSourceDescription>();
		for(DataSourceDescription s : storedSEPs)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<DataProcessorDescription> getSEPAById(String id)
	{
		List<DataProcessorDescription> result = new ArrayList<DataProcessorDescription>();
		for(DataProcessorDescription s : storedSEPAs)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
}
