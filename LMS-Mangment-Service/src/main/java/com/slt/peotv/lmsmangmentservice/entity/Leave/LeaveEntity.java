package com.slt.peotv.lmsmangmentservice.entity.Leave;

import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveCategoryEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name="leave_requests")
@Setter
@Getter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    public String publicId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "submit_date", nullable = false)
    private Date submitDate;

    @Column(name = "from_date", nullable = false)
    private Date fromDate;

    @Column(name = "to_date", nullable = false)
    private Date toDate;

    @ManyToOne
    @JoinColumn(name = "leave_category_id")
    private LeaveCategoryEntity leaveCategory;

    @ManyToOne
    @JoinColumn(name = "leave_type_id")
    private LeaveTypeEntity leaveType;

    @Column(name = "is_supervised_approved", nullable = false, columnDefinition = "int(10) unsigned default '0'")
    @Builder.Default
    private Boolean isSupervisedApproved = false;

    @Column(name = "is_HOD_approved", nullable = false, columnDefinition = "int(10) unsigned default '0'")
    @Builder.Default
    private Boolean isHODApproved = false;

    @Column(name = "is_nopay", columnDefinition = "int(10) unsigned default '0'")
    private Integer isNoPay = 0;

    @Column(name = "num_of_days")
    private Long numOfDays;

    @Column(name = "description")
    private String description;

    @Column(name = "is_halfday")
    private Boolean isHalfDay;

    @Builder.Default
    private Boolean unSuccessful = false;
    @Builder.Default
    private Boolean isLate = false;
    @Builder.Default
    private Boolean isLateCover = false;
    @Builder.Default
    private Boolean isShort_Leave = false;
    @Builder.Default
    private Boolean isPending = false;
    @Builder.Default
    private Boolean isAccepted = false;
    @Builder.Default
    private Boolean notUsed = false;

    private Date happenDate;
}
