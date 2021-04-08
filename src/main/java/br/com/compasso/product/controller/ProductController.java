package br.com.compasso.product.controller;


import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import br.com.compasso.product.entity.Product;
import br.com.compasso.product.model.Return;
import br.com.compasso.product.repository.ProductRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Api(value = "Compasso - 'Catálogo de Produtos'")
@RestController
@RequestMapping("/products")
public class ProductController {
	@Autowired
	private ProductRepository productRepository;	
	protected Logger log = (Logger) LogManager.getLogger(ProductController.class);
	protected Gson gson = new Gson();
	
	@ApiOperation(value = "Realiza o cadastro de um produto")
	@ApiResponses(value = @ApiResponse(code = 201, message = "OK", response = Product.class))
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Product> createProduct(@ApiParam(value ="Body contendo definições do produto") @RequestBody @Valid Product product){
		
		try {			
			Product retorno = productRepository.save(product);		// Salva produto na base
			log.info("Produto salvo com sucesso: '{}'", gson.toJson(retorno));
			return  ResponseEntity.status(201).body(retorno);		// HTTP 201 - criado com sucesso
		}catch(Exception e) {
			log.error("Erro ao salvar produto: {}", e.getMessage());
			return ResponseEntity.status(500).build();				// Erro de processamento
		}				
	}
	
	@ApiOperation(value = "Realiza a alteração dos dados de um produto")
	@ApiResponses(value = @ApiResponse(code = 200, message = "OK", response = Product.class))
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<Product> alterProduct(	@PathVariable("id") @ApiParam(value = "Id do produto a ser alterado") String id,
			@ApiParam(value ="Body contendo definições do produto")@RequestBody @Valid Product product){
		
		try {
			Integer updated = productRepository.updateProduct(id, product.getName(), product.getDescription(), product.getPrice());
			if(updated > 0) {										// Caso tenha encontrado e alterado algum produto na base com o id informado
				log.info("Dados do produto alterados com sucesso:'{}'", gson.toJson(updated));
				return ResponseEntity.status(200).body(new Product(id,product));	// HTTP 200 - alteracao realizada
			}else {													// Caso não tenha achado nenhum produto na base com o id informado
				log.info("Produto com id '{}' nao foi encontrado", id);
				return ResponseEntity.status(404).build();			// HTTP 404 - Produto nao encontrado
			}
		}catch(Exception e) {
			log.error("Erro ao salvar produto: {}", e.getMessage());
			return ResponseEntity.status(500).build();								// Erro de processamento
		}		
	}
	
	@ApiOperation(value = "Resgata informações a respeito de um produto já cadastrado")
	@ApiResponses(value = @ApiResponse(code = 200, message = "OK", response = Product.class))
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Product> findProduct(	@PathVariable("id") @ApiParam(value = "Id do produto a ser buscado") String id){
		
		try {			
			Optional<Product> prod = productRepository.findById(id);
			if(prod.isPresent()) {											// Caso encontre o produto na base
				log.info("Produtos encontrados na base de dados:'{}'", gson.toJson(prod));				
				return ResponseEntity.status(200).body(prod.get());			//  HTTP 200 - Produto encontrado + dados
			}else {
				log.info("Produto com id '{}' nao foi encontrado", id);				
				return ResponseEntity.status(404).build();					// HTTP 404 - Produto nao foi encontrado
			}
		}catch(Exception e) {
			log.error("Erro ao salvar produto: {}", e.getMessage());
			return ResponseEntity.status(500).build();						// Erro de processamento
		}		
	}
	
	@ApiOperation(value = "Resgata informações a respeito de todos os produtos já cadastrados")
	@ApiResponses(value = @ApiResponse(code = 200, message = "OK", response = Product.class))
	@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Iterable<Product>> searchAllProduct(){
		
		try {			
			Iterable<Product> prod = productRepository.findAll();			// Procura por todos os produtos cadastrados previamente na base
			log.info("Produtos encontrados na base de dados:'{}'", gson.toJson(prod));
			return ResponseEntity.status(200).body(prod);					// HTTP 200 - Lista dos produtos encontrados + dados	
		}catch(Exception e) {
			log.error("Erro ao salvar produto: {}", e.getMessage());
			return ResponseEntity.status(500).build();						// Erro de processamento
		}			
	}
	
	@ApiOperation(value = "Resgata uma lista contendo todos os produtos já cadastrados e filtrados por parâmetros")
	@ApiResponses(value = @ApiResponse(code = 200, message = "OK", response = Product.class))
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Product>> searchProduct(
			@Param(value = "q") @ApiParam(value = "Buscará nos campos 'name' e 'description'") String q,
			@Param(value = "min_price") @ApiParam(value = "Preço minimo") Double min_price,
			@Param(value = "max_price") @ApiParam(value = "Preço máximo") Double max_price){
		
		try {			
			List<Product> prod = productRepository.searchProductByParameters(q,min_price,max_price);	// Procura pelos produtos com base nos parametros informados
			log.info("Produtos encontrados:'{}'", gson.toJson(prod));
			return ResponseEntity.status(200).body(prod);			// HTTP 200 - Lista dos produtos encontrados + dados
		}catch(Exception e) {
			log.error("Erro ao salvar produto: {}", e.getMessage());
			return ResponseEntity.status(500).build();				// Erro de processamento
		}		
	}
	
	@ApiOperation(value = "Realiza a remoção de um produto previamente cadastrado")
	@ApiResponses(value = @ApiResponse(code = 200, message = "OK", response = Product.class))
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<Product> deleteProduct(	@PathVariable("id") @ApiParam(value = "Id do produto a ser deletado") String id){
		
		try {			
			if(productRepository.existsById(id)) {
				productRepository.deleteById(id);
				log.info("Produto com id '{}' foi deletado com sucesso", id);
				return ResponseEntity.status(200).build();			// HTTP 200 - Produto deletado com sucesso
			}else {
				log.info("Produto com id '{}' nao foi encontrado", id);
				return ResponseEntity.status(404).build();			// HTTP 404 - Produto nao encontrado
			}						
		}catch(Exception e) {
			log.error("Erro ao salvar produto: {}", e.getMessage());
			return ResponseEntity.status(500).build();				// Erro de processamento
		}
	}

	/*
	 *  Todas as vezes que ocorrer um erro de validacao dos campos do objeto Product,
	 *   este metodo sera chamado e retornara HTTP 400 junto com a mensagem do erro
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Return> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {	
	    return ResponseEntity.status(400).body(new Return(400,ex.getFieldErrors().get(0).getDefaultMessage()));     	   
	}

}
