package com.phincon.talents.app.services.dataapproval;

import com.phincon.talents.app.dao.*;
import com.phincon.talents.app.dto.CustomGenericException;
import com.phincon.talents.app.dto.OvertimeHistoryApprovalDTO;
import com.phincon.talents.app.dto.OvtRequestDTO;
import com.phincon.talents.app.dto.SubmitOvertimeDTO;
import com.phincon.talents.app.model.DataApproval;
import com.phincon.talents.app.model.TalentsParameter;
import com.phincon.talents.app.model.hr.OvtRequest;
import com.phincon.talents.app.model.hr.RequestCategoryType;
import com.phincon.talents.app.model.hr.VwEmpAssignment;
import com.phincon.talents.app.services.DataApprovalService;
import com.phincon.talents.app.services.OvtRequestService;
import com.phincon.talents.app.services.TalentsParameterService;
import com.phincon.talents.app.utils.Utils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.phincon.talents.app.model.hr.JobTitle;
import com.phincon.talents.app.dao.JobTitleRepository; 

import jakarta.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OvertimeService {
	@Autowired
	private Environment env;
	
	@Autowired
	VwEmpAssignmentRepository vwEmpAssignmentRepository;
	
	@Autowired
	OvtRequestService ovtRequestService;
	
	@Autowired
	OvtRequestRepository ovtRequestRepository;
	
	@Autowired
	RequestCategoryTypeRepository requestCategoryTypeRepository;
	
	@Autowired
	WorkflowEligibilityRepository workflowEligibilityRepository;
	
	@Autowired
	WorkflowRepository workflowRepository;

	@Autowired
	JobTitleRepository jobTitleRepository;

	@Autowired
	DataApprovalService dataApprovalService;
	
	@Autowired
	TalentsParameterService talentsParameterService;

	@Value("${mtf.sc.jwt.secret}")
	private String secret;

	@Transactional
	public String InsertOvertimeRequest(SubmitOvertimeDTO request, String claimForEmployeeId, String requesterEmployeeId) {
		return this.InsertOvertimeRequestAdmin(request, claimForEmployeeId, requesterEmployeeId, false);
	}
	
	@Transactional
	public String InsertOvertimeRequestAdmin(SubmitOvertimeDTO request, String claimForEmployeeId, String requesterEmployeeId, Boolean isAdmin) {
		String requestNo = "";
		Optional<VwEmpAssignment> employeeViewOpt = vwEmpAssignmentRepository.findById(claimForEmployeeId);
		if (employeeViewOpt.isPresent()) {
			VwEmpAssignment employeeView = employeeViewOpt.get();

			if(employeeView.getJobTitleId() != null){
				Optional<JobTitle> jobTitleOpt = jobTitleRepository.findById(employeeView.getJobTitleId());
				if (jobTitleOpt.isPresent()){
					Integer flagOvt = jobTitleOpt.get().getFlagOvertime();
					if(flagOvt == null || flagOvt == 0){
						throw new CustomGenericException("Karyawan tidak eligible untuk Overtime");
					}
				} else {
					throw new CustomGenericException("Data job title tidak ditemukan");
				}
			} else{
				throw new CustomGenericException("Karyawan tidak memiliki job title");
			}

			Optional<RequestCategoryType> requestCategoryTypeOpt = requestCategoryTypeRepository.findByName("Overtime");
			RequestCategoryType requestCategoryType = requestCategoryTypeOpt.get();
			String categoryId = requestCategoryType.getId();
			Integer crossing = ovtRequestRepository.findCountRequest(employeeView.getEmploymentId(), request.getStartDate());
			if(crossing > 0)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
				throw new CustomGenericException("You have overtime request on "+sdf.format(request.getStartDate())+".");
			}
			
			List<Object[]> shiftByDate = ovtRequestRepository.getShiftByDate(request.getStartDate(), request.getStartDate(),employeeView.getEmploymentId() );
			for(Object[] x:shiftByDate)
			{
				if(x[10]!= null)
				{
					if(!Boolean.valueOf(x[10].toString()) &&x[12] == null)
					{
						//jika bukan off shift dan bukan holiday-holiday idnya kosong
						if(x[19]!=null)
						{
							//Time time = Time.valueOf(x[19].toString());
							LocalTime time = LocalTime.parse(x[19].toString());
							
							Instant.ofEpochMilli(request.getStartDate().getTime())
						      .atZone(ZoneId.systemDefault())
						      .toLocalDate();		
							LocalDateTime startOvertimeHour = Instant.ofEpochMilli(request.getStartDate().getTime())
								      .atZone(ZoneId.systemDefault())
								      .toLocalDate().atTime(time);
							LocalDateTime requestDate = Instant.ofEpochMilli(request.getStartDate().getTime())
								      .atZone(ZoneId.systemDefault()).toLocalDateTime();
							System.out.println("start overtime hour :"+startOvertimeHour);
							System.out.println("start overtime request :"+requestDate);
							if(requestDate.isBefore(startOvertimeHour))
							{
								throw new CustomGenericException("You can only start overtime from "+time);
							}
						}
					}
				}
			}
			
			TalentsParameter backDateLimitParameter = talentsParameterService.findByKeyAndActiveFlag("OVERTIME_REQUEST_BACK_DATE_LIMIT_DAY", true);
			if(backDateLimitParameter!= null)
			{
				Integer value = Integer.valueOf(backDateLimitParameter.getValue());
				Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
				Date backdatedDate = DateUtils.addDays(today, -value);
				if (backdatedDate.compareTo(request.getStartDate()) > 0) {
					throw new CustomGenericException("You can only submit this request backdated "
							+ value + " days from today.");
				}
			}
			
			TalentsParameter minOvertime = talentsParameterService.findByKeyAndActiveFlag("MIN_OVERTIME_MINUTE", true);
			Long minOvertimeLong = Long.valueOf(minOvertime.getValue())*60000;
			//Long requestOvertime = request.getTotalHoursGross().getTime();
			
			String[] timeArray = request.getTotalHoursGross().toString().split(":");
			Long requestOvertime = 0L;
			requestOvertime = requestOvertime+(Long.valueOf(timeArray[0])*3600000);
			requestOvertime = requestOvertime+(Long.valueOf(timeArray[1])*60000);
			
			//System.out.println("min "+minOvertimeLong);
			//System.out.println("curr "+requestOvertime);
			if(requestOvertime.compareTo(minOvertimeLong) < 0)
			{
				throw new CustomGenericException("Minimum overtime request is "
						+ minOvertime.getValue() + " minute(s).");
			}
			OvtRequest ovtRequest = ovtRequestService.createOvertimeRequest(request.getTotalHoursGross(),
					categoryId, employeeView.getEmploymentId(), request.getRemark(), request.getStartDate(),
					request.getEndDate());
			requestNo = ovtRequest.getRequestNo();
			Optional<VwEmpAssignment> requestorViewOpt = vwEmpAssignmentRepository.findById(requesterEmployeeId);
			if (requestorViewOpt.isPresent()) {
				VwEmpAssignment requestorView = requestorViewOpt.get();
				if (!isAdmin) {
					dataApprovalService.createDataApproval(1, requesterEmployeeId,
							RequestCategoryType.Overtime, ovtRequest.getId(), DataApproval.IN_PROGRESS,
							claimForEmployeeId, null, categoryId, requestorView.getWorkLocationType(), requestorView.getPositionLevelId(), requestCategoryType.getName(), requestorView.getRegionalId());
				}
				else
					dataApprovalService.createDataApprovalAdmin(requesterEmployeeId, RequestCategoryType.Overtime, ovtRequest.getId(), requestCategoryType.getName(), claimForEmployeeId, null,Optional.empty(),Optional.empty());

			}
	
		} else {
			throw new CustomGenericException("Something went wrong,please contact Admin - Employee Detail Not Found");
		}
		return requestNo;
	}

	@Transactional
	public Page<OvtRequestDTO> findNeedApprovalOvertimeList(String approverEmployeeId, String status,
			HttpServletRequest request, String requestDateStart, String requestDateEnd, String ppl,
			PageRequest pageable, Jwt jwt) {
		String http = env.getProperty("talents.protocol");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date requestDateStartDate = null;
		Date requestDateEndDate = null;
		if (requestDateStart != null) {
			try {
				requestDateStartDate = sdf.parse(requestDateStart);
				requestDateEndDate = sdf.parse(requestDateEnd);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Page<OvtRequestDTO> resultOpt = ovtRequestRepository.findNeedApprovalBy("%" + approverEmployeeId + "%", status,
				Utils.getServerName(http, request, jwt, secret), requestDateStartDate, requestDateEndDate, "%" + ppl + "%",
				pageable,secret);
		return resultOpt;
	}

	@Transactional
	public Page<OvtRequestDTO> findOvertimeByEmployeeAndModule(String employmentRequest, String status,
			HttpServletRequest request, String requestDateStart, String requestDateEnd, PageRequest pageable,String requestNo, Jwt jwt) {
		String http = env.getProperty("talents.protocol");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date requestDateStartDate = null;
		Date requestDateEndDate = null;
		if (requestDateStart != null) {
			try {
				requestDateStartDate = sdf.parse(requestDateStart);
				requestDateEndDate = sdf.parse(requestDateEnd);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Page<OvtRequestDTO> findByEmployeeAndStatus = ovtRequestRepository.findByEmployeeAndStatus(employmentRequest,
				status, Utils.getServerName(http, request, jwt, secret), requestDateStartDate, requestDateEndDate, pageable,'%'+requestNo+'%',secret);
		return findByEmployeeAndStatus;
	}

	@Transactional
	public Optional<OvtRequestDTO> findOvertimeByEmployeeAndId(String empRequest, String dataApprovalId,
			HttpServletRequest request, Jwt jwt) {
		String http = env.getProperty("talents.protocol");
		Optional<OvtRequestDTO> resultOpt = ovtRequestRepository.findByEmployeeAndId(empRequest, dataApprovalId,
				Utils.getServerName(http, request, jwt, secret),secret);
		return resultOpt;
	}

	@Transactional
	public Optional<OvtRequestDTO> findNeedApprovalOvertimeById(String approverEmployeeId, String dataApprovalId,
			HttpServletRequest request, Jwt jwt) {
		String http = env.getProperty("talents.protocol");
		Optional<OvtRequestDTO> resultOpt = ovtRequestRepository.findNeedApprovalById("%" + approverEmployeeId + "%",
				dataApprovalId, Utils.getServerName(http, request, jwt, secret),secret);
		return resultOpt;
	}
	

	public Page<OvertimeHistoryApprovalDTO> findHistoryApprovalOvertime(String approverEmployeeId, String status,
			HttpServletRequest request, String requestDateStart, String requestDateEnd, String ppl,
			PageRequest pageable, Jwt jwt) {
		String http = env.getProperty("talents.protocol");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date requestDateStartDate = null;
		Date requestDateEndDate = null;
		if (requestDateStart != null) {
			try {
				requestDateStartDate = sdf.parse(requestDateStart);
				requestDateEndDate = sdf.parse(requestDateEnd);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Page<OvertimeHistoryApprovalDTO> resultOpt = ovtRequestRepository.findHistoryApproval(approverEmployeeId,
				status, Utils.getServerName(http, request, jwt, secret), requestDateStartDate, requestDateEndDate, "%" + ppl + "%",
				pageable,secret);
		return resultOpt;
	}
	


	public Optional<OvertimeHistoryApprovalDTO> findHistoryApprovalByIdAndEmployeeOvertime(String approverEmployeeId,
																						   String dataApprovalDetailId, HttpServletRequest request, Jwt jwt) {
		String http = env.getProperty("talents.protocol");
		Optional<OvertimeHistoryApprovalDTO> resultOpt = ovtRequestRepository.findHistoryApprovalByIdAndEmployee(
				approverEmployeeId, dataApprovalDetailId, Utils.getServerName(http, request, jwt, secret),secret);
		return resultOpt;
	}
	

	public List<OvertimeHistoryApprovalDTO> findHistoryApprovalByApprovalIdOvertime(String dataApprovalId,
			HttpServletRequest request, Jwt jwt) {
		String http = env.getProperty("talents.protocol");
		Sort sort = Sort.by(Direction.DESC, "actionDate");
		List<OvertimeHistoryApprovalDTO> resultOpt = ovtRequestRepository
				.findHistoryApprovalByDataApprovalId(dataApprovalId, Utils.getServerName(http, request, jwt, secret), sort,secret);
		return resultOpt;
	}
	
	
}
