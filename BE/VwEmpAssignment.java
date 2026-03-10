package com.phincon.talents.app.model.hr;

import com.phincon.talents.app.security.ConfidentialConverter.ConfidentialValueStringConverter;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "vw_employee_assignment")
public class VwEmpAssignment implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "employee_id")
	private String employeeId;
	
	@Column(name="timezone")
	private Integer timezone;
	
	@Column(name = "name", length = 255)
	private String name;
	
	@Column(name = "gender", length = 50)
	private String gender;
	
	@Column(name = "marital_status", length = 50)
	private String maritalStatus;
	
	@Column(name = "employee_no", length = 100)
	private String employeeNo;
	
	@Column(name = "employment_id")
	private String employmentId;
	
	@Column(name = "assignment_id")
	private String assignmentId;
	
	@Column(name = "employee_status", length=100)
	private String employeeStatus;
	

	@Column(name = "direct_employee_id")
	private String directEmployeeId;
	
	@Column(name = "position_name", length = 255)
	private String positionName;
	
	@Column(name = "position_id")
	private String positionId;


	// fixing assignment  start    
	@Column(name = "location_id")
	private String locationId;
	// fixing assignment end 
	
	@Column(name = "grade_name", length = 100)
	@Convert(converter = ConfidentialValueStringConverter.class)
	private String gradeName;

	@Column(name = "grade_id")
	private String gradeId;
	
	@Column(name = "legal_entity_name", length = 200)
	private String legalEntityName;
	
	@Column(name = "work_location_name", length = 100)
	private String workLocationName;

	@Column(name = "photo_profile", length = 200)
	private String photoProfile;
	
	@Column(name = "work_location_type",length=100)
	private String workLocationType;
	
	@Column(name = "position_level_id")
	private String positionLevelId;
	
	@Column(name = "position_level_name",length=255)
	private String positionLevelName;
	
	@Column(name = "regional_id")
	private String regionalId;
	
	@Column(name = "regional_name",length=100)
	private String regionalName;

	@Column(name = "device_id",length=100)
	private String deviceID;

	/** New One (Begin) - 15.08.2021 */
	@Column(name = "company_office_id")
	private String companyOfficeId;

	@Column(name = "organization_id")
	private String organizationId;

	@Column(name = "organization_group_id")
	private String organizationGroupId;

	@Column(name = "job_title_id")
	private String jobTitleId;
	/** New One (End) - 15.08.2021 */

	/** Enhancement EPP (Begin) */
	@Column(name = "attempt_regulation_checklist")
	private Boolean attemptRegulationChecklist;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_attempt_regulation")
	private Date lastAttemptRegulation;
	/** Enhancement EPP (End) */

	/** Enhancement Pakta Integritas (Begin) */
	@Column(name = "pakta_regulation_checklist")
	private Boolean paktaRegulationChecklist;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_attempt_pakta")
	private Date lastAttemptPakta;

	@Column(name = "job_title",length=100)
	private String jobTitle;
	/** Enhancement Pakta Integritas (End) */

	/** Enhancement Overtima (Flag Overtime) (Start) */
	@Column(name =  "flag_overtime")
	private Integer flagOvertime;
	/** Enhancement Overtima (Flag Overtime) (End) */

	@Transient
	private String token;

	@Transient
	private String employeeProfile;

	public String getEmployeeProfile() {
		return employeeProfile;
	}

	public void setEmployeeProfile(String employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getdeviceID() {
		return deviceID;
	}

	public void sedeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	
	
	public Integer getTimezone() {
		return timezone;
	}

	public String getRegionalId() {
		return regionalId;
	}

	public void setRegionalId(String regionalId) {
		this.regionalId = regionalId;
	}

	public String getRegionalName() {
		return regionalName;
	}

	public void setRegionalName(String regionalName) {
		this.regionalName = regionalName;
	}

	public void setTimezone(Integer timezone) {
		this.timezone = timezone;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getWorkLocationType() {
		return workLocationType;
	}

	public void setWorkLocationType(String workLocationType) {
		this.workLocationType = workLocationType;
	}

	public String getPositionLevelId() {
		return positionLevelId;
	}

	public void setPositionLevelId(String positionLevelId) {
		this.positionLevelId = positionLevelId;
	}

	// fixing start
	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	// fixing end


	public String getPositionLevelName() {
		return positionLevelName;
	}

	public void setPositionLevelName(String positionLevelName) {
		this.positionLevelName = positionLevelName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getEmploymentId() {
		return employmentId;
	}

	public void setEmploymentId(String employmentId) {
		this.employmentId = employmentId;
	}

//	public String getJobTitle() {
//		return jobTitle;
//	}
//
//	public void setJobTitle(String jobTitle) {
//		this.jobTitle = jobTitle;
//	}
//
//	public String getJobTitleName() {
//		return jobTitleName;
//	}
//
//	public void setJobTitleName(String jobTitleName) {
//		this.jobTitleName = jobTitleName;
//	}

	public String getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public String getDirectEmployeeId() {
		return directEmployeeId;
	}

	public void setDirectEmployeeId(String directEmployeeId) {
		this.directEmployeeId = directEmployeeId;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getPositionId() {
		return positionId;
	}

	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}

//	public String getOrganizationName() {
//		return organizationName;
//	}
//
//	public void setOrganizationName(String organizationName) {
//		this.organizationName = organizationName;
//	}
//
//	public String getOrganizationId() {
//		return organizationId;
//	}
//
//	public void setOrganizationId(String organizationId) {
//		this.organizationId = organizationId;
//	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public String getGradeId() {
		return gradeId;
	}

	public void setGradeId(String gradeId) {
		this.gradeId = gradeId;
	}

	public String getLegalEntityName() {
		return legalEntityName;
	}

	public void setLegalEntityName(String legalEntityName) {
		this.legalEntityName = legalEntityName;
	}

	public String getWorkLocationName() {
		return workLocationName;
	}

	public void setWorkLocationName(String workLocationName) {
		this.workLocationName = workLocationName;
	}

	public String getPhotoProfile() {
		return photoProfile;
	}

	public void setPhotoProfile(String photoProfile) {
		this.photoProfile = photoProfile;
	}

	/** Adding new one (Begin) - 15.08.2021 */
	public String getCompanyOfficeId() {
		return companyOfficeId;
	}

	public void setCompanyOfficeId(String companyOfficeId) {
		this.companyOfficeId = companyOfficeId;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganizationGroupId() {
		return organizationGroupId;
	}

	public void setOrganizationGroupId(String organizationGroupId) {
		this.organizationGroupId = organizationGroupId;
	}

	public String getJobTitleId() {
		return jobTitleId;
	}

	public void setJobTitleId(String jobTitleId) {
		this.jobTitleId = jobTitleId;
	}
	/** Adding new one (End) - 15.08.2021 */

	/** Enhancement EPP (Begin) */
	public Boolean getAttemptRegulationChecklist() {
		return attemptRegulationChecklist;
	}

	public void setAttemptRegulationChecklist(Boolean attemptRegulationChecklist) {
		this.attemptRegulationChecklist = attemptRegulationChecklist;
	}

	public Date getLastAttemptRegulation() {
		return lastAttemptRegulation;
	}

	public void setLastAttemptRegulation(Date lastAttemptRegulation) {
		this.lastAttemptRegulation = lastAttemptRegulation;
	}

	public Boolean getPaktaRegulationChecklist() {
		return this.paktaRegulationChecklist;
	}

	public void setPaktaRegulationChecklist(Boolean paktaRegulationChecklist) {
		this.paktaRegulationChecklist = paktaRegulationChecklist;
	}

	public Date getLastAttemptPakta() {
		return this.lastAttemptPakta;
	}

	public void setLastAttemptPakta(Date lastAttemptPakta) {
		this.lastAttemptPakta = lastAttemptPakta;
	}

	public String getJobTitle() {
		return this.jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}


	/** Enhancement EPP (End) */

	/** Enhancement Overtima (Flag Overtime) (Start) */
	public Integer getFlagOvertime(){
		return flagOvertime;
	}

	public void setFlagOvertime(Integer flagOvertime){
		this.flagOvertime = flagOvertime
	}
	/** Enhancement Overtima (Flag Overtime) (End) */
}
