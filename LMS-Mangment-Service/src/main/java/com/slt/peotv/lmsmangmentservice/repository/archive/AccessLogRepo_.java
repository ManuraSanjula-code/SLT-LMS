package com.slt.peotv.lmsmangmentservice.repository.archive;

import com.slt.peotv.lmsmangmentservice.entity.archive.Logs_.AccessLogEntity_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessLogRepo_ extends JpaRepository<AccessLogEntity_, Long> {
}
