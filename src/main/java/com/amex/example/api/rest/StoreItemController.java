package com.amex.example.api.rest;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.amex.example.domain.StoreItem;
import com.amex.example.service.StoreItemService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/*
 * Demonstrates how to set up RESTful API endpoints using Spring MVC
 */

@RestController
@RequestMapping(value = "/example/v1/storeItems")
public class StoreItemController extends AbstractRestHandler {
	private static final Logger log = LoggerFactory.getLogger(StoreItemController.class);
	@Autowired
	private StoreItemService storeItemService;

//Step 1: Build an Orders Service
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = { "application/json",
			"application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Returns Summary of Order.", notes = "")
	@ResponseBody
	public HashMap<String, String> getSummaryOfSuppliedItems(@RequestBody StoreItem storeItem,
			HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, String> summaryDetails = new HashMap<String, String>();
		double price = 0.0;
		if (storeItem.getName().equals("Apples")) {
			price = (storeItem.getQuantity() * 0.6);// Each item caluculated respective amount
		} else if (storeItem.getName().equals("Oranges")) {
			price = (storeItem.getQuantity() * 0.25);/// Each item caluculated respective amount
		}

		summaryDetails.put("Name", storeItem.getName());
		summaryDetails.put("Quantity", String.valueOf(storeItem.getQuantity()));
		summaryDetails.put("Price", String.valueOf(price));
		log.info("Summary Details==>" + summaryDetails.toString());
		return summaryDetails;

	}

	// Step 2: Simple offer
	@RequestMapping(value = "/offerDetails", method = RequestMethod.POST, consumes = { "application/json",
			"application/xml" }, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Returns Summary of Order.", notes = "")
	@ResponseBody
	public HashMap<String, String> getOfferDetails(@RequestBody StoreItem storeItem, HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, String> offerDetails = new HashMap<String, String>();
		int checkOutOffer = 0;
		if (storeItem.getName().equals("Apples")) {
			checkOutOffer = (storeItem.getQuantity() * 2);
		} else if (storeItem.getName().equals("Oranges")) {
			double price = (0.25 * 2);
			checkOutOffer = (int) ((storeItem.getQuantity() / price) * 3);// give money take number of Oranges.

		}

		offerDetails.put("Name", storeItem.getName());
		offerDetails.put("Quantity", String.valueOf(storeItem.getQuantity()));
		offerDetails.put("offerItems", String.valueOf(checkOutOffer));
		log.info("checkOutOffer Details==>" + offerDetails.toString());
		return offerDetails;

	}

	// Step 3: Store and retrieve orders DB is Not up. may throw exceptions
// Create	
	@RequestMapping(value = "create", method = RequestMethod.POST, consumes = { "application/json",
			"application/xml" }, produces = { "application/json", "application/xml" })

	@ResponseStatus(HttpStatus.CREATED)

	@ApiOperation(value = "Create a indivisual  Store items.", notes = "")
	public void createItem(@RequestBody StoreItem storeItem, HttpServletRequest request, HttpServletResponse response) {
		StoreItem storeItemEntity = this.storeItemService.createItem(storeItem);
		response.setHeader("Location", request.getRequestURL().append("/").append(storeItemEntity.getId()).toString());
	}

	

	// Get By Id....
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get a single item.", notes = "You have to provide a valid item ID.")
	public
	@ResponseBody StoreItem getItemById(@ApiParam(value = "The ID of the Item.", required = true)
	@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StoreItem item = this.storeItemService.getItem(id);
		// todo:
		return item;
	}
	
	// Get All records..Through pages
		@RequestMapping(value = "/getAllRecords", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
		@ResponseStatus(HttpStatus.OK)
		@ApiOperation(value = "get all items stored in DB with pagination", notes = "You can provide a page number (default 0) and a page size (default 100)")
		public
		@ResponseBody Page<StoreItem> getAllStoreItems(@ApiParam(value = "The page number (zero-based)", required = true)
		@RequestParam(value = "page",  defaultValue = DEFAULT_PAGE_NUM) Integer page,
				@RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) Integer size,
				HttpServletRequest request, HttpServletResponse response) {
			return this.storeItemService.getAllStoredItems(page, size);
		}

}
