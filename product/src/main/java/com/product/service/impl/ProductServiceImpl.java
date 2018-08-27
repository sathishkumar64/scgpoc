package com.product.service.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.product.dao.ProductRepository;
import com.product.domain.Product;
import com.product.kafka.service.ProductKafkaDispatcher;
import com.product.service.ProductService;

@Service("productService")
@Transactional
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	ProductRepository productRepository;

	@Autowired
	ProductKafkaDispatcher productKafkaDispatcher;
	
	@Override
	public Product findByCode(String productCode) {
		Product product =productRepository.findByProductCode(productCode);
		return product;
	}

	@Override
	public Product saveProduct(Product product) {	
		Product productObj =productRepository.save(product);
		return productObj;
	}

	@Override
	public List<Product> findAllProduct() {
		List<Product> listProduct =productRepository.findAll();
		return listProduct;
	}

	@Override
	public Product findByName(String name) {
		Product product =productRepository.findByproductName(name);
		return product;
	}

	@Override
	public String feedToTopic() {
		List<Product> listProduct =productRepository.findAll();		
		for (Iterator<Product> iterator = listProduct.iterator(); iterator.hasNext();) {
			Product product = iterator.next();
			productKafkaDispatcher.dispatch(product);			
		}		
		return "Process is Done";
	}

}
