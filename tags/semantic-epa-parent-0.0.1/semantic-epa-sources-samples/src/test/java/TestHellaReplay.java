import de.fzi.cep.sepa.sources.samples.hella.MaterialMovementStream;
import de.fzi.cep.sepa.sources.samples.hella.MouldingParameterStream;


public class TestHellaReplay {

	public static void main(String[] args)
	{
		new MaterialMovementStream().executeStream();
	}
}
