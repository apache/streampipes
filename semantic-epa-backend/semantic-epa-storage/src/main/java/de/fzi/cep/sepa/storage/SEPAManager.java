package de.fzi.cep.sepa.storage;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.SEP;
import de.fzi.cep.sepa.model.impl.SEPA;


public enum SEPAManager {
	
	INSTANCE;

	List<SEPA> storedSEPAs;
	List<SEP> storedSEPs;
	List<EventStream> storedEventStreams;
	
	
	SEPAManager()
	{
		this.storedSEPAs = new ArrayList<SEPA>();
		this.storedSEPs = new ArrayList<SEP>();
		this.storedEventStreams = new ArrayList<EventStream>();
	}
	
	public List<SEPA> getStoredSEPAs() {
		return storedSEPAs;
	}
	public void setStoredSEPAs(List<SEPA> storedSEPAs) {
		this.storedSEPAs = storedSEPAs;
	}
	public List<SEP> getStoredSEPs() {
		return storedSEPs;
	}
	public void setStoredSEPs(List<SEP> storedSEPs) {
		this.storedSEPs = storedSEPs;
	}
	
	public boolean addSEPA(SEPA SEPA)
	{
		return storedSEPAs.add(SEPA);
	}
	
	public boolean addSEP(SEP SEP)
	{
		return storedSEPs.add(SEP);
	}

	public List<EventStream> getStoredEventStreams() {
		return storedEventStreams;
	}

	public void setStoredEventStreams(List<EventStream> storedEventStreams) {
		this.storedEventStreams = storedEventStreams;
	}
	
	public List<SEP> getSEPById(String id)
	{
		List<SEP> result = new ArrayList<SEP>();
		for(SEP s : storedSEPs)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<SEPA> getSEPAById(String id)
	{
		List<SEPA> result = new ArrayList<SEPA>();
		for(SEPA s : storedSEPAs)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
	/*
	public List<EventStream> getEventStreamById(String id)
	{
		List<EventStream> result = new ArrayList<EventStream>();
		for(EventStream s : storedEventStreams)
		{
			if (s.getElementId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<SEP> getSEPsByEventStream(String id)
	{
		List<SEP> result = new ArrayList<SEP>();
		for(SEP s : storedSEPs)
		{
			if (s.getEventStreamId().equals(id))
				result.add(s);
		}
		return result;
	}
	
	public List<EventStream> getEventStreamsByDomain(String domain)
	{
		List<EventStream> result = new ArrayList<EventStream>();
		for(EventStream s : storedEventStreams)
		{
			for(Domain d : s.getDomain())
			if (d.toString().equals(domain))
				result.add(s);
		}
		return result;
	}
	*/
}
