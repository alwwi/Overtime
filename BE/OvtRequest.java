package com.phincon.talents.app.model.hr;

import java.sql.Time;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import com.phincon.talents.app.model.AbstractEntity;
import com.phincon.talents.app.model.AbstractEntityUUID;

@Entity
@Table(name = "hr_ovt_request")
public class OvtRequest extends AbstractEntityUUID {

	@Column(name="employment_id")
	private String employmentId;
	
	@Column(name="category_id")
	private String categoryId;
	
	
	
	@Temporal(TemporalType.TIMESTAMP)	
	@Column(name = "request_date")
	private Date requestDate;
	
	@Temporal(TemporalType.TIMESTAMP)	
	@Column(name = "start_date")
	private Date startDate;
	
	@Column(name = "total_hours_gross")
	private Time totalHoursGross;

	@Temporal(TemporalType.TIMESTAMP)	
	@Column(name = "end_date")
	private Date endDate;
	
	@Column(name = "remark", length = 1000)
	private String remark;
	
	@Column(name = "request_no", length = 100)
	private String requestNo;

	
	public Time getTotalHoursGross() {
		return totalHoursGross;
	}

	public void setTotalHoursGross(Time totalHoursGross) {
		this.totalHoursGross = totalHoursGross;
	}

	public String getEmploymentId() {
		return employmentId;
	}

	public void setEmploymentId(String employmentId) {
		this.employmentId = employmentId;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}

	
}
