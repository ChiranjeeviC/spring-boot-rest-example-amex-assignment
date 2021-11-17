package com.amex.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.amex.example.dao.jpa.StoreRepository;
import com.amex.example.domain.StoreItem;
/*
 * Sample service to demonstrate what the API would use to get things done
 */
@Service
public class StoreItemService {

    private static final Logger log = LoggerFactory.getLogger(StoreItemService.class);

    @Autowired
    private StoreRepository storelRepository;

    public StoreItemService() {
    }

    public StoreItem createItem(StoreItem storeItem) {
        return storelRepository.save(storeItem);
    }

    public StoreItem getItem(long id) {
        return storelRepository.findOne(id);
    }

    public Page<StoreItem> getAllStoredItems(Integer page, Integer size) {
        Page pageOfItems = storelRepository.findAll(new PageRequest(page, size));
        return pageOfItems;
    }
}
