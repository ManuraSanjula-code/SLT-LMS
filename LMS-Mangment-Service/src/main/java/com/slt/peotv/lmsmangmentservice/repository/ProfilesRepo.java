package com.slt.peotv.lmsmangmentservice.repository;

import com.slt.peotv.lmsmangmentservice.entity.User.company.ProfilesEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfilesRepo  extends CrudRepository<ProfilesEntity, Long> {
    ProfilesEntity findByName(String name);
}
