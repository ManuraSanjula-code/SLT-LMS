package com.slt.peotv.lmsmangmentservice.repository;

import com.slt.peotv.lmsmangmentservice.entity.Leave.LeaveEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepo extends JpaRepository<LeaveEntity, Long> {
    List<LeaveEntity> findByUser(UserEntity user);
    Optional<LeaveEntity> findByPublicId(String publicId);

}
