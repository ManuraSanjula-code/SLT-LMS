package com.slt.peotv.lmsmangmentservice.repository;

import com.slt.peotv.lmsmangmentservice.entity.Absentee.AbsenteeEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AbsenteeRepo extends JpaRepository<AbsenteeEntity, Long> {
    List<AbsenteeEntity> findByUser(UserEntity user);
    Optional<AbsenteeEntity> findByPublicId(String publicId);
}
