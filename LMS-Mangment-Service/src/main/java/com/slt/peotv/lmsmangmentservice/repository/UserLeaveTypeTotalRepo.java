package com.slt.peotv.lmsmangmentservice.repository;

import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeTotalEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLeaveTypeTotalRepo extends CrudRepository<UserLeaveTypeTotalEntity, Long> {
    Optional<UserLeaveTypeTotalEntity> findByPublicId(String publicId);
    List<UserLeaveTypeTotalEntity> findUserLeaveTypeTotalByUserAndLeaveType(UserEntity userEntity, LeaveTypeEntity leaveTypeEntity);
}
