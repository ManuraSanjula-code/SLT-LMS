package com.slt.peotv.lmsmangmentservice.repository;

import com.slt.peotv.lmsmangmentservice.entity.Movement.MovementsEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovementsRepo extends CrudRepository<MovementsEntity, Long> {
    List<MovementsEntity> findAllByUser(UserEntity user);
    Optional<MovementsEntity> findByPublicId(String publicId);
    List<MovementsEntity> findByDueDateBefore(Date currentDate);

    @Query("SELECT e FROM YourEntity e WHERE e.dueDate < :currentDate")
    List<MovementsEntity> findOverdueEntities(@Param("currentDate") Date currentDate);
    List<MovementsEntity> findByHappenDate(Date happenDate);

}
