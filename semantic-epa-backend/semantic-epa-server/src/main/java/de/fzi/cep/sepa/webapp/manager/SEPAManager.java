package de.fzi.cep.sepa.webapp.manager;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.mock.Domain;
import de.fzi.cep.sepa.mock.SEPAMock;
import de.fzi.cep.sepa.mock.SEPMock;
import de.fzi.cep.sepa.mock.SourceMock;


public enum SEPAManager {
	
	INSTANCE;

	List<SEPAMock> storedSEPAs;
	List<SEPMock> storedSEPs;
	List<SourceMock> storedSources;
	
	
	SEPAManager()
	{
		this.storedSEPAs = new ArrayList<SEPAMock>();
		this.storedSEPs = new ArrayList<SEPMock>();
		this.storedSources = new ArrayList<SourceMock>();
	}
	
	public List<SEPAMock> getStoredSEPAs() {
		return storedSEPAs;
	}
	public void setStoredSEPAs(List<SEPAMock> storedSEPAs) {
		this.storedSEPAs = storedSEPAs;
	}
	public List<SEPMock> getStoredSEPs() {
		return storedSEPs;
	}
	public void setStoredSEPs(List<SEPMock> storedSEPs) {
		this.storedSEPs = storedSEPs;
	}
	
	public boolean addSEPA(SEPAMock sepaMock)
	{
		return storedSEPAs.add(sepaMock);
	}
	
	public boolean addSEP(SEPMock sepMock)
	{
		return storedSEPs.add(sepMock);
	}

	public List<SourceMock> getStoredSources() {
		return storedSources;
	}

	public void setStoredSources(List<SourceMock> storedSources) {
		this.storedSources = storedSources;
	}
	
	public List<SEPMock> getSEPById(String id)
	{
		List<SEPMock> result = new ArrayList<SEPMock>();
		for(SEPMock s : storedSEPs)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<SEPAMock> getSEPAById(String id)
	{
		List<SEPAMock> result = new ArrayList<SEPAMock>();
		for(SEPAMock s : storedSEPAs)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<SourceMock> getSourceById(String id)
	{
		List<SourceMock> result = new ArrayList<SourceMock>();
		for(SourceMock s : storedSources)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<SEPMock> getSEPsBySource(String id)
	{
		List<SEPMock> result = new ArrayList<SEPMock>();
		for(SEPMock s : storedSEPs)
		{
			if (s.getSourceId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<SourceMock> getSourcesByDomain(String domain)
	{
		List<SourceMock> result = new ArrayList<SourceMock>();
		for(SourceMock s : storedSources)
		{
			for(Domain d : s.getDomain())
			if (d.toString().equals(domain))
				result.add(s);
		}
		return result;
	}
	
	
	
	
	
}
