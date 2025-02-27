package com.slt.peotv.lmsmangmentservice.entity.card;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "InOut")
@Setter
@Getter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InOutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String employeeID;
    private String userId;
    private Date date;
    private Date punchInMoa; // earliest moaning time -- date
    private Date punchInEv; // earliest eve time -- date

    private Time timeMoa; // earliest moaning time -- time
    private Time timeEve;// earliest eve time -- time

    @Builder.Default
    private Boolean InOut = false;

    @Builder.Default
    private Boolean isMoaning = false;

    @Builder.Default
    private Boolean isEvening = false;
}
