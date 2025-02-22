package com.slt.peotv.lmsmangmentservice.entity.User.basic;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;


@Entity
@Table(name="authorities")
@Setter
@Getter
@EqualsAndHashCode
public class AuthorityEntity implements Serializable {

	private static final long serialVersionUID = -5828101164006114538L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	public String publicId;

	@Column(nullable=false, length=20)
	private String name;
	
	@ManyToMany(mappedBy="authorities")
	private Collection<RoleEntity> roles;

	public AuthorityEntity() {}
	
	public AuthorityEntity(String name) {
		 this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(Collection<RoleEntity> roles) {
		this.roles = roles;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		AuthorityEntity that = (AuthorityEntity) o;
		return id == that.id && Objects.equals(publicId, that.publicId) && Objects.equals(name, that.name) && Objects.equals(roles, that.roles);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, publicId, name, roles);
	}
}
