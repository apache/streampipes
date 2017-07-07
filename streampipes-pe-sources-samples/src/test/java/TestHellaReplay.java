import de.fzi.cep.sepa.sources.samples.hella.MaterialMovementStream;


public class TestHellaReplay {

	public static void main(String[] args)
	{
		new MaterialMovementStream().executeStream();
	}
}
