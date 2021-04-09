package br.com.compasso.product.entity;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import br.com.compasso.product.model.Return;
import io.swagger.annotations.ApiModelProperty;

@Entity
public class Product{

	private static final long serialVersionUID = 7381009935406313836L;
	@Id
	@ApiModelProperty(hidden = true)
    private String id;
	@NotNull(message = "Campos obrigatorios não foram preenchidos")
	@ApiModelProperty(example = "Product Name")
	private String name;
	@NotNull(message = "Campos obrigatorios não foram preenchidos")
	@ApiModelProperty(example = "Product Description")
	private String description;
	@NotNull(message = "Campos obrigatorios não foram preenchidos")
	@Min(value = 0, message = "O valor de 'price' deve ser maior do que zero")
	@ApiModelProperty(example = "10.5")
	private Double price;
	
	public Product() {}

	public Product(String id, Product product) {	
	this.id = id;
	this.name = product.getName();
	this.description = product.getDescription();
	this.price = product.getPrice();
}


	@PrePersist
	private void setIdValue() {
		this.setId(UUID.randomUUID().toString());
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
}
