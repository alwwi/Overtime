package com.phincon.talents.app.controllers.api.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.phincon.talents.app.controllers.api.admin.view.Views;
import com.phincon.talents.app.dao.*;
import com.phincon.talents.app.dto.CustomGenericException;
import com.phincon.talents.app.dto.admin.SecMatrixSetupForUpsertDTO;
import com.phincon.talents.app.model.SecGroups;
import com.phincon.talents.app.model.SecMatrixSecurity;
import com.phincon.talents.app.model.hr.*;
import com.phincon.talents.app.services.admin.GeneralSettingService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by Phincon on 26/03/2019.
 */

@RestController
@RequestMapping("api/admin")
public class AdSecurityMatrixController extends GeneralSettingService {

    @Autowired
    SecMatrixSecurityRepository repository;

    @Autowired
    SecGroupsRepository repositoryGroup;

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

    public AdSecurityMatrixController() {
        super();
        this.objectName ="Security Matrix";
    }

    @JsonView(Views.Find.class)
    @RequestMapping(value = "secMatrix", method = RequestMethod.GET)
    public ResponseEntity<?> getListData(@RequestParam(value = "name", required = false)String name,
                                         @RequestParam(value = "page", required = false,defaultValue="0") Integer page,
                                         @RequestParam(value = "size", required = false,defaultValue="15") Integer size){

        if (name!=null)
            name = "%" + name + "%";


        Sort sorting = Sort.by(Sort.Direction.ASC, "name");
        PageRequest pageRequest = PageRequest.of(page, size,sorting);

        Page<SecMatrixSecurity> lstData = repository.findAllWCriteria(name, pageRequest);
        return new ResponseEntity<>(lstData, HttpStatus.OK);
    }

    @RequestMapping(value = "secMatrix/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getData(@PathVariable("id")String id){
        return this.getDetailData(repository.findById(id));
    }

    @RequestMapping(value = "secMatrix", method = RequestMethod.POST)
    public ResponseEntity<?> upsertData(@RequestBody SecMatrixSetupForUpsertDTO request){
/*
        if (request.getGroupId()==null)
            throw new CustomGenericException("Group is Required!");
*/
        Optional<SecGroups> optGrp = repositoryGroup.findById(request.getGroupId());

        if (!optGrp.isPresent())
            throw new CustomGenericException("Group is Not Found!");

        if ("Overtime".equalsIgnoreCase(optGrp.get().getGroupName())){
            throw new CustomGenericException("Konfigurasi Matrix untuk modul Overtime telah dilewati otomatis");
        }

        if (request.getWorklocationId()==null && request.getWorklocationType()==null
                && request.getOrganizationId()==null && request.getJobTitleId()==null && request.getPositionLevelId()==null
                && request.getEmployeeId()==null && request.getGradeId()==null)
            throw new CustomGenericException("Fill at least one criteria!");

        if (request.getName()==null || request.getName().isEmpty())
            throw new CustomGenericException("Name is Required!");

        if (request.getOrganizationId()!=null){
            Optional<Organization> optOrg = organizationRepository.findById(request.getOrganizationId());
            if (!optOrg.isPresent())
                throw new CustomGenericException("Organization not Found!");
        }

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

        SecMatrixSecurity currData = new SecMatrixSecurity();
        BeanUtils.copyProperties(request, currData);
        return this.upsertData(currData, repository);
    }

    @RequestMapping(value = "secMatrix/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteData(@RequestBody SecMatrixSetupForUpsertDTO request) {
        return this.deleteData(repository.findById(request.getId()), request.getId(), repository);
    }
}
