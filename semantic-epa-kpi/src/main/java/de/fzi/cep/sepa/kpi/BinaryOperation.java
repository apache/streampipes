package de.fzi.cep.sepa.kpi;

public class BinaryOperation extends Operation {

	protected Operation left;
	protected Operation right;
	
	protected ArithmeticOperationType arithmeticOperationType;
	
	
	public Operation getLeft() {
		return left;
	}
	public void setLeft(Operation left) {
		this.left = left;
	}
	public Operation getRight() {
		return right;
	}
	public void setRight(Operation right) {
		this.right = right;
	}
	public ArithmeticOperationType getArithmeticOperationType() {
		return arithmeticOperationType;
	}
	public void setArithmeticOperationType(
			ArithmeticOperationType arithmeticOperationType) {
		this.arithmeticOperationType = arithmeticOperationType;
	}
	
	
	
	
}
