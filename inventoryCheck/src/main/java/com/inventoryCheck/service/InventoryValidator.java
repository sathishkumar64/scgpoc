package com.inventoryCheck.service;

import static com.edadto.domain.OrderValidationResult.FAIL;
import static com.edadto.domain.OrderValidationResult.PASS;
import static com.edadto.domain.OrderValidationType.INVENTORY_CHECK;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edadto.domain.OrderProductInfo;
import com.edadto.domain.OrderValidation;

public class InventoryValidator implements Transformer<String, OrderProductInfo, KeyValue<String, OrderValidation>> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryValidator.class);

	public static final String RESERVED_STOCK_STORE_NAME = "store-of-reserved-stock";
	private KeyValueStore<String, Long> reservedStocksStore;

	@Override
	public void init(ProcessorContext context) {		
		KeyValueStore<String, Long> stateStore = (KeyValueStore<String, Long>) context.getStateStore(RESERVED_STOCK_STORE_NAME);
		reservedStocksStore = stateStore;
	}

	@Override
	public KeyValue<String, OrderValidation> transform(String productId, OrderProductInfo orderproductStock) {
		OrderValidation validated;
		Integer warehouseStockCount = orderproductStock.getProduct().getQuantity();
		
		Long reserved = reservedStocksStore.get(orderproductStock.getProduct().getProductCode());	
		
		LOGGER.info("Reserved Stock........."+reserved +" Warehouse Stock Count...."+warehouseStockCount);
		
		if (reserved == null) {
			reserved = 0L;
		}
	
		if (warehouseStockCount - reserved - orderproductStock.getOrder().getQuantity() >= 0) {
			// reserve the stock by adding it to the 'reserved' store
			reservedStocksStore.put(orderproductStock.getProduct().getProductCode(),reserved + orderproductStock.getOrder().getQuantity());
			// validate the order
			validated = new OrderValidation(orderproductStock.getOrder().getOrderId(), INVENTORY_CHECK, PASS);
		} else {
			// fail the order
			validated = new OrderValidation(orderproductStock.getOrder().getOrderId(), INVENTORY_CHECK, FAIL);
		}
		LOGGER.info("Inventory Validator Prodcut::::" + productId + " Status::::::::::" + validated.toString());
		return KeyValue.pair(orderproductStock.getOrder().getOrderId(), validated);
	}

	@Override
	public KeyValue<String, OrderValidation> punctuate(long timestamp) {
		return null;
	}

	@Override
	public void close() {

	}

}
