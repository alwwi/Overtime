package com.phincon.talents.app.dto;

import com.phincon.talents.app.model.AttachmentDataApproval;
import com.phincon.talents.app.model.DataApproval;
import com.phincon.talents.app.model.hr.OvtRequest;
import com.phincon.talents.app.model.hr.RequestCategoryType;
import com.phincon.talents.app.model.hr.VwEmpAssignment;
import com.phincon.talents.app.utils.JwtWrapperService;

import java.sql.Time;
import java.util.Date;

public class OvtRequestDTO {
	// requestCategory
	private String categoryName;

	// ovtRequest
	private String ovtRequestId;
	private Date requestDate;
	private Date startDate;
	private Date endDate;
	private String requestNo;
	private Time totalHoursGross;
	private String remark;

	// dataApproval
	private String dataApprovalId;
	private String status;
	private String progressOverall;
	private String approverName;
	// private String remark;
	// private String claimFor;
	private String attachments;
	private VwEmpAssignment vwEmpAssignment;
	private String employeeProfile;
	private String reasonReject;
	private VwEmpAssignment requestorAsgn;
	private Date expiredDateDetail;
	public OvtRequestDTO() {
		super();
	}

	public OvtRequestDTO(OvtRequest ovtRequest, RequestCategoryType requestCategoryType, DataApproval dataApproval,
			VwEmpAssignment vwEmpAssignment, AttachmentDataApproval attachmentDataApproval, String approverName,
			String serverNamePath, String secret) {
		super();
		// absenceType

		// absenceRequest
		this.ovtRequestId = ovtRequest.getId();
		this.remark = ovtRequest.getRemark();
		this.categoryName = requestCategoryType.getName();
		this.requestNo = ovtRequest.getRequestNo();
		this.totalHoursGross = ovtRequest.getTotalHoursGross();
		this.requestDate = ovtRequest.getRequestDate();
		this.startDate = ovtRequest.getStartDate();
		this.endDate = ovtRequest.getEndDate();
		// data approval
		this.status = dataApproval.getStatus();
		this.progressOverall = dataApproval.getCurrentApprovalLevel() + " of " + dataApproval.getApprovalLevel();
		this.approverName = approverName;
		this.expiredDateDetail = dataApproval.getExpiredDateDetail();
		this.reasonReject = dataApproval.getReasonReject();

		if (attachmentDataApproval != null) {
			this.attachments = serverNamePath + attachmentDataApproval.getPath()+"&token=" + JwtWrapperService.createPathJwt(attachmentDataApproval.getPath(),secret);
		}
		this.vwEmpAssignment = vwEmpAssignment;
		this.dataApprovalId = dataApproval.getId();
		this.employeeProfile = vwEmpAssignment.getPhotoProfile();

		if (this.vwEmpAssignment != null) {
			this.employeeProfile = serverNamePath + vwEmpAssignment.getPhotoProfile() +"&token=" + JwtWrapperService.createPathJwt(vwEmpAssignment.getPhotoProfile(),secret);
		}
	}

	public OvtRequestDTO(OvtRequest ovtRequest, RequestCategoryType requestCategoryType, DataApproval dataApproval,
						 VwEmpAssignment vwEmpAssignment, AttachmentDataApproval attachmentDataApproval, String approverName,
						 String serverNamePath, VwEmpAssignment empRqst, String secret) {
		this(ovtRequest, requestCategoryType, dataApproval, vwEmpAssignment, attachmentDataApproval, approverName, serverNamePath, secret);
		this.requestorAsgn = empRqst;
	}

	public Date getExpiredDateDetail() {
		return expiredDateDetail;
	}

	public void setExpiredDateDetail(Date expiredDateDetail) {
		this.expiredDateDetail = expiredDateDetail;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getOvtRequestId() {
		return ovtRequestId;
	}

	public void setOvtRequestId(String ovtRequestId) {
		this.ovtRequestId = ovtRequestId;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	public Time getTotalHoursGross() {
		return totalHoursGross;
	}

	public void setTotalHoursGross(Time totalHoursGross) {
		this.totalHoursGross = totalHoursGross;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDataApprovalId() {
		return dataApprovalId;
	}

	public void setDataApprovalId(String dataApprovalId) {
		this.dataApprovalId = dataApprovalId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProgressOverall() {
		return progressOverall;
	}

	public void setProgressOverall(String progressOverall) {
		this.progressOverall = progressOverall;
	}

	public String getApproverName() {
		return approverName;
	}

	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	public VwEmpAssignment getVwEmpAssignment() {
		return vwEmpAssignment;
	}

	public void setVwEmpAssignment(VwEmpAssignment vwEmpAssignment) {
		this.vwEmpAssignment = vwEmpAssignment;
	}

	public String getEmployeeProfile() {
		return employeeProfile;
	}

	public void setEmployeeProfile(String employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	public String getReasonReject() {
		return reasonReject;
	}

	public void setReasonReject(String reasonReject) {
		this.reasonReject = reasonReject;
	}

	public VwEmpAssignment getRequestorAsgn() {
		return requestorAsgn;
	}

	public void setRequestorAsgn(VwEmpAssignment requestorAsgn) {
		this.requestorAsgn = requestorAsgn;
	}
}
