package com.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.product.domain.Product;
import com.product.service.ProductService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController   
@RequestMapping(path="/api")
@Api(value="onlinestore", description="Operations pertaining to products in Online Store")	
public class ProductController {
	
	@Autowired
	ProductService productService;
	
	
	@ApiOperation(value = "View a list of available products", response = Iterable.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully retrieved list"),
	        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	}
	)
	@GetMapping(path="/product/all")
	public @ResponseBody Iterable<Product> getAllProduct() {		
		return productService.findAllProduct();
	}
	
	@ApiOperation(value = "Search a product with an ID",response = Product.class)
	@GetMapping(path="/product")
	public @ResponseBody Product getProduct(@RequestParam (value = "id") String productcode) {		
		return productService.findByCode(productcode);
	}
	
	@ApiOperation(value = "Search a product with an Name",response = Product.class)
	@GetMapping(path="/productName")
	public @ResponseBody Product getProductByName(@RequestParam (value = "productName") String productName) {		
		return productService.findByName(productName);
	}
	
	@ApiOperation(value = "Add a product")
	@PostMapping(path="/product" )
	public @ResponseBody String addNewProduct (@RequestBody Product product) {
		productService.saveProduct(product);
		return "saved";
	}
	
	
	
	/**
	 * TODO -Have to think about kafka connect how to sink source to kafka
	 */
	
	@ApiOperation(value = "Feed a products to Kafka topic")
	@GetMapping(path="/feedproduct" )
	public @ResponseBody String feedToKafka () {
		String status=productService.feedToTopic();
		return status;
	}

}
