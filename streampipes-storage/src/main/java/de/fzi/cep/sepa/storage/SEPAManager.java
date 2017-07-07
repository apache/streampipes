package de.fzi.cep.sepa.storage;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;


public enum SEPAManager {
	
	INSTANCE;

	List<SepaDescription> storedSEPAs;
	List<SepDescription> storedSEPs;
	List<EventStream> storedEventStreams;
	
	
	SEPAManager()
	{
		this.storedSEPAs = new ArrayList<SepaDescription>();
		this.storedSEPs = new ArrayList<SepDescription>();
		this.storedEventStreams = new ArrayList<EventStream>();
	}
	
	public List<SepaDescription> getStoredSEPAs() {
		return storedSEPAs;
	}
	public void setStoredSEPAs(List<SepaDescription> storedSEPAs) {
		this.storedSEPAs = storedSEPAs;
	}
	public List<SepDescription> getStoredSEPs() {
		return storedSEPs;
	}
	public void setStoredSEPs(List<SepDescription> storedSEPs) {
		this.storedSEPs = storedSEPs;
	}
	
	public boolean addSEPA(SepaDescription SEPA)
	{
		return storedSEPAs.add(SEPA);
	}
	
	public boolean addSEP(SepDescription SEP)
	{
		return storedSEPs.add(SEP);
	}

	public List<EventStream> getStoredEventStreams() {
		return storedEventStreams;
	}

	public void setStoredEventStreams(List<EventStream> storedEventStreams) {
		this.storedEventStreams = storedEventStreams;
	}
	
	public List<SepDescription> getSEPById(String id)
	{
		List<SepDescription> result = new ArrayList<SepDescription>();
		for(SepDescription s : storedSEPs)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<SepaDescription> getSEPAById(String id)
	{
		List<SepaDescription> result = new ArrayList<SepaDescription>();
		for(SepaDescription s : storedSEPAs)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
}
