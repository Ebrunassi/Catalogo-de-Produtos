package br.com.compasso.product.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;

import br.com.compasso.product.entity.Product;
import br.com.compasso.product.repository.ProductRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc 
public class ControllerTest {

	@Autowired
	private MockMvc mockMvc;	
	@Autowired
	private ProductRepository mockRepository;	
	private Gson gson = new Gson();

	
	@Test
	public void createProductSuccess() throws Exception{
		Product product = new Product();
		product.setName("Test");
		product.setDescription("Description: test of product");
		product.setPrice(10.5);
		
		// Validacao dos campos informados
		Assert.assertNotNull(product.getName());
		Assert.assertNotNull(product.getDescription());
		Assert.assertNotNull(product.getPrice());
		Assert.assertTrue(product.getPrice() >= 0);				
		
		// Validacao do HTTP e dados do retorno do endpoint - Produto criado com sucesso
		mockMvc.perform(post("/products")
				.content(gson.toJson(product))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(201))
				.andExpect(jsonPath("$.id",notNullValue()))
				.andExpect(jsonPath("$.name").value(product.getName()))
				.andExpect(jsonPath("$.description").value(product.getDescription()))				
				.andExpect(jsonPath("$.price").value(product.getPrice()));
	}
	@Test
	public void createProductRequiredFieldFailure() throws Exception{
		// Validacao dos campos - removido o campo obrigatorio 'description'
		Product product = new Product();
		product.setName("Test");		
		product.setPrice(10.5);
		
		// Validacao do HTTP e dados do retorno do endpoint - Produto nao criado devido a um campo invalido
		mockMvc.perform(post("/products")
				.content(gson.toJson(product))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(400))
				.andExpect(jsonPath("$.status_code").value(400))
				.andExpect(jsonPath("$.message").value("Campos obrigatorios não foram preenchidos"));
	}
	@Test
	public void createProductPriceLessThanZeroFailure() throws Exception{
		// Validacao dos campos - o valor de price nao esta positivo
		Product product = new Product();
		product.setName("Test");	
		product.setDescription("Description: test of product");
		product.setPrice(-10.5);
		
		// Validacao do HTTP e dados do retorno do endpoint - Produto nao criado devido a um campo invalido
		mockMvc.perform(post("/products")
				.content(gson.toJson(product))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(400))
				.andExpect(jsonPath("$.status_code").value(400))
				.andExpect(jsonPath("$.message").value("O valor de 'price' deve ser maior do que zero"));
	}
	@Test
	public void alterProductSucess() throws Exception{
		// Produto previamente cadastrado no banco
		Product product = new Product();		
		product.setName("Test");	
		product.setDescription("Description: test of product");
		product.setPrice(10.5);
		
		// Alteracoes a serem feitas no produto previamente cadastrado
		Product alterProduct = new Product();
		alterProduct.setName("New Name");	
		alterProduct.setDescription("Description: New test of product");
		alterProduct.setPrice(99.9);		
		
		Product saved = mockRepository.save(product);			
				
		// Validacao do HTTP e dados do retorno do endpoint - Produto alterado com sucesso
		mockMvc.perform(put("/products/" + saved.getId())
				.content(gson.toJson(alterProduct))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.id",notNullValue()))
				.andExpect(jsonPath("$.name").value(alterProduct.getName()))
				.andExpect(jsonPath("$.description").value(alterProduct.getDescription()))				
				.andExpect(jsonPath("$.price").value(alterProduct.getPrice()));
	}
	@Test
	public void alterProductFailure() throws Exception{
		// Alteracoes a serem feitas - as alteracoes abaixo serao realizadas em um produto que nao existe na base
		Product alterProduct = new Product();
		alterProduct.setName("New Name");	
		alterProduct.setDescription("Description: New test of product");
		alterProduct.setPrice(99.9);			
		
		//	Validacao do HTTP e dados do retorno do endpoint - Produto nao alterado pois o mesmo nao existe na base
		mockMvc.perform(put("/products/" + "another-different-id")
				.content(gson.toJson(alterProduct))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))				
				.andExpect(status().is(404));
	}
	@Test
	public void findProductById() throws Exception{	
		// Produto previamente cadastrado no banco
		Product p = new Product();		
		p.setName("First product");	
		p.setDescription("Description: test of product");
		p.setPrice(10.5);			
		
		p = mockRepository.save(p);			
		
		// Validacao do HTTP e dados do retorno do endpoint - Produto encontrado
		mockMvc.perform(get("/products/" + p.getId()))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$.id",notNullValue()))
				.andExpect(jsonPath("$.name").value(p.getName()))
				.andExpect(jsonPath("$.description").value(p.getDescription()))				
				.andExpect(jsonPath("$.price").value(p.getPrice()));
	}
	@Test
	public void findAllProducts() throws Exception{		
		// Produtos previamente cadastrados no banco
		Product p1 = new Product();		
		p1.setName("First product");	
		p1.setDescription("Description: test of product");
		p1.setPrice(10.5);
		
		Product p2 = new Product();
		p2.setName("Second product");	
		p2.setDescription("Description: New test of product");
		p2.setPrice(99.9);		
		
		mockRepository.save(p1);			
		mockRepository.save(p2);
		
		// Validacao do HTTP e dados do retorno do endpoint - Lista de produtos encontrados
		mockMvc.perform(get("/products"))				
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$",hasSize(2)))
				.andExpect(jsonPath("$[0].id",notNullValue()))
				.andExpect(jsonPath("$[0].name").value(p1.getName()))
				.andExpect(jsonPath("$[0].description").value(p1.getDescription()))				
				.andExpect(jsonPath("$[0].price").value(p1.getPrice()))
				.andExpect(jsonPath("$[1].id",notNullValue()))
				.andExpect(jsonPath("$[1].name").value(p2.getName()))
				.andExpect(jsonPath("$[1].description").value(p2.getDescription()))				
				.andExpect(jsonPath("$[1].price").value(p2.getPrice()));
	}
	@Test
	public void searchProduct() throws Exception{		
		// Produtos previamente cadastrados no banco
		Product p1 = new Product();		
		p1.setName("First product");	
		p1.setDescription("Description: test of product");
		p1.setPrice(10.5);
		
		Product p2 = new Product();
		p2.setName("Second product");	
		p2.setDescription("Description: New test of product");
		p2.setPrice(99.9);		
		
		mockRepository.save(p1);			
		mockRepository.save(p2);
		
		// Validacao do HTTP e dados do retorno do endpoint - Lista contendo apenas um produto retornada
		mockMvc.perform(get("/products/search?min_price=50&q=product"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$",hasSize(1)))
				.andExpect(jsonPath("$[0].id",notNullValue()))
				.andExpect(jsonPath("$[0].name").value(p2.getName()))
				.andExpect(jsonPath("$[0].description").value(p2.getDescription()))				
				.andExpect(jsonPath("$[0].price").value(p2.getPrice()));
	}
	@Test
	public void deleteProductSucess() throws Exception{
		// Produto previamente cadastrados no banco
		Product product = new Product();
		product.setName("Test");
		product.setDescription("Description: test of product");
		product.setPrice(10.5);
		
		// Validacao dos campos informados
		Assert.assertNotNull(product.getName());
		Assert.assertNotNull(product.getDescription());
		Assert.assertNotNull(product.getPrice());
		Assert.assertTrue(product.getPrice() >= 0);				
		
		product = mockRepository.save(product);
		
		// Validacao do HTTP e dados do retorno do endpoint - Produto deletado
		mockMvc.perform(delete("/products/" + product.getId()))				
				.andExpect(status().is(200));
	}
	@Test
	public void deleteProductFailure() throws Exception{			
				
		// Validacao dos campos informados - Produto nao deletado pois o mesmo nao existe na base
		mockMvc.perform(delete("/products/another-id-that-doesnt-exists"))				
				.andExpect(status().is(404));
	}
	
	@AfterEach
    public void cleanUpEach(){		
		mockRepository.deleteAll();	// Depois de cada teste ser realizado, realiza-se uma limpeza na base
    }
}

