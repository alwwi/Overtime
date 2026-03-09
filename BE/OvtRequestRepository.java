package com.phincon.talents.app.dao;

import com.phincon.talents.app.dto.OvertimeHistoryApprovalDTO;
import com.phincon.talents.app.dto.OvtRequestDTO;
import com.phincon.talents.app.model.hr.OvtRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OvtRequestRepository extends CrudRepository<OvtRequest,String>, PagingAndSortingRepository<OvtRequest,String> {
	@Query("select new com.phincon.talents.app.dto.OvtRequestDTO(" +
			"ar,rct,da,vea,ada,f_get_empname (da.currentAssignApprover)," +
			"concat('',:serverPath), :secret) " +
			"from OvtRequest ar join DataApproval da on ar.id = da.objectRef and da.module='Overtime' join VwEmpAssignment vea on ar.employmentId = vea.employmentId left join AttachmentDataApproval ada on ada.dataApproval = da.id join RequestCategoryType rct on rct.id = ar.categoryId  " +
			"where current_date() <= coalesce(da.expiredDate,current_date()) AND :serverPath = :serverPath and da.status = :status and da.currentAssignApprover like :approverEmployeeId and (Date(ar.requestDate) between :requestDateStart and :requestDateEnd or :requestDateStart is null or :requestDateEnd is null) and (vea.name like :ppl or vea.employeeNo like :ppl or ar.requestNo like :ppl)")
	Page<OvtRequestDTO> findNeedApprovalBy(@Param("approverEmployeeId") String approverEmployeeId,@Param("status") String status,@Param("serverPath") String serverPath,@Param("requestDateStart") Date requestDateStart,@Param("requestDateEnd") Date requestDateEnd,@Param("ppl") String ppl,Pageable pageable,@Param("secret") String secret);

	@Query("select new com.phincon.talents.app.dto.OvtRequestDTO(ar,rct,da,vea,ada,f_get_empname (da.currentAssignApprover),concat('',:serverPath), :secret) from OvtRequest ar join DataApproval da on ar.id = da.objectRef and da.module='Overtime' join VwEmpAssignment vea on ar.employmentId = vea.employmentId left join AttachmentDataApproval ada on ada.dataApproval = da.id join RequestCategoryType rct on rct.id = ar.categoryId  where  ar.employmentId = :employmentId and :serverPath = :serverPath and (da.status = :status or :status is null ) and (Date(ar.requestDate) between :requestDateStart and :requestDateEnd or :requestDateStart is null or :requestDateEnd is null) and (:requestNo is null or ar.requestNo like :requestNo)")
	Page<OvtRequestDTO> findByEmployeeAndStatus(@Param("employmentId") String employmentId,@Param("status") String status,@Param("serverPath") String serverPath,@Param("requestDateStart") Date requestDateStart,@Param("requestDateEnd") Date requestDateEnd,Pageable pageable,@Param("requestNo") String requestNo,@Param("secret") String secret);

	@Query("select new com.phincon.talents.app.dto.OvtRequestDTO(ar,rct,da,vea,ada,f_get_empname (da.currentAssignApprover),concat('',:serverPath), :secret) from OvtRequest ar join DataApproval da on ar.id = da.objectRef and da.module='Overtime' and da.isByAdmin=true join VwEmpAssignment vea on ar.employmentId = vea.employmentId left join AttachmentDataApproval ada on ada.dataApproval = da.id join RequestCategoryType rct on rct.id = ar.categoryId LEFT JOIN VwEmpAssignment empRqstee on empRqstee.employeeId=da.employeeId where  :serverPath = :serverPath and (da.status = :status or :status is null ) and (Date(ar.requestDate) between :requestDateStart and :requestDateEnd or :requestDateStart is null or :requestDateEnd is null) and (empRqstee.name like :ppl or empRqstee.employeeNo like :ppl or ar.requestNo like :ppl)")
	Page<OvtRequestDTO> findByEmployeeAndStatusAdmin(@Param("status") String status,@Param("serverPath") String serverPath,@Param("requestDateStart") Date requestDateStart,@Param("requestDateEnd") Date requestDateEnd, @Param("ppl") String ppl, Pageable pageable,@Param("secret") String secret);

	@Query("select new com.phincon.talents.app.dto.OvtRequestDTO(ar,rct,da,vea,ada,f_get_empname (da.currentAssignApprover),concat('',:serverPath), :secret) from OvtRequest ar join DataApproval da on ar.id = da.objectRef and da.module='Overtime' join VwEmpAssignment vea on ar.employmentId = vea.employmentId left join AttachmentDataApproval ada on ada.dataApproval = da.id join RequestCategoryType rct on rct.id = ar.categoryId  where  ar.employmentId = :employmentId and :serverPath = :serverPath and da.id = :dataApprovalId")
	Optional<OvtRequestDTO> findByEmployeeAndId(@Param("employmentId") String employmentId,@Param("dataApprovalId") String dataApprovalId,@Param("serverPath") String serverPath,@Param("secret") String secret);

	@Query("select new com.phincon.talents.app.dto.OvtRequestDTO(ar,rct,da,vea,ada,f_get_empname (da.currentAssignApprover),concat('',:serverPath), empRqst, :secret) from OvtRequest ar join DataApproval da on ar.id = da.objectRef and da.module='Overtime' and da.isByAdmin=true join VwEmpAssignment vea on ar.employmentId = vea.employmentId left join AttachmentDataApproval ada on ada.dataApproval = da.id join RequestCategoryType rct on rct.id = ar.categoryId LEFT JOIN VwEmpAssignment empRqst on empRqst.employeeId=da.empRequest where  :serverPath = :serverPath and da.id = :dataApprovalId")
	Optional<OvtRequestDTO> findByEmployeeAndIdAdmin(@Param("dataApprovalId") String dataApprovalId,@Param("serverPath") String serverPath,@Param("secret") String secret);

	@Query("select new com.phincon.talents.app.dto.OvtRequestDTO(ar,rct,da,vea,ada,f_get_empname (da.currentAssignApprover),concat('',:serverPath), :secret) from OvtRequest ar join DataApproval da on ar.id = da.objectRef and da.module='Overtime' join VwEmpAssignment vea on ar.employmentId = vea.employmentId left join AttachmentDataApproval ada on ada.dataApproval = da.id join RequestCategoryType rct on rct.id = ar.categoryId  where  :serverPath = :serverPath and da.id = :dataApprovalId and da.currentAssignApprover like :approverEmployeeId")
	Optional<OvtRequestDTO> findNeedApprovalById(@Param("approverEmployeeId") String approverEmployeeId,@Param("dataApprovalId") String dataApprovalId,@Param("serverPath") String serverPath,@Param("secret") String secret);


	@Query("select new com.phincon.talents.app.dto.OvertimeHistoryApprovalDTO(br,rct,da,vea,ada,f_get_empname (da.currentAssignApprover),concat('',:serverPath),dad,veaApp, :secret) from DataApprovalDetail dad left join DataApproval da  on da.id = dad.dataApprovalId join  OvtRequest br  on br.id = da.objectRef and da.module='Overtime' join VwEmpAssignment vea on br.employmentId = vea.employmentId left join AttachmentDataApproval ada on ada.dataApproval = da.id join RequestCategoryType rct on rct.id = br.categoryId  join VwEmpAssignment veaApp on veaApp.employeeId = dad.actionByEmployeeId  where  :serverPath = :serverPath and (dad.action = :status or (:status is null and dad.action in ('Approved','Rejected') ) ) and dad.actionByEmployeeId = :approverEmployeeId and (Date(br.requestDate) between :requestDateStart and :requestDateEnd or :requestDateStart is null or :requestDateEnd is null) and (vea.name like :ppl or vea.employeeNo like :ppl or br.requestNo like :ppl)")
	Page<OvertimeHistoryApprovalDTO> findHistoryApproval(@Param("approverEmployeeId") String approverEmployeeId,@Param("status") String status,@Param("serverPath") String serverPath,@Param("requestDateStart") Date requestDateStart,@Param("requestDateEnd") Date requestDateEnd,@Param("ppl") String ppl,Pageable pageable,@Param("secret") String secret);
	
	@Query("select new com.phincon.talents.app.dto.OvertimeHistoryApprovalDTO(br,rct,da,vea,ada,f_get_empname (da.currentAssignApprover),concat('',:serverPath),dad,veaApp, :secret) from DataApprovalDetail dad left join DataApproval da  on da.id = dad.dataApprovalId join  OvtRequest br  on br.id = da.objectRef and da.module='Overtime' join VwEmpAssignment vea on br.employmentId = vea.employmentId left join AttachmentDataApproval ada on ada.dataApproval = da.id join RequestCategoryType rct on rct.id = br.categoryId  join VwEmpAssignment veaApp on veaApp.employeeId = dad.actionByEmployeeId  where  :serverPath = :serverPath and dad.actionByEmployeeId = :approverEmployeeId  and dad.id = :dataApprovalDetailId")
	Optional<OvertimeHistoryApprovalDTO> findHistoryApprovalByIdAndEmployee(@Param("approverEmployeeId") String approverEmployeeId,@Param("dataApprovalDetailId") String dataApprovalDetailId,@Param("serverPath") String serverPath,@Param("secret") String secret);

	@Query("select new com.phincon.talents.app.dto.OvertimeHistoryApprovalDTO(br,rct,da,vea,ada,f_get_empname (da.currentAssignApprover),concat('',:serverPath),dad,veaApp, :secret) from DataApprovalDetail dad left join DataApproval da  on da.id = dad.dataApprovalId join  OvtRequest br  on br.id = da.objectRef and da.module='Overtime' join VwEmpAssignment vea on br.employmentId = vea.employmentId left join AttachmentDataApproval ada on ada.dataApproval = da.id join RequestCategoryType rct on rct.id = br.categoryId  join VwEmpAssignment veaApp on veaApp.employeeId = dad.actionByEmployeeId  where  :serverPath = :serverPath and dad.action is not null and da.id = :dataApprovalId   ")
	List<OvertimeHistoryApprovalDTO> findHistoryApprovalByDataApprovalId(@Param("dataApprovalId") String dataApprovalId,@Param("serverPath") String serverPath,Sort sort,@Param("secret") String secret);

	@Query("select count(1)  from OvtRequest br join DataApproval da on br.id = da.objectRef and da.module ='Overtime' where br.employmentId =:employmentId and da.status in ('Approved','In Progress') and Date(:startDate) = Date(br.startDate) ")
	Integer findCountRequest(@Param("employmentId") String employmentId,@Param("startDate") Date startDate);
	
	@Query(nativeQuery=true,value="CALL p_get_shift_by_date(:startDate,:endDate,:employmentId)")
	List<Object[]>getShiftByDate(@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("employmentId")String employmentId);
}
