package br.com.compasso.product.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;



@JsonInclude(JsonInclude.Include.NON_NULL)
public class Return implements Serializable{

	private static final long serialVersionUID = 8943847076844465755L;
	
	private Integer status_code;	
	private String message;
	
	public Return(Integer status_code, String message) {
		super();
		this.status_code = status_code;
		this.message = message;
	}
	public Integer getStatus_code() {
		return status_code;
	}
	public void setStatus_code(Integer status_code) {
		this.status_code = status_code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
