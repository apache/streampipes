package de.fzi.cep.sepa.esper.debs.c1;

import java.util.Comparator;

public class CellDataComparator implements Comparator<CellData>{

	@Override
	public int compare(CellData o1, CellData o2) {
		if (o1.getCount() < o2.getCount()) return -1;
		else if (o1.getCount() == o2.getCount()) 
			{
				if (o1.getReadDatetime() < o2.getReadDatetime()) return 1;
				else if (o1.getReadDatetime() == o2.getReadDatetime()) return 0;
				else return -1;
			}
		else return 1;
	}

}
