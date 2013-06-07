package side.model;

import java.util.List;

public class ComplexType {
	
	String name;
	List<ComplexElement> complexElement;
	Integer quantity;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<ComplexElement> getComplexElement() {
		return complexElement;
	}

	public void setComplexElement(List<ComplexElement> complexElement) {
		this.complexElement = complexElement;
	}
	
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
}
