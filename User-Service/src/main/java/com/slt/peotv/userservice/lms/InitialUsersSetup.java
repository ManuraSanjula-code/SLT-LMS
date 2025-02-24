package com.slt.peotv.userservice.lms;

import java.util.Arrays;
import java.util.Collection;

import com.slt.peotv.userservice.lms.entity.AuthorityEntity;
import com.slt.peotv.userservice.lms.entity.RoleEntity;
import com.slt.peotv.userservice.lms.entity.UserEntity;
import com.slt.peotv.userservice.lms.entity.company.ProfilesEntity;
import com.slt.peotv.userservice.lms.entity.company.SectionEntity;
import com.slt.peotv.userservice.lms.repository.*;
import com.slt.peotv.userservice.lms.shared.Roles;
import com.slt.peotv.userservice.lms.shared.Utils;
import com.slt.peotv.userservice.lms.shared.model.request.ProfileReq;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
public class InitialUsersSetup {

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ProfilesRepo profilesRepo;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    Utils utils;

    @Autowired
    UserRepository userRepository;
    
    private final ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private SectionRepo sectionRepo;

    @EventListener
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("From Application ready event...");

        AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
        AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
        AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");

        createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority,writeAuthority));
        RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority,writeAuthority, deleteAuthority));

        if(roleAdmin == null) return;

        UserEntity adminUser = new UserEntity();
        adminUser.setFirstName("Manura");
        adminUser.setLastName("Sanjula");
        adminUser.setEmail("manura@test.com");
        adminUser.setEmailVerificationStatus(false);
        adminUser.setUserId(utils.generateUserId(30));
        adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("12345678"));
        adminUser.setRoles(Arrays.asList(roleAdmin));

        UserEntity storedUserDetails = userRepository.findByEmail("manura@test.com");
        if (storedUserDetails == null) {
            userRepository.save(adminUser);
        }
    }

    @Transactional
    protected AuthorityEntity createAuthority(String name) {

        AuthorityEntity authority = authorityRepository.findByName(name);
        if (authority == null) {
            authority = new AuthorityEntity(name);
            authorityRepository.save(authority);
        }
        return authority;
    }

    @Transactional
    protected RoleEntity createRole(
            String name, Collection<AuthorityEntity> authorities) {

        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }
        return role;
    }

    @Transactional
    protected ProfilesEntity createProfiles(String name, ProfileReq req) {
        ProfilesEntity map = modelMapper.map(req, ProfilesEntity.class);
        map.setPublicId(utils.generateAddressId(30));
        map.setName(name);
        return profilesRepo.save(map);
    }

    @Transactional
    protected SectionEntity createSection(String name) {
        SectionEntity section = new SectionEntity();
        section.setPublicId(utils.generateAddressId(30));
        section.setSection(name);
        return sectionRepo.save(section);
    }


}