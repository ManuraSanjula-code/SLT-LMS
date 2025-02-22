package com.slt.peotv.lmsmangmentservice.entity.archive.Logs_;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "accesslog")
public class AccessLogEntity_ {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(length = 45, nullable = false)
    private String userId; // Employee Id

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String profilePicture;

    @Column(name = "LogDate", length = 45, nullable = false)
    private String logDate;

    @Column(name = "LogTime", length = 45, nullable = false)
    private String logTime;

    @Column(name = "TerminalID", length = 45, nullable = false)
    private String terminalId;

    @Column(name = "InOut", length = 45, nullable = false)
    private String inOut;

    @Column(name = "read", length = 45, nullable = false)
    private String read;

    @Column(name = "processed", nullable = false, columnDefinition = "int(10) unsigned default '0'")
    private Integer processed;

    @Column(name = "etl_run_time", nullable = false, columnDefinition = "timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP")
    private Timestamp etlRunTime;
}

