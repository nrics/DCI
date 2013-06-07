package side.model;

import java.util.List;

public class ComplexElementType {
	
	List<ComplexElement> complexElement;
	String name;
	int quantity;

	public List<ComplexElement> getComplexElement() {
		return complexElement;
	}

	public void setComplexElement(List<ComplexElement> complexElement) {
		this.complexElement = complexElement;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
