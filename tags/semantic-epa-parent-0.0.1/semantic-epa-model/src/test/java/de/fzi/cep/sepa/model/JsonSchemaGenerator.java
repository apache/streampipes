package de.fzi.cep.sepa.model;


import java.util.UUID;

import com.google.gson.Gson;

import de.fzi.cep.sepa.model.util.GsonSerializer;
import pl.zientarski.SchemaMapper;

//public class JsonSchemaGenerator {
//
//	public static void main(String[] args) {
//
//		KpiRequest kpiRequest = new KpiRequest();
//		kpiRequest.setKpiId(UUID.randomUUID().toString());
//		kpiRequest.setKpiName("Scrap Rate");
//		kpiRequest.setKpiDescription("Description");
//		kpiRequest.setKpiOperation(KpiOperationType.ADD);
//		
//		BinaryOperation operation = new BinaryOperation();
//		operation.setOperationType(OperationType.BINARY);
//		
//		operation.setArithmeticOperationType(ArithmeticOperationType.DIVIDE);
//		
//		UnaryOperation scrap = new UnaryOperation();
//		scrap.setEventPropertyName("scrap");
//		scrap.setPropertyRestriction("true");
//		scrap.setPropertyType("BOOLEAN");
//		scrap.setPartition(false);
//		scrap.setSensorId("ScrapSensorStream");
//		scrap.setUnaryOperationType(UnaryOperationType.SUM);
//		
//		UnaryOperation total = new UnaryOperation();
//		total.setEventPropertyName("*");
//		total.setSensorId("ScrapSensorStream");
//		total.setPartition(false);
//		total.setUnaryOperationType(UnaryOperationType.COUNT);
//		
//		Window window = new Window();
//		window.setTimeUnit(TimeUnit.DAYS);
//		window.setValue(1);
//		window.setWindowType(WindowType.TIME);
//		
//		total.setWindow(window);
//		scrap.setWindow(window);
//		
//		operation.setLeft(scrap);
//		operation.setRight(total);
//		
//		kpiRequest.setOperation(operation);
//		
//
//		System.out.println(GsonSerializer.getGson().toJson(kpiRequest));
//
//	}
//}
