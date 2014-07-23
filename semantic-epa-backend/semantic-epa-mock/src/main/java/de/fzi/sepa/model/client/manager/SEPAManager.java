package de.fzi.sepa.model.client.manager;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.client.Domain;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SourceClient;
import de.fzi.cep.sepa.model.client.StreamClient;


public enum SEPAManager {
	
	INSTANCE;

	List<SEPAClient> storedSEPAs;
	List<StreamClient> storedSEPs;
	List<SourceClient> storedSources;
	
	
	SEPAManager()
	{
		this.storedSEPAs = new ArrayList<SEPAClient>();
		this.storedSEPs = new ArrayList<StreamClient>();
		this.storedSources = new ArrayList<SourceClient>();
	}
	
	public List<SEPAClient> getStoredSEPAs() {
		return storedSEPAs;
	}
	public void setStoredSEPAs(List<SEPAClient> storedSEPAs) {
		this.storedSEPAs = storedSEPAs;
	}
	public List<StreamClient> getStoredSEPs() {
		return storedSEPs;
	}
	public void setStoredSEPs(List<StreamClient> storedSEPs) {
		this.storedSEPs = storedSEPs;
	}
	
	public boolean addSEPA(SEPAClient sepaMock)
	{
		return storedSEPAs.add(sepaMock);
	}
	
	public boolean addSEP(StreamClient sepMock)
	{
		return storedSEPs.add(sepMock);
	}

	public List<SourceClient> getStoredSources() {
		return storedSources;
	}

	public void setStoredSources(List<SourceClient> storedSources) {
		this.storedSources = storedSources;
	}
	
	public List<StreamClient> getSEPById(String id)
	{
		List<StreamClient> result = new ArrayList<StreamClient>();
		for(StreamClient s : storedSEPs)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<SEPAClient> getSEPAById(String id)
	{
		List<SEPAClient> result = new ArrayList<SEPAClient>();
		for(SEPAClient s : storedSEPAs)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<SourceClient> getSourceById(String id)
	{
		List<SourceClient> result = new ArrayList<SourceClient>();
		for(SourceClient s : storedSources)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<StreamClient> getSEPsBySource(String id)
	{
		List<StreamClient> result = new ArrayList<StreamClient>();
		for(StreamClient s : storedSEPs)
		{
			if (s.getSourceId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<SourceClient> getSourcesByDomain(String domain)
	{
		List<SourceClient> result = new ArrayList<SourceClient>();
		for(SourceClient s : storedSources)
		{
			for(Domain d : s.getDomain())
			if (d.toString().equals(domain))
				result.add(s);
		}
		return result;
	}
}
