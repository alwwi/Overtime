package com.phincon.talents.app.controllers.api.admin;

import com.phincon.talents.app.dao.*;
import com.phincon.talents.app.dto.CustomGenericException;
import com.phincon.talents.app.dto.admin.SecGroupsDetailForUpsertDTO;
import com.phincon.talents.app.dto.admin.SecGroupsForUpsertDTO;
import com.phincon.talents.app.dto.admin.SecMatrixSetupForUpsertDTO;
import com.phincon.talents.app.model.GroupAuthorities;
import com.phincon.talents.app.model.SecGroups;
import com.phincon.talents.app.model.SecMatrixSecurity;
import com.phincon.talents.app.model.hr.*;
import com.phincon.talents.app.projection.general.groupauthorities.GroupAuthoritiesAllProj;
import com.phincon.talents.app.services.admin.GeneralSettingService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by Phincon on 26/03/2019.
 */
@RestController
@RequestMapping("api/admin")
public class AdSecurityGroupsController extends GeneralSettingService {
    @Autowired
    SecGroupsRepository repository;

    @Autowired
    GroupAuthoritiesRepository repositoryDtl;

    @Autowired
    SecMatrixSecurityRepository repositoryMtx;

    @Autowired
    VwEmpAssignmentRepository empAssignmentRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    JobTitleRepository jobTitleRepository;

    @Autowired
    WorkLocationRepository workLocationRepository;

    @Autowired
    PositionLevelRepository positionLevelRepository;

    @Autowired
    GradeRepository gradeRepository;

    public static String secGroup_str ="Security Groups";
    public static String secGroupDtl_str ="Authority";
    public static String secGroupMtx_str ="Matrix";

    @RequestMapping(value = "secGroups", method = RequestMethod.GET)
    public ResponseEntity<?> getListData(@RequestParam(value = "name", required = false)String name){

        if (name!=null)
            name = "%" + name +"%";

        List<SecGroups> findAll = repository.findAllWCriteria(name);
        return new ResponseEntity<>(findAll, HttpStatus.OK);
    }

    @RequestMapping(value = "secGroups/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getData(@PathVariable("id")String id){
        return this.getDetailData(repository.findById(id));
    }

    @RequestMapping(value = "secGroups/{id}/detail", method = RequestMethod.GET)
    public ResponseEntity<?> getDataDetailList(@PathVariable("id")String id){
        return this.getDataList(repositoryDtl.findByGroupIdOrderByAuthority(id));
    }

