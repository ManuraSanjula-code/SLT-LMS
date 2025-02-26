package com.slt.peotv.userservice.lms.entity;

import com.slt.peotv.userservice.lms.entity.company.ProfilesEntity;
import com.slt.peotv.userservice.lms.entity.company.SectionEntity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
 
@Entity
@Table(name="users")
public class UserEntity implements Serializable {
 
	private static final long serialVersionUID = 5313493413859894403L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(nullable=false)
	private String userId; //// Employee Id
	///
	@Column(nullable=false)
	private String employeeId;

	@Column(nullable=false, length=50)
	private String firstName;
	
	@Column(nullable=false, length=50)
	private String lastName;
	
	@Column(nullable=false, length=120)
	private String email;
	
	@Column(nullable=false)
	private String encryptedPassword;
	
	private String emailVerificationToken;
	
	@Column(nullable=false)
	private Boolean emailVerificationStatus = false;

	@OneToMany(mappedBy="userDetails", cascade=CascadeType.ALL)
	private List<AddressEntity> addresses;

	private int defaultAddress;

	private String profilePic;

	//==================================================

	@Column(name = "gender", length = 1,nullable=false)
	private String gender;

	@Column(name = "phone", length = 45, nullable=false)
	private String phone;

	@Column(name = "section", length = 45,nullable=false)
	private String section;

	@Column(name = "send_no_pay_alert", columnDefinition = "int(11) default 1",nullable=false)
	private Integer sendNoPayAlert;

	@Column(name = "is_slt_emp", nullable = false, columnDefinition = "int(10) unsigned default 0")
	private Integer isSltEmp;

	@Column(name = "is_weekend_works", nullable = false, columnDefinition = "int(10) unsigned default 0")
	private Integer isWeekendWorks;

	@Column(name = "hod_id", nullable = false)
	private Integer hodId;

	@Column(nullable=false)
	private Integer active;

	@ManyToMany(cascade= { CascadeType.PERSIST }, fetch = FetchType.EAGER )
	@JoinTable(name="users_roles",
			joinColumns=@JoinColumn(name="users_id",referencedColumnName="id"),
			inverseJoinColumns=@JoinColumn(name="roles_id",referencedColumnName="id"))
	@Column(nullable=false)
	private Collection<RoleEntity> roles;

	@ManyToMany(cascade= { CascadeType.PERSIST }, fetch = FetchType.EAGER )
	@JoinTable(name="users_profile",
			joinColumns=@JoinColumn(name="users_id",referencedColumnName="id"),
			inverseJoinColumns=@JoinColumn(name="profile_id",referencedColumnName="id"))
	@Column(nullable=false)
	private Collection<ProfilesEntity> profiles;

	@ManyToMany(cascade= { CascadeType.PERSIST }, fetch = FetchType.EAGER )
	@JoinTable(name="users_section",
			joinColumns=@JoinColumn(name="users_id",referencedColumnName="id"),
			inverseJoinColumns=@JoinColumn(name="section_id",referencedColumnName="id"))
	@Column(nullable=false)
	private Collection<SectionEntity> sections;

	public Collection<ProfilesEntity> getProfiles() {
		return profiles;
	}

	public void setProfiles(Collection<ProfilesEntity> profiles) {
		this.profiles = profiles;
	}

	public Collection<SectionEntity> getSections() {
		return sections;
	}

	public void setSections(Collection<SectionEntity> sections) {
		this.sections = sections;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public Integer getSendNoPayAlert() {
		return sendNoPayAlert;
	}

	public void setSendNoPayAlert(Integer sendNoPayAlert) {
		this.sendNoPayAlert = sendNoPayAlert;
	}

	public Integer getIsSltEmp() {
		return isSltEmp;
	}

	public void setIsSltEmp(Integer isSltEmp) {
		this.isSltEmp = isSltEmp;
	}

	public Integer getIsWeekendWorks() {
		return isWeekendWorks;
	}

	public void setIsWeekendWorks(Integer isWeekendWorks) {
		this.isWeekendWorks = isWeekendWorks;
	}

	public Integer getHodId() {
		return hodId;
	}

	public void setHodId(Integer hodId) {
		this.hodId = hodId;
	}

	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public int getDefaultAddress() {
		return defaultAddress;
	}

	public void setDefaultAddress(int defaultAddress) {
		this.defaultAddress = defaultAddress;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getEmailVerificationToken() {
		return emailVerificationToken;
	}

	public void setEmailVerificationToken(String emailVerificationToken) {
		this.emailVerificationToken = emailVerificationToken;
	}

	public Boolean getEmailVerificationStatus() {
		return emailVerificationStatus;
	}

	public void setEmailVerificationStatus(Boolean emailVerificationStatus) {
		this.emailVerificationStatus = emailVerificationStatus;
	}

	@Column(nullable = false)
	public List<AddressEntity> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressEntity> addresses) {
		this.addresses = addresses;
	}

	@Column(nullable = false)
	public Collection<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(Collection<RoleEntity> roles) {
		this.roles = roles;
	}


	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
}
