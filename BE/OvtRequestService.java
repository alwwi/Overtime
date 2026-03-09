package com.phincon.talents.app.services;

import java.sql.Time;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.phincon.talents.app.dao.OvtRequestRepository;

import com.phincon.talents.app.model.hr.OvtRequest;

@Service
public class OvtRequestService {
	@Autowired
	OvtRequestRepository ovtRequestRepository;
	
	@Autowired
	TalentsTransactionNoService talentsTransactionNoService;
	
	@Transactional
	public OvtRequest createOvertimeRequest(Time totalHoursGross,String categoryId,String employmentId,
			String remark,Date startDate,Date endDate)
	{
		OvtRequest overtimeRequest= new OvtRequest();
		overtimeRequest.setEmploymentId(employmentId);
		String requestNo = talentsTransactionNoService.getTransactionNextVal("Overtime","Overtime");	
		overtimeRequest.setTotalHoursGross(totalHoursGross);
		overtimeRequest.setRequestDate(new Date());
		overtimeRequest.setStartDate(startDate);
		overtimeRequest.setEndDate(endDate);
		overtimeRequest.setRemark(remark);
		overtimeRequest.setRequestNo(requestNo);
		overtimeRequest.setCategoryId(categoryId);
		OvtRequest result = ovtRequestRepository.save(overtimeRequest);
		return result;
	}
}
