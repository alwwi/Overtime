package com.phincon.external.app.controller.salesforce;

import com.phincon.external.app.dao.JobFamilyRepository;
import com.phincon.external.app.dao.JobTitleRepository;
import com.phincon.external.app.model.hr.JobFamily;
import com.phincon.external.app.model.hr.JobTitle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("salesforce")
public class JobTitleController {
	@Autowired
	JobTitleRepository jobTitleRepository;
	@Autowired
	JobFamilyRepository jobFamilyRepository;

	@RequestMapping(value = "/jobtitle", method = RequestMethod.POST)
	public ResponseEntity<List<JobTitle>> upsert(@RequestBody List<JobTitle> requests) {
		Logger log = LoggerFactory.getLogger(JobTitleController.class);
		List<JobTitle> listVal = new ArrayList<JobTitle>();
		//System.out.println("Start Job title countdata=" + requests.size());
		for (JobTitle request : requests) {
			log.info("Ext ID: " + request.getExtId());
			Optional<JobTitle> optResult = jobTitleRepository.findByExtId(request.getExtId());
			Optional<JobFamily> optJobFamily = jobFamilyRepository.findByExtId(request.getJobFamilyExtId());
			
			JobTitle val = new JobTitle();
			if (optResult.isPresent()) {
				val = optResult.get();
			}
			
			if(optJobFamily.isPresent()) {
				val.setJobFamilyId(optJobFamily.get().getId());
			}
			
			val.setName(request.getName());
			
			val.setDescription(request.getDescription());
			val.setStartDate(request.getStartDate());
			val.setEndDate(request.getEndDate());
			val.setFlagOvertime(request.getFlagOvertime());
			val.setContractMonth(request.getContractMonth());
			val.setExtId(request.getExtId());
			listVal.add(val);
		}
		//System.out.println("End Job title countdata=" + listVal.size());
		jobTitleRepository.saveAll(listVal);
		return new ResponseEntity<List<JobTitle>>(listVal, HttpStatus.OK);
	}
}
