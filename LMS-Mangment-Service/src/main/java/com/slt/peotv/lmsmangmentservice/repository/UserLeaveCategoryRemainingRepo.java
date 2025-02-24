package com.slt.peotv.lmsmangmentservice.repository;

import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryRemainingEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveCategoryEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLeaveCategoryRemainingRepo extends CrudRepository<UserLeaveCategoryRemainingEntity, Long> {
    Optional<UserLeaveCategoryRemainingEntity> findByPublicId(String publicId);
    List<UserLeaveCategoryRemainingEntity> findByLeaveCategoryAndUser(LeaveCategoryEntity leaveCategory, UserEntity user);
}

