package com.slt.peotv.lmsmangmentservice.repository;

import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryTotalEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLeaveCategoryTotalRepo extends CrudRepository<UserLeaveCategoryTotalEntity, Long> {
}
