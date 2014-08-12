package de.fzi.cep.sepa.esper.movement;

import de.fzi.cep.sepa.desc.ModelSubmitter;

public class Init {

	public static void main(String[] args)
	{
		MovementController movementAnalysis = new MovementController();
		try {
			ModelSubmitter.submitAgent(8092, movementAnalysis);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
