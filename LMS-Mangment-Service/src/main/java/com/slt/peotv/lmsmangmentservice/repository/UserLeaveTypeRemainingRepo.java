package com.slt.peotv.lmsmangmentservice.repository;

import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeRemaining;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLeaveTypeRemainingRepo extends CrudRepository<UserLeaveTypeRemaining, Long> {
    Optional<UserLeaveTypeRemaining> findByPublicId(String publicId);
    List<UserLeaveTypeRemaining> findUserLeaveTypeRemainingByUserAndLeaveType(UserEntity userEntity, LeaveTypeEntity leaveTypeEntity);
    List<UserLeaveTypeRemaining> findUserLeaveTypeRemainingByUser(UserEntity userEntity);
}