    @RequestMapping(value = "secGroups/{id}/detail/{idDetail}", method = RequestMethod.GET)
    public ResponseEntity<?> getDataDetail(@PathVariable("id")String id, @PathVariable("idDetail")String idDetail){
        Optional<GroupAuthoritiesAllProj> optPat = repositoryDtl.findByIdFetch(idDetail);
        if (optPat.isPresent()){
            if(optPat.get().getGroupId().equals(id))
                return this.getDetailData(optPat);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "secGroups/{id}/matrix", method = RequestMethod.GET)
    public ResponseEntity<?> getDataMatrix(@PathVariable("id")String id){
        return this.getDataList(repositoryMtx.findByGroupIdOrderByName(id));
    }

    @RequestMapping(value = "secGroups/{id}/matrix/{idDetail}", method = RequestMethod.GET)
    public ResponseEntity<?> getDataMatrixDetail(@PathVariable("id")String id, @PathVariable("idDetail")String idDetail){
        Optional<SecMatrixSecurity> optPat = repositoryMtx.findById(idDetail);
        if (optPat.isPresent()){
            if(optPat.get().getGroupId().equals(id))
                return this.getDetailData(optPat);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "secGroups", method = RequestMethod.POST)
    public ResponseEntity<?> upsertData(@RequestBody SecGroupsForUpsertDTO request){

        if (request.getGroupName()==null || request.getGroupName().isEmpty())
            throw new CustomGenericException("Group Name is Required!");

        if (request.getActiveFlag()==null)
            request.setActiveFlag(true);

        SecGroups currData;
        if (request.getId()!=null) {
            Optional<SecGroups> optSec = repository.findById(request.getId());
            if (!optSec.isPresent())
                throw new CustomGenericException("Security Group is Not Found!");

            currData = optSec.get();
            currData.setGroupName(request.getGroupName());
            currData.setActiveFlag(request.getActiveFlag());
        }
        else {
            currData = new SecGroups();
            BeanUtils.copyProperties(request, currData);
            currData.setTalentsLocked(false);
        }

        this.objectName =secGroup_str;
        return this.upsertData(currData, repository);
    }

    @RequestMapping(value = "secGroupsDetail", method = RequestMethod.POST)
    public ResponseEntity<?> upsertDataDetail(@RequestBody SecGroupsDetailForUpsertDTO request){

        if (request.getGroupId()==null)
            throw new CustomGenericException("Group Name is Required!");

        Optional<SecGroups> optGrp = repository.findById(request.getGroupId());
        Optional<GroupAuthorities> optData;
        if (!optGrp.isPresent())
            throw new CustomGenericException("Group is Not Found!");

        if (request.getId()!=null) {
            optData = repositoryDtl.findById(request.getId());
            if (!optData.isPresent())
                throw new CustomGenericException("Authority is Not Found!");

            if (!optData.get().getGroupId().equals(request.getGroupId()))
                throw new CustomGenericException("Group is Not Found!");
        }

        if (request.getAuthority()==null || request.getAuthority().isEmpty())
            throw new CustomGenericException("Authority is Required!");

        /*
        optData = repositoryDtl.findByGroupIdAndAuthority(request.getGroupId(), request.getAuthority());

        if (optData.isPresent())
        {
            if (request.getId()==null)
                throw new CustomGenericException("Authority already exists!");
            else
                if (!optData.get().getId().equals(request.getId()))
                    throw new CustomGenericException("Authority already exists!");
        }*/

        GroupAuthorities currData;
        currData = new GroupAuthorities();
        BeanUtils.copyProperties(request, currData);

        this.objectName =secGroupDtl_str;
        return this.upsertData(currData, repositoryDtl);
    }

    @RequestMapping(value = "secGroupsMatrix", method = RequestMethod.POST)
    public ResponseEntity<?> upsertDataMatrix(@RequestBody SecMatrixSetupForUpsertDTO request){

        if (request.getGroupId()==null)
            throw new CustomGenericException("Group is Required!");

        Optional<SecGroups> optGrp = repository.findById(request.getGroupId());

        if (!optGrp.isPresent())
            throw new CustomGenericException("Group is Not Found!");
        
        System.out.println("____Group OK");
        if (request.getWorklocationId()==null && request.getWorklocationType()==null
                && request.getOrganizationId()==null && request.getJobTitleId()==null && request.getPositionLevelId()==null
                && request.getEmployeeId()==null && request.getGradeId()==null && request.getEmploymentStatus()==null
                && request.getLengthOfService()==null) 
            throw new CustomGenericException("Fill at least one criteria!");
        System.out.println("____Params OK");

        if (request.getName()==null || request.getName().isEmpty())
            throw new CustomGenericException("Name is Required!");
        System.out.println("____Name OK");


        if (request.getOrganizationId()!=null){
            Optional<Organization> optOrg = organizationRepository.findById(request.getOrganizationId());
            if (!optOrg.isPresent())
                throw new CustomGenericException("Organization not Found!");
        }
        System.out.println("______ Org OK");

        if (request.getEmployeeId()!=null){
            Optional<VwEmpAssignment> optOrg = empAssignmentRepository.findById(request.getEmployeeId());
            if (!optOrg.isPresent())
                throw new CustomGenericException("Employee not Found!");
        }

        if (request.getWorklocationId()!=null){
            Optional<WorkLocation> optWrk = workLocationRepository.findById(request.getWorklocationId());
            if (!optWrk.isPresent())
                throw new CustomGenericException("Work location not Found!");
        }

        if (request.getJobTitleId()!=null){
            Optional<JobTitle> optDt = jobTitleRepository.findById(request.getJobTitleId());
            if (!optDt.isPresent())
                throw new CustomGenericException("Job Title not Found!");
        }

        if (request.getPositionLevelId()!=null){
            Optional<PositionLevel> optDt = positionLevelRepository.findById(request.getPositionLevelId());
            if (!optDt.isPresent())
                throw new CustomGenericException("Position Level not Found!");
        }

        if (request.getGradeId()!=null){
            Optional<Grade> optDt = gradeRepository.findById(request.getGradeId());
            if (!optDt.isPresent())
                throw new CustomGenericException("Grade not Found!");
        }
        this.objectName =secGroupMtx_str;
        SecMatrixSecurity currData = new SecMatrixSecurity();
        BeanUtils.copyProperties(request, currData);
        return this.upsertData(currData, repositoryMtx);
    }

    @RequestMapping(value = "secGroups/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteData(@RequestBody SecGroupsForUpsertDTO request) {
        this.objectName =secGroup_str;
        return this.deleteData(repository.findById(request.getId()), request.getId(), repository);
    }

    @RequestMapping(value = "secGroupsDetail/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteDataDetail(@RequestBody SecGroupsDetailForUpsertDTO request) {
        this.objectName =secGroupDtl_str;
        return this.deleteData(repositoryDtl.findById(request.getId()), request.getId(), repositoryDtl);
    }

    @RequestMapping(value = "secGroupsMatrix/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteData(@RequestBody SecMatrixSetupForUpsertDTO request) {
        this.objectName =secGroupMtx_str;
        return this.deleteData(repositoryMtx.findById(request.getId()), request.getId(), repositoryMtx);
    }
}
