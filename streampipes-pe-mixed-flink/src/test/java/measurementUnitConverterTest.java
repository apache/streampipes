import com.github.jqudt.Quantity;
import com.github.jqudt.Unit;
import org.streampipes.units.UnitProvider;

import java.util.HashMap;
import java.util.Map;

public class measurementUnitConverterTest {

    private static String unitName;
    private static Unit inputUnit;
    private static Unit outputtUnit;


    public static void main(String[] args) throws Exception {

//        DataProcessorInvocation sepa = new DataProcessorInvocation();

  //      MeasurementUnitConverterParameters param = new MeasurementUnitConverterParameters(sepa, "",
  //              UnitProvider.INSTANCE.getUnitByLabel("Kelvin"), UnitProvider.INSTANCE.getUnitByLabel("Degree Celsius"));

        unitName = "key";
        inputUnit = UnitProvider.INSTANCE.getUnitByLabel("Degree Celsius");
        outputtUnit = UnitProvider.INSTANCE.getUnitByLabel("Kelvin");

        Map<String, Object> in = new HashMap<>();
        Map<String, Object> out = new HashMap<>();

        in.put("key", 20.0);
        System.out.print(in.get("key").toString() + " " + inputUnit.getSymbol() + " = ");

        out = flatMap(in);

        System.out.print(out.get("key").toString() + " " + outputtUnit.getSymbol());
    }



    private static Map<String, Object> flatMap(Map<String, Object> in) throws Exception {
        double value = (double) in.get(unitName);

        Quantity obs = new Quantity(value, inputUnit);

        double newValue = obs.convertTo(outputtUnit).getValue();

        in.put(unitName, newValue);

        return in;

    }

}
