package br.com.compasso.product.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.compasso.product.entity.Product;

public interface ProductRepository extends CrudRepository<Product,String>{
	
	@Modifying
	@Transactional
	@Query("UPDATE Product p SET p.name = ?2, p.description = ?3, p.price = ?4 WHERE id = ?1")
	Integer updateProduct(String id, String name, String description, Double price);
	
	@Query("SELECT p FROM Product p "
			+ "WHERE ((?1 is null OR upper(p.name) LIKE CONCAT('%',upper(?1),'%')) "
			+ "OR (?1 is null OR upper(p.description) LIKE CONCAT('%',upper(?1),'%'))) "
			+ "AND (?2 is null OR p.price >= ?2) "
			+ "AND (?3 is null OR p.price <= ?3)")
	List<Product> searchProductByParameters(String q, Double min_price, Double max_price);
	
}
