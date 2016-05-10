//package de.fzi.cep.sepa.sources.samples.proveit;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import de.fzi.proveit.senslet.model.Senslet;
//import de.fzi.proveit.senslet.model.input.BarcodeInput;
//import de.fzi.proveit.senslet.model.input.CheckboxInput;
//import de.fzi.proveit.senslet.model.input.ElementVisitor;
//import de.fzi.proveit.senslet.model.input.GPSInput;
//import de.fzi.proveit.senslet.model.input.PictureInput;
//import de.fzi.proveit.senslet.model.input.RadioInput;
//import de.fzi.proveit.senslet.model.input.SensletInputElement;
//import de.fzi.proveit.senslet.model.input.SignatureInput;
//import de.fzi.proveit.senslet.model.input.StaticElement;
//import de.fzi.proveit.senslet.model.input.TextInput;
//import de.fzi.proveit.senslet.model.property.ReportProperty;
//import de.fzi.proveit.senslet.model.property.ReportPropertyType;
//import de.fzi.proveit.senslet.model.property.SensletProperty;
//import de.fzi.proveit.senslet.model.property.SensletPropertyType;
//
//public class ProveITEventConverter {
//
//	public static Map<String, Object> makeFlat(Senslet senslet)
//	{
//		Map<String, Object> result = new HashMap<>();
//		
//		for(SensletPropertyType type : senslet.getSensletProperties().keySet())
//		{
//			result.put(type.toString(), senslet.getSensletProperties().get(type).getValue());
//		}
//		
//		for(ReportPropertyType type : senslet.getReportProperties().keySet())
//		{
//			result.put(type.toString(), senslet.getReportProperties().get(type).getValue());
//		}
//		
//		ElementVisitor visitor = new ElementVisitor() {
//			
//			@Override
//			public void visit(SensletProperty sensletProperty) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void visit(ReportProperty reportProperty) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void visit(TextInput textInput) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void visit(StaticElement staticElement) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void visit(SignatureInput signatureInput) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void visit(RadioInput radioInput) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void visit(PictureInput pictureInput) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void visit(GPSInput gpsInput) {
//				Map<String, Object> location = new HashMap<>();
//				location.put("latitude", gpsInput.getLatitude());
//				location.put("longitude", gpsInput.getLongitude());
//				
//				result.put("location", location);
//			}
//			
//			@Override
//			public void visit(CheckboxInput checkboxInput) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void visit(BarcodeInput barcodeInput) {
//				// TODO Auto-generated method stub
//				
//			}
//		};
//		
//		for(SensletInputElement input : senslet.getSensletElements())
//		{
//			input.accept(visitor);
//		}
//		
//		return result;
//	}
//}
