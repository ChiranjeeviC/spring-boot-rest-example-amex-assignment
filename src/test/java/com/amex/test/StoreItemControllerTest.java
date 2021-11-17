package com.amex.test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.amex.example.Application;
import com.amex.example.api.rest.StoreItemController;
import com.amex.example.domain.StoreItem;

/**
 * Uses JsonPath: http://goo.gl/nwXpb, Hamcrest and MockMVC
 */

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class StoreItemControllerTest {

	private static final String RESOURCE_LOCATION_PATTERN = "http://localhost/example/v1/storeItems/[0-9]+";
	@InjectMocks
	StoreItemController controller;

	@Autowired
	WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void initTests() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test

	public void shouldHaveEmptyDB() throws Exception {
		mvc.perform(get("/example/v1/storeItems").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	// Summary-Step-1
	public void getSummaryOfOrderDetails() throws Exception {
		// StoreItem obj1 = mockItem("Apples");
		StoreItem obj1 = mockItem("Oranges");
		ArrayList<StoreItem> alList = new ArrayList<StoreItem>();
		alList.add(obj1);
		// alList.add(obj2);
		byte[] r1Json = toJson(obj1);

		// Summary
		double price = 0.0;
		int checkOutOffer = 0;
		if (obj1.getName().equals("Apples")) {
			price = (obj1.getQuantity() * 0.6);
		} else if (obj1.getName().equals("Oranges")) {
			price = (obj1.getQuantity() * 0.25);
		}

		mvc.perform(post("/example/v1/storeItems").content(r1Json).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect((jsonPath("$.Name", is((obj1.getName())))))
				.andExpect((jsonPath("$.Price", is((String.valueOf(price))))));

	}

	// Step-2 OfferDetails-Apples
	@Test

	public void getOfferDetails0() throws Exception {
		StoreItem obj1 = mockItem("Apples");
		int checkOutOffer = 0;
		byte[] r1Json = toJson(obj1);
		if (obj1.getName().equals("Apples")) {
			checkOutOffer = (obj1.getQuantity() * 2);
		} else if (obj1.getName().equals("Oranges")) {
			checkOutOffer = (obj1.getQuantity() / 2) == 0 ? (3 * 2) : obj1.getQuantity();

		}
		mvc.perform(post("/example/v1/storeItems/offerDetails").content(r1Json).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect((jsonPath("$.Name", is((obj1.getName())))))
				.andExpect((jsonPath("$.offerItems", is((String.valueOf(checkOutOffer))))));
	}

	// OfferDetails-Oranges
	@Test

	public void getOfferDetails1() throws Exception {
		StoreItem obj1 = mockItem("Oranges");
		int checkOutOffer = 0;
		byte[] r1Json = toJson(obj1);
		if (obj1.getName().equals("Apples")) {
			checkOutOffer = (obj1.getQuantity() * 2);
		} else if (obj1.getName().equals("Oranges")) {
			double price = (0.25 * 2);
			checkOutOffer = (int) ((obj1.getQuantity() / price) * 3);

		}
		mvc.perform(post("/example/v1/storeItems/offerDetails").content(r1Json).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect((jsonPath("$.Name", is((obj1.getName())))))
				.andExpect((jsonPath("$.offerItems", is((String.valueOf(checkOutOffer))))));
	}

//Step#3 get From DB

	@Test
	public void create() throws Exception {
		StoreItem r1 = mockItem("Apple");
		byte[] r1Json = toJson(r1);
		// CREATE
		MvcResult result = mvc
				.perform(post("/example/v1/storeItems/create").content(r1Json).contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(redirectedUrlPattern(RESOURCE_LOCATION_PATTERN)).andReturn();
		long id = getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

		StoreItem r2 = mockItem("Orange");
		r2.setId(3);
		byte[] r2Json = toJson(r2);

		// RETRIEVE updated by Id
		mvc.perform(get("/example/v1/storeItems/" + id).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is((int) id))).andExpect(jsonPath("$.name", is(r2.getName())))
				.andExpect(jsonPath("$.description", is(r2.getDescription())))
				.andExpect(jsonPath("$.Quantity", is(r2.getQuantity())));

	}

	/*
	******************************
	 */

	private long getResourceIdFromUrl(String locationUrl) {
		String[] parts = locationUrl.split("/");
		return Long.valueOf(parts[parts.length - 1]);
	}

	private StoreItem mockItem(String name) {
		StoreItem item = new StoreItem();
		item.setName(name);
		item.setDescription(name);
		item.setQuantity(10);

		return item;
	}

	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}


	private static ResultMatcher redirectedUrlPattern(final String expectedUrlPattern) {
		return new ResultMatcher() {
			public void match(MvcResult result) {
				Pattern pattern = Pattern.compile("\\A" + expectedUrlPattern + "\\z");
				assertTrue(pattern.matcher(result.getResponse().getRedirectedUrl()).find());
			}
		};
	}

}
