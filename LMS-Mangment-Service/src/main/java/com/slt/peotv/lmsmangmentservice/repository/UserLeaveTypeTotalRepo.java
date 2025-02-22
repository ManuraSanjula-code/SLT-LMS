package com.slt.peotv.lmsmangmentservice.repository;

import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeTotalEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLeaveTypeTotalRepo extends CrudRepository<UserLeaveTypeTotalEntity, Long> {
}
