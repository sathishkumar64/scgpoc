package com.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.customer.domain.Customer;
import com.customer.service.CustomerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController   
@RequestMapping(path="/api")
@Api(value="onlinestore", description="Operations pertaining to customers in Online Store")	
public class CustomerController {
	
	@Autowired
	CustomerService customerService;
	
	
	@ApiOperation(value = "View a list of available customers", response = Iterable.class)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully retrieved list"),
	        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	}
	)
	@GetMapping(path="/customer/all")
	public @ResponseBody Iterable<Customer> getAllProduct() {		
		return customerService.findAllCustomer();
	}
	
	@ApiOperation(value = "Search a customer with an ID",response = Customer.class)
	@GetMapping(path="/customer")
	public @ResponseBody Customer getCustomer(@RequestParam (value = "customerId") String customerId) {		
		return customerService.findByCode(customerId);
	}
	
	
	
	@ApiOperation(value = "Add a customer")
	@PostMapping(path="/customer" )
	public @ResponseBody String addNewCustomer (@RequestBody Customer product) {
		customerService.saveCustomer(product);
		return "saved";
	}
	
	@ApiOperation(value = "Feed a Customers to Kafka topic")
	@GetMapping(path="/feedcustomer" )
	public @ResponseBody String feedToKafka () {
		String status=customerService.feedToTopic();
		return status;
	}

}
