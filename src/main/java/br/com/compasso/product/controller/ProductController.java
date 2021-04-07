package br.com.compasso.product.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import br.com.compasso.product.entity.Product;
import br.com.compasso.product.model.Return;
import br.com.compasso.product.repository.ProductRepository;
import br.com.compasso.product.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


@Api(value = "Compasso - 'Product' - Java Pleno Project")
@RestController
@RequestMapping("/products")
public class ProductController {
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductService productService;
	
	@ApiOperation(value = "Realiza o cadastro de um produto")
	@ApiResponses(value = @ApiResponse(code = 200, message = "OK", response = Product.class))
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Product> createProduct(@ApiParam(value ="Body contendo definições do produto")
		@RequestBody @Valid Product product){
		
		try {			
			Product retorno = productRepository.save(product);
			return  ResponseEntity.status(201).body(retorno);
		}catch(Exception e) {
//			return ResponseEntity.status(400).body(new Return(400,"Erro ao salvar produto"));
		}
		return null;

				
	}
	
	@ApiOperation(value = "Realiza a alteração dos dados de um produto")
	@ApiResponses(value = @ApiResponse(code = 200, message = "OK", response = Product.class))
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<Product> alterProduct(	@PathVariable("id") @ApiParam(value = "Id do produto a ser alterado") String id,
			@ApiParam(value ="Body contendo definições do produto")@RequestBody @Valid Product product){
		
		try {
			Integer updated = productRepository.updateProduct(id, product.getName(), product.getDescription(), product.getPrice());
			if(updated > 0)				
				return ResponseEntity.status(200).body(new Product(id,product));
			else
				return ResponseEntity.status(404).build();				
		}catch(Exception e) {
			e.printStackTrace();
//			return ResponseEntity.status(400).body(new Return(400,"Erro ao salvar produto"));
		}
		return null;			
	}
	
	@ApiOperation(value = "Resgata informações a respeito de um produto já cadastrado")
	@ApiResponses(value = @ApiResponse(code = 200, message = "OK", response = Product.class))
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Product> findProduct(	@PathVariable("id") @ApiParam(value = "Id do produto a ser buscado") String id){
		
		try {			
			Optional<Product> prod = productRepository.findById(id);
			if(prod.isPresent())				
				return ResponseEntity.status(201).body(prod.get());
			else
				return ResponseEntity.status(404).build();				
		}catch(Exception e) {
			e.printStackTrace();
//			return ResponseEntity.status(400).body(new Return(400,"Erro ao salvar produto"));
		}
		return null;			
	}
	
	@ApiOperation(value = "Resgata informações a respeito de todos os produtos já cadastrados")
	@ApiResponses(value = @ApiResponse(code = 200, message = "OK", response = Product.class))
	@RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Iterable<Product>> searchAllProduct(){
		
		try {			
			Iterable<Product> prod = productRepository.findAll();
			if(prod.iterator().hasNext())				
				return ResponseEntity.status(200).body(prod);
			else
				return ResponseEntity.status(200).body(prod);				
		}catch(Exception e) {
			e.printStackTrace();
//			return ResponseEntity.status(400).body(new Return(400,"Erro ao salvar produto"));
		}
		return null;			
	}
	
	@ApiOperation(value = "Resgata uma lista contendo todos os produtos já cadastrados e filtrados por parâmetros")
	@ApiResponses(value = @ApiResponse(code = 200, message = "OK", response = Product.class))
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Product>> searchProduct(
			@Param(value = "q") @ApiParam(value = "Buscará nos campos 'name' e 'description'") String q,
			@Param(value = "min_price") @ApiParam(value = "Preço minimo") Double min_price,
			@Param(value = "max_price") @ApiParam(value = "Preço máximo") Double max_price){
		
		try {			
			List<Product> prod = productRepository.searchProductByParameters(q,min_price,max_price);				
			return ResponseEntity.status(200).body(prod);
			
							
		}catch(Exception e) {
			e.printStackTrace();
//			return ResponseEntity.status(400).body(new Return(400,"Erro ao salvar produto"));
		}
		return null;			
	}
	
	@ApiOperation(value = "Realiza a remoção de um produto previamente cadastrado")
	@ApiResponses(value = @ApiResponse(code = 200, message = "OK", response = Product.class))
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<Product> deleteProduct(	@PathVariable("id") @ApiParam(value = "Id do produto a ser deletado") String id){
		
		try {			
			if(productRepository.existsById(id)) {
				productRepository.deleteById(id);
				return ResponseEntity.status(200).build();
			}else {
				return ResponseEntity.status(404).build();	
			}						
		}catch(Exception e) {
			e.printStackTrace();
//			return ResponseEntity.status(400).body(new Return(400,"Erro ao salvar produto"));
		}
		return null;			
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Return> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {	
	    return ResponseEntity.status(400).body(new Return(400,ex.getFieldErrors().get(0).getDefaultMessage()));     	   
	}

}
