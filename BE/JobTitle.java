package com.phincon.talents.app.model.hr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phincon.talents.app.model.AbstractEntity;
import com.phincon.talents.app.model.AbstractEntityUUID;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "hr_job_title")
public class JobTitle extends AbstractEntityUUID {

	@Column(name = "name", length = 255)
	private String name;
	
	@Column(name = "description", length = 255)
	private String description;

	@Temporal(TemporalType.DATE)
	@Column(name = "start_date")
	private Date startDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "end_date")
	private Date endDate;

	@Column(name = "flag_overtime")
	private Integer flagOvertime;

	private String jobFamilyId;

	private Integer contractMonth;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(insertable=false,updatable=false,name="jobFamilyId")
	@JsonIgnore
	private JobFamily jobFamily;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Integer getContractMonth() {
		return contractMonth;
	}

	public void setContractMonth(Integer contractMonth) {
		this.contractMonth = contractMonth;
	}

	public JobFamily getJobFamily() {
		return jobFamily;
	}

	public void setJobFamily(JobFamily jobFamily) {
		this.jobFamily = jobFamily;
	}

	public String getJobFamilyId() {
		return jobFamilyId;
	}

	public void setJobFamilyId(String jobFamilyId) {
		this.jobFamilyId = jobFamilyId;
	}

	public Integer getFlagOvertime(){
		return flagOvertime;
	}
	public void setFlagOvertime(Integer flagOvertime){
		this.flagOvertime = flagOvertime;
	}
}
