package com.slt.peotv.lmsmangmentservice.entity.User.basic;

import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name="roles")
@Setter
@Getter
@EqualsAndHashCode
public class RoleEntity implements Serializable {

	private static final long serialVersionUID = 5605260522147928803L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	public String publicId;
	
	@Column(nullable=false, length=20)
	private String name;
	
	@ManyToMany(mappedBy="roles")
	private Collection<UserEntity> users;
	
	@ManyToMany(cascade= { CascadeType.PERSIST }, fetch = FetchType.EAGER )
	@JoinTable(name="roles_authorities", 
			joinColumns=@JoinColumn(name="roles_id",referencedColumnName="id"), 
			inverseJoinColumns=@JoinColumn(name="authorities_id",referencedColumnName="id"))
	private Collection<AuthorityEntity> authorities;
 	
	public RoleEntity() {}
	
	public RoleEntity(String name) {
		 this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(Collection<UserEntity> users) {
		this.users = users;
	}

	public Collection<AuthorityEntity> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<AuthorityEntity> authorities) {
		this.authorities = authorities;
	}

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		RoleEntity that = (RoleEntity) o;
		return id == that.id && Objects.equals(publicId, that.publicId) && Objects.equals(name, that.name) && Objects.equals(users, that.users) && Objects.equals(authorities, that.authorities);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, publicId, name, users, authorities);
	}
}
