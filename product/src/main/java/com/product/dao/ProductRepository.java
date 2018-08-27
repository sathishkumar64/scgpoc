package com.product.dao;


import java.util.List;

import org.springframework.data.repository.Repository;

import com.product.domain.Product;

public interface ProductRepository extends Repository<Product,Long> {

	 Product findByProductCode(String productCode);
	 
	 Product findByproductName(String productName);
	 
	 List<Product> findAll();
	 
	 Product save(Product product);
}
