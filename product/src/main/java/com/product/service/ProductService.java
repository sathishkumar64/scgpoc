package com.product.service;

import java.util.List;

import com.product.domain.Product;

public interface ProductService {

	Product findByCode(String productCode);
	
	Product findByName(String name);

	Product saveProduct(Product product);

	List<Product> findAllProduct();
	
	String feedToTopic();
}
