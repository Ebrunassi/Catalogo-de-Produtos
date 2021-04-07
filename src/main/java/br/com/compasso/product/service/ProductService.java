package br.com.compasso.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import br.com.compasso.product.entity.Product;
import br.com.compasso.product.model.Return;
import br.com.compasso.product.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepositoy;
	protected Gson gson = new Gson();
	
	public Product createProduct(Product product) {		
		try {			
//			Product product = gson.fromJson(json, Product.class);
		
//			if(product.getName() == null || product.getDescription() == null || product.getPrice() == null)			
//				return new Return(400,"Campo obrigatório não foi preenchido");
//			if(product.getPrice() < 0)
//				return new Return(400,"O valor de 'price' deverá ser maior que zero"); 
			return productRepositoy.save(product);
	
		}catch(Exception e) {
//			return new Return(400,"Erro no processamento da requisicao");
		}
		return null;
	}
}
