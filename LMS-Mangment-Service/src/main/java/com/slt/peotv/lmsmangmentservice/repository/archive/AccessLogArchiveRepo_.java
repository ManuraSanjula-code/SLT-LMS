package com.slt.peotv.lmsmangmentservice.repository.archive;

import com.slt.peotv.lmsmangmentservice.entity.archive.Logs_.AccessLogArchiveEntity_;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessLogArchiveRepo_ extends CrudRepository<AccessLogArchiveEntity_, Long> {
}
