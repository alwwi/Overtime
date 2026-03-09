package com.phincon.talents.app.controllers.api.admin.dataapproval;

import com.phincon.talents.app.dao.OvtRequestRepository;
import com.phincon.talents.app.dto.OvtRequestDTO;
import com.phincon.talents.app.dto.SubmitOvertimeDTO;
import com.phincon.talents.app.model.User;
import com.phincon.talents.app.model.generalnew.SysUser;
import com.phincon.talents.app.services.dataapproval.OvertimeService;
import com.phincon.talents.app.utils.CustomMessage;
import com.phincon.talents.app.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("api")
public class AdDAOvertimeController extends AdDAParentController {

    @Autowired
    OvertimeService overtimeService;

    @Autowired
    OvtRequestRepository ovtRequestRepository;

    @Value("${mtf.sc.jwt.secret}")
    private String secret;

    @RequestMapping(value = "/admin/dataApproval/overtime/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<CustomMessage> submitDataApprovalOvertime(@RequestBody SubmitOvertimeDTO request,
                                                                    @AuthenticationPrincipal Jwt jwt) {

        SysUser user = userRepository.findByUsernameCaseInsensitiveNew(jwt.getClaimAsString("username"));
        this.getEmployeeRequestee(request.getEmploymentRequestee(), user.getEmployeeId());
        User userForSubmit = null;
        if(request.getEmploymentRequestee()!= null)
        {
        	userForSubmit = userRepository.findByEmployeeId(employmentRepository.findById(request.getEmploymentRequestee()).get().getEmployeeId());
        }
        String insertRequest = overtimeService.InsertOvertimeRequestAdmin(request, this.requesteeEmplid, user.getEmployeeId(),true);

        return new ResponseEntity<>(
                new CustomMessage("Submit Overtime Request Sucessfully (Request No : "+insertRequest+")", false), HttpStatus.OK);

    }

    @RequestMapping(value = "/admin/dataApproval/overtime/myrequest", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Page<OvtRequestDTO>> myRequestOvertime(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "requestDateStart", required = false) String requestDateStart,
            @RequestParam(value = "requestDateEnd", required = false) String requestDateEnd,
            @RequestParam(value = "ppl",defaultValue="" ,required = false) String ppl,
            @RequestParam(value = "page", required = false,defaultValue="0") Integer page,
            @RequestParam(value = "size", required = false,defaultValue="15") Integer size,
            HttpServletRequest request,@AuthenticationPrincipal Jwt jwt) {

        String http = env.getProperty("talents.protocol");
        Sort sorting = Sort.by(Sort.Direction.DESC, "requestDate");
        PageRequest pageRequest = PageRequest.of(page, size,sorting);

        Date requestDateStartDate = null;
        Date requestDateEndDate = null;
        if (requestDateStart != null) {
            requestDateStartDate = this.parseDate(requestDateStart);
            requestDateEndDate = this.parseDate(requestDateEnd);
        }

        Page<OvtRequestDTO> findByEmployeeAndStatus = ovtRequestRepository.findByEmployeeAndStatusAdmin(status, Utils.getServerName(http, request, jwt, secret),
                requestDateStartDate, requestDateEndDate, "%" + ppl + "%", pageRequest,secret);

        return new ResponseEntity<>(findByEmployeeAndStatus, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/dataApproval/overtime/myrequest/{dataApprovalId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<OvtRequestDTO> myRequestAbsenceById(@PathVariable("dataApprovalId") String dataApprovalId,
                                                              HttpServletRequest request,@AuthenticationPrincipal Jwt jwt) {

        String http = env.getProperty("talents.protocol");
        Optional<OvtRequestDTO> resultOpt = ovtRequestRepository.findByEmployeeAndIdAdmin(dataApprovalId, Utils.getServerName(http, request, jwt, secret),secret);
        if(resultOpt.isPresent()) {
            if(resultOpt.get().getVwEmpAssignment() != null && resultOpt.get().getVwEmpAssignment().getPhotoProfile() != null) {
                resultOpt.get().getVwEmpAssignment().setToken(Utils.createTokenJwt(jwt, secret, resultOpt.get().getVwEmpAssignment().getPhotoProfile()));
                String url = Utils.getUrlAttachment(http, request, resultOpt.get().getVwEmpAssignment().getPhotoProfile(), jwt, secret);
//                resultOpt.get().setEmployeeProfile(url);
                resultOpt.get().getVwEmpAssignment().setEmployeeProfile(url);
            }

            if(resultOpt.get().getRequestorAsgn() != null && resultOpt.get().getRequestorAsgn().getPhotoProfile() != null) {
                resultOpt.get().getRequestorAsgn().setToken(Utils.createTokenJwt(jwt, secret, resultOpt.get().getRequestorAsgn().getPhotoProfile()));
                String url = Utils.getUrlAttachment(http, request, resultOpt.get().getRequestorAsgn().getPhotoProfile(), jwt, secret);
                resultOpt.get().getRequestorAsgn().setEmployeeProfile(url);
            }

//            if(resultOpt.get().getAttachments() != null){
//                String urlAttachment = Utils.getUrlAttachment(http, request, resultOpt.get().getAttachments(), jwt, secret);
//                resultOpt.get().setAttachments(urlAttachment);
//            }

            return new ResponseEntity<>(resultOpt.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
