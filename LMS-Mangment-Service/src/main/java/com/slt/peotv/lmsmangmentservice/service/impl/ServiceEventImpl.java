package com.slt.peotv.lmsmangmentservice.service.impl;

import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryRemainingEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveCategoryTotalEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeRemaining;
import com.slt.peotv.lmsmangmentservice.entity.Leave.category.UserLeaveTypeTotalEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveCategoryEntity;
import com.slt.peotv.lmsmangmentservice.entity.Leave.types.LeaveTypeEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.basic.AuthorityEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.basic.RoleEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.company.ProfilesEntity;
import com.slt.peotv.lmsmangmentservice.entity.User.company.SectionEntity;
import com.slt.peotv.lmsmangmentservice.exceptions.LMSServiceException_AllReadyExits;
import com.slt.peotv.lmsmangmentservice.model.ProfileReq;
import com.slt.peotv.lmsmangmentservice.repository.*;
import com.slt.peotv.lmsmangmentservice.service.LMS_Service;
import com.slt.peotv.lmsmangmentservice.service.ServiceEvent;
import com.slt.peotv.lmsmangmentservice.exceptions.ErrorMessages;
import com.slt.peotv.lmsmangmentservice.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ServiceEventImpl implements ServiceEvent {
    @Autowired
    private ProfilesRepo profilesRepo;
    @Autowired
    private RoleRepo roleRepository;
    @Autowired
    private AuthorityRepo authorityRepository;
    @Autowired
    private SectionRepo sectionRepo;
    @Autowired
    private UserLeaveCategoryRemainingRepo userLeaveCategoryRemainingRepo;
    @Autowired
    private UserLeaveCategoryTotalRepo userLeaveCategoryTotalRepo;
    @Autowired
    private UserLeaveTypeTotalRepo userLeaveTypeTotalRepo;
    @Autowired
    private UserLeaveTypeRemainingRepo userLeaveTypeRemainingRepo;
    @Autowired
    private LeaveCategoryRepo leaveCategoryRepo;
    @Autowired
    private LeaveTypeRepo leaveTypeRepo;
    @Autowired
    private LMS_Service lmsService;
    @Autowired
    private Utils utils;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public void userCreatedEvent() {

    }

    private UserEntity getUser(String user_id, String employee_id){
        UserEntity userByEmployeeId = lmsService.getUserByEmployeeId(user_id);
        UserEntity userByUserId = lmsService.getUserByUserId(user_id);

        UserEntity user = null;

        if (userByEmployeeId != null)
            user = userByEmployeeId;
        else if (userByUserId != null)
            user = userByUserId;
        return user;
    }

    @Override
    public void saveUserLeaveCategoryRemaining(String cat_name, String user_id, String employee_id, Integer remainingLeaves) {

        Optional<LeaveCategoryEntity> byName = leaveCategoryRepo.findByName(cat_name);
        UserEntity user = getUser(user_id, employee_id);

        if(byName.isPresent() && user != null) {
            LeaveCategoryEntity leaveCategoryEntity = byName.get();

            UserLeaveCategoryRemainingEntity userLeaveCategoryRemainingEntity = new UserLeaveCategoryRemainingEntity();
            userLeaveCategoryRemainingEntity.setPublicId(utils.generateId(10));
            userLeaveCategoryRemainingEntity.setLeaveCategory(leaveCategoryEntity);
            userLeaveCategoryRemainingEntity.setUser(user);
            userLeaveCategoryRemainingRepo.save(userLeaveCategoryRemainingEntity);

        }else{
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
    }

    @Override
    public UserLeaveCategoryRemainingEntity getUserLeaveCategoryRemaining(String cat_name, String user_id, String employee_id) {

        UserEntity user = getUser(user_id, employee_id);
        Optional<LeaveCategoryEntity> byName = leaveCategoryRepo.findByName(cat_name);

        if(user != null && byName.isPresent()) {
            List<UserLeaveCategoryRemainingEntity> byLeaveCategoryAndUser = userLeaveCategoryRemainingRepo.findByLeaveCategoryAndUser(byName.get(), user);
            if(byLeaveCategoryAndUser.isEmpty())
                throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
            else
                return byLeaveCategoryAndUser.get(0);
        }else{
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
    }

    @Override
    public List<UserLeaveCategoryRemainingEntity> getUserLeaveCategoryRemaining(String user_id, String employee_id) {
        return null;
    }

    @Override
    public void deleteUserLeaveCategoryRemaining(String publicId) {
        Optional<UserLeaveCategoryRemainingEntity> byPublicId = userLeaveCategoryRemainingRepo.findByPublicId(publicId);
        if(byPublicId.isPresent())
            userLeaveCategoryRemainingRepo.delete(byPublicId.get());
        else
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
    }

    @Override
    public void saveUserLeaveCategoryTotal(String cat_name, String user_id, String employee_id, Integer totalLeaves) {

        Optional<LeaveCategoryEntity> byName = leaveCategoryRepo.findByName(cat_name);
        UserEntity  user = getUser(user_id, employee_id);

        if(user != null && byName.isPresent()) {
            LeaveCategoryEntity leaveCategoryEntity = byName.get();
            UserLeaveCategoryTotalEntity userLeaveCategoryTotalEntity = new UserLeaveCategoryTotalEntity();
            userLeaveCategoryTotalEntity.setPublicId(utils.generateId(10));
            userLeaveCategoryTotalEntity.setLeaveCategory(leaveCategoryEntity);
            userLeaveCategoryTotalEntity.setUser(user);
            userLeaveCategoryTotalRepo.save(userLeaveCategoryTotalEntity);
        }else{
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
    }

    @Override
    public List<UserLeaveCategoryTotalEntity> getUserLeaveCategoryTotal(String user_id, String employee_id) {
        UserEntity  user = getUser(user_id, employee_id);
        if(user != null) return userLeaveCategoryTotalRepo.findByUser(user);
        else throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
    }

    @Override
    public List<UserLeaveCategoryTotalEntity> getUserLeaveCategoryTotal(UserEntity user) {
        if(user != null) return userLeaveCategoryTotalRepo.findByUser(user);
        else throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
    }

    @Override
    public UserLeaveCategoryTotalEntity getUserLeaveCategoryTotal(String cat_name, String user_id, String employee_id) {

        Optional<LeaveCategoryEntity> byName = leaveCategoryRepo.findByName(cat_name);
        UserEntity  user = getUser(user_id, employee_id);

        if(user != null && byName.isPresent()) {
            List<UserLeaveCategoryTotalEntity> byLeaveCategoryAndUser = userLeaveCategoryTotalRepo.findByLeaveCategoryAndUser(byName.get(), user);
            if(byLeaveCategoryAndUser.isEmpty())
                throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
            else
                return byLeaveCategoryAndUser.get(0);
        }else{
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
    }

    @Override
    public void deleteUserLeaveCategoryTotal(String publicId) {
        Optional<UserLeaveCategoryTotalEntity> byPublicId = userLeaveCategoryTotalRepo.findByPublicId(publicId);
        if(byPublicId.isPresent())
            userLeaveCategoryTotalRepo.delete(byPublicId.get());
        else
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
    }

    @Override
    public void saveUserLeaveTypeRemaining(String user_id, String employee_id, Integer remainingLeaves, String type_name) {
        Optional<LeaveTypeEntity> byName = leaveTypeRepo.findByName(type_name);

        UserEntity user = getUser(user_id, employee_id);

        if(byName.isPresent() && user != null) {
            LeaveTypeEntity leaveCategoryEntity = byName.get();

            UserLeaveTypeRemaining userLeaveTypeRemaining = new UserLeaveTypeRemaining();
            userLeaveTypeRemaining.setPublicId(utils.generateId(10));
            userLeaveTypeRemaining.setLeaveType(leaveCategoryEntity);
            userLeaveTypeRemaining.setRemainingLeaves(remainingLeaves);
            userLeaveTypeRemaining.setUser(user);

            userLeaveTypeRemainingRepo.save(userLeaveTypeRemaining);

        }else{
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
    }

    @Override
    public List<UserLeaveTypeRemaining> getUserLeaveTypeRemaining(String user_id, String employee_id) {
        UserEntity  user = getUser(user_id, employee_id);
        if(user != null) return userLeaveTypeRemainingRepo.findUserLeaveTypeRemainingByUser(user);
        else throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
    }

    @Override
    public List<UserLeaveTypeRemaining> getUserLeaveTypeRemaining(UserEntity user) {
        return userLeaveTypeRemainingRepo.findUserLeaveTypeRemainingByUser(user);
    }

    @Override
    public UserLeaveTypeRemaining getUserLeaveTypeRemaining(String type_name, String user_id, String employee_id) {
        Optional<LeaveTypeEntity> byName = leaveTypeRepo.findByName(type_name);
        UserEntity  user = getUser(user_id, employee_id);

        if(user != null && byName.isPresent()) {
            List<UserLeaveTypeRemaining> byLeaveTypeAndUser = userLeaveTypeRemainingRepo.findUserLeaveTypeRemainingByUserAndLeaveType(user,byName.get());
            if(byLeaveTypeAndUser.isEmpty())
                throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
            else
                return byLeaveTypeAndUser.get(0);
        }else{
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
    }

    @Override
    public void deleteUserLeaveTypeRemaining(String publicId) {
        userLeaveTypeRemainingRepo.findByPublicId(publicId).ifPresent(userLeaveTypeRemainingRepo::delete);
    }

    @Override
    public void saveUserLeaveTypeTotal(String user_id, String employee_id, Integer totalLeaves, String type_name) {
        Optional<LeaveTypeEntity> byName = leaveTypeRepo.findByName(type_name);

        UserEntity user = getUser(user_id, employee_id);

        if(byName.isPresent() && user != null) {
            LeaveTypeEntity leaveCategoryEntity = byName.get();

            UserLeaveTypeTotalEntity userLeaveTypeTotal = new UserLeaveTypeTotalEntity();
            userLeaveTypeTotal.setPublicId(utils.generateId(10));
            userLeaveTypeTotal.setLeaveType(leaveCategoryEntity);
            userLeaveTypeTotal.setUser(user);
            userLeaveTypeTotal.setTotalLeaves(totalLeaves);

            userLeaveTypeTotalRepo.save(userLeaveTypeTotal);

        }else{
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
    }

    @Override
    public UserLeaveTypeTotalEntity getUserLeaveTypeTotal(String user_id, String employee_id) {
        return null;
    }

    @Override
    public UserLeaveTypeTotalEntity getUserLeaveTypeTotal(String type_name, String user_id, String employee_id) {
        Optional<LeaveTypeEntity> byName = leaveTypeRepo.findByName(type_name);
        UserEntity  user = getUser(user_id, employee_id);

        if(user != null && byName.isPresent()) {
            List<UserLeaveTypeTotalEntity> byLeaveTypeAndUser = userLeaveTypeTotalRepo.findUserLeaveTypeTotalByUserAndLeaveType(user,byName.get());
            if(byLeaveTypeAndUser.isEmpty())
                throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
            else
                return byLeaveTypeAndUser.get(0);
        }else{
            throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
    }

    @Override
    public void deleteUserLeaveTypeTotal(String publicId) {
        userLeaveTypeTotalRepo.findByPublicId(publicId).ifPresent(userLeaveTypeTotalRepo::delete);
    }

    @Override
    public ProfilesEntity createProfiles(String name, ProfileReq req) {
        if(getProfiles(name) != null) throw new LMSServiceException_AllReadyExits(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        ProfilesEntity map = modelMapper.map(req, ProfilesEntity.class);
        map.setPublicId(utils.generateAddressId(30));
        map.setName(name);
        return profilesRepo.save(map);
    }

    @Override
    public SectionEntity createSection(String name) {
        if(getSection(name) != null) throw new LMSServiceException_AllReadyExits(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
        SectionEntity sectionEntity = new SectionEntity();
        sectionEntity.setSection(name);
        return sectionRepo.save(sectionEntity);
    }

    @Override
    public SectionEntity getSection(String name) {
        SectionEntity section = sectionRepo.findBySection(name);
        if(section != null) throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        return null;
    }

    @Override
    public ProfilesEntity getProfiles(String name) {
        ProfilesEntity profile = profilesRepo.findByName(name);
        if(profile != null) throw new NoSuchElementException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        return null;
    }

    @Override
    public AuthorityEntity createAuthority(String name) {
        AuthorityEntity authority = authorityRepository.findByName(name);
        if (authority == null) {
            authority = new AuthorityEntity(name);
            authorityRepository.save(authority);
        }
        return authority;
    }

    @Override
    public RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }
        return role;
    }
}
