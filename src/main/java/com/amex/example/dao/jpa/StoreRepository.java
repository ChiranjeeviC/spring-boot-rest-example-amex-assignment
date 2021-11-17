package com.amex.example.dao.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.amex.example.domain.StoreItem;

public interface StoreRepository extends PagingAndSortingRepository<StoreItem, Long> {
     @SuppressWarnings("unchecked")
	Page findAll(Pageable pageable);
}
