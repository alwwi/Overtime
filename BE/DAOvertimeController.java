package com.phincon.talents.app.controllers.api.user.dataapproval;


import com.phincon.talents.app.dao.UserRepository;
import com.phincon.talents.app.dto.OvertimeHistoryApprovalDTO;
import com.phincon.talents.app.dto.OvtRequestDTO;
import com.phincon.talents.app.dto.SubmitOvertimeDTO;
import com.phincon.talents.app.model.generalnew.SysUser;
import com.phincon.talents.app.services.dataapproval.OvertimeService;
import com.phincon.talents.app.utils.CustomMessageTest;
import com.phincon.talents.app.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.phincon.talents.app.dao.AddressRequestRepository;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Arrays;


@RestController
@RequestMapping("api")
public class DAOvertimeController {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	OvertimeService overtimeService;
	
	@Autowired
    AddressRequestRepository addressRequestRepository;

	@Autowired
	Environment env;

	@Value("${mtf.sc.jwt.secret}")
	private String secret;
	
	@RequestMapping(value = "/user/dataApproval/overtime/submit", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<CustomMessageTest> submitDataApprovalOvertime(@RequestBody SubmitOvertimeDTO request,
			@AuthenticationPrincipal Jwt jwt) {

		SysUser user = userRepository.findByUsernameCaseInsensitiveNew(jwt.getClaimAsString("username"));
		
		//String insertRequest = "wow";
		
		String insertRequest = overtimeService.InsertOvertimeRequest(request, user.getEmployeeId(), user.getEmployeeId());

		String replaceString = "";
		String listIdApprover = "";
		String listDeviceString = "";
		//fuguh
		// start perubahan
		String requestIdReq = addressRequestRepository.findIdUserRequestOvertime(insertRequest);
        String listApprover = addressRequestRepository.findUserApprover(user.getEmployeeId(),requestIdReq);

		if(listApprover != null) {
			replaceString = listApprover.replaceAll("##", ",");
			listIdApprover = replaceString.replaceAll("#", "");

        // Optional<User> listDeviceApprover = addressRequestRepository.findDeviceIdApprover(listApprover);

        List<String> myList = new ArrayList<String>(Arrays.asList(listIdApprover.split(",")));
        int size = myList.size();

        List deviceIdList = new ArrayList();

        String indexssss = "";
        for (int i = 0; i < size; i++) {
		
//            indexssss = myList.get(2);
            String listLong = myList.get(i);
			String listMentahDeviceId = addressRequestRepository.findDeviceIdApprover(listLong);

            deviceIdList.add(listMentahDeviceId);

          }

			listDeviceString = deviceIdList.toString();
		}
		//end perubahan
		return new ResponseEntity<>(
				new CustomMessageTest("Submit Overtime Request Sucessfully (Request No : "+insertRequest+")", false, listDeviceString), HttpStatus.OK);

	}
	
	@RequestMapping(value = "/user/dataApproval/overtime/needapproval", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Page<OvtRequestDTO>> needApprovalOvertime(
			@RequestParam(value = "status", required = false,defaultValue ="In Progress") String status,
			@RequestParam(value = "requestDateStart", required = false) String requestDateStart,
			@RequestParam(value = "requestDateEnd", required = false) String requestDateEnd,
			@RequestParam(value = "ppl",defaultValue="" ,required = false) String ppl,
			@RequestParam(value = "page", required = false,defaultValue="0") Integer page,
			@RequestParam(value = "size", required = false,defaultValue="15") Integer size, @AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request) {

		SysUser user = userRepository.findByUsernameCaseInsensitiveNew(jwt.getClaimAsString("username"));
	
		Sort sorting = Sort.by(Direction.ASC, "requestDate");
		PageRequest pageRequest = PageRequest.of(page, size,sorting);
		Page<OvtRequestDTO> findByEmployeeAndModule = overtimeService.findNeedApprovalOvertimeList(user.getEmployeeId(),status,request,requestDateStart,requestDateEnd,ppl,pageRequest,jwt);
		
		return new ResponseEntity<>(findByEmployeeAndModule, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/user/dataApproval/overtime/myrequest", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Page<OvtRequestDTO>> myRequestOvertime(
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "requestDateStart", required = false) String requestDateStart,
			@RequestParam(value = "requestDateEnd", required = false) String requestDateEnd,
			@RequestParam(value = "page", required = false,defaultValue="0") Integer page,
			@RequestParam(value = "size", required = false,defaultValue="15") Integer size,@RequestParam(value = "requestNo", required = false,defaultValue="") String requestNo, @AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request) {

		SysUser user = userRepository.findByUsernameCaseInsensitiveNew(jwt.getClaimAsString("username"));
	
		Sort sorting = Sort.by(Direction.DESC, "requestDate");
		PageRequest pageRequest = PageRequest.of(page, size,sorting);
		Page<OvtRequestDTO> findByEmployeeAndModule = overtimeService.findOvertimeByEmployeeAndModule(user.getEmployee().getEmployment().iterator().next().getId(),status,request,requestDateStart,requestDateEnd,pageRequest,requestNo,jwt);
		String http = env.getProperty("talents.protocol");

		return new ResponseEntity<>(findByEmployeeAndModule, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/user/dataApproval/overtime/myrequest/{dataApprovalId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<OvtRequestDTO> myRequestOvertimeById(@PathVariable("dataApprovalId") String dataApprovalId,
			//@RequestParam(value = "status", required = false,defaultValue ="In Progress") String status,
			@AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request) {

		SysUser user = userRepository.findByUsernameCaseInsensitiveNew(jwt.getClaimAsString("username"));
	
		//Sort sorting = Sort.by(Direction.DESC, "requestDate");
		Optional<OvtRequestDTO> resultOpt = overtimeService.findOvertimeByEmployeeAndId(user.getEmployee().getEmployment().iterator().next().getId(),dataApprovalId,request,jwt);
		if(resultOpt.isPresent())
		{
			return new ResponseEntity<>(resultOpt.get(), HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}
	
	@RequestMapping(value = "/user/dataApproval/overtime/needapproval/{dataApprovalId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<OvtRequestDTO> needApprovalOvertime(@PathVariable("dataApprovalId") String dataApprovalId,
			 @AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request) {

		SysUser user = userRepository.findByUsernameCaseInsensitiveNew(jwt.getClaimAsString("username"));
	

		Optional<OvtRequestDTO> resultOpt = overtimeService.findNeedApprovalOvertimeById(user.getEmployeeId(),dataApprovalId,request,jwt);
		if(resultOpt.isPresent())
		{
			String http = env.getProperty("talents.protocol");
			if(resultOpt.get().getVwEmpAssignment() != null && resultOpt.get().getVwEmpAssignment().getPhotoProfile() != null) {
				resultOpt.get().getVwEmpAssignment().setToken(Utils.createTokenJwt(jwt, secret, resultOpt.get().getVwEmpAssignment().getPhotoProfile()));
				String url = Utils.getUrlAttachment(http, request, resultOpt.get().getVwEmpAssignment().getPhotoProfile(), jwt, secret);
				resultOpt.get().setEmployeeProfile(url);
				resultOpt.get().getVwEmpAssignment().setEmployeeProfile(url);
			}

//			if(resultOpt.get().getAttachments() != null){
//				String urlAttachment = Utils.getUrlAttachment(http, request, resultOpt.get().getAttachments(), jwt, secret);
//				resultOpt.get().setAttachments(urlAttachment);
//			}

			return new ResponseEntity<>(resultOpt.get(), HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

	}
	
	@RequestMapping(value = "/user/dataApproval/overtime/historyApproval", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Page<OvertimeHistoryApprovalDTO>> getHistoryApprovalOvertime(
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "requestDateStart", required = false) String requestDateStart,
			@RequestParam(value = "requestDateEnd", required = false) String requestDateEnd,
			@RequestParam(value = "ppl",defaultValue="" ,required = false) String ppl,
			@RequestParam(value = "page", required = false,defaultValue="0") Integer page,
			@RequestParam(value = "size", required = false,defaultValue="15") Integer size, @AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request) {

		SysUser user = userRepository.findByUsernameCaseInsensitiveNew(jwt.getClaimAsString("username"));
	
		Sort sorting = Sort.by(Direction.ASC, "br.requestDate");
		PageRequest pageRequest = PageRequest.of(page, size,sorting);
		Page<OvertimeHistoryApprovalDTO> findByEmployeeAndModule = overtimeService.findHistoryApprovalOvertime(user.getEmployeeId(),status,request,requestDateStart,requestDateEnd,ppl,pageRequest,jwt);
		return new ResponseEntity<>(findByEmployeeAndModule, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/user/dataApproval/overtime/historyApproval/{dataApprovalDetailId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<OvertimeHistoryApprovalDTO> getHistoryApprovalOvertimeDetail(
			@PathVariable("dataApprovalDetailId") String dataApprovalDetailId, @AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request) {

		SysUser user = userRepository.findByUsernameCaseInsensitiveNew(jwt.getClaimAsString("username"));
	
		Optional<OvertimeHistoryApprovalDTO> findByEmployeeAndModule = overtimeService.findHistoryApprovalByIdAndEmployeeOvertime(user.getEmployeeId(),dataApprovalDetailId,request,jwt);
		if(findByEmployeeAndModule.isPresent())
		{
			return new ResponseEntity<>(findByEmployeeAndModule.get(), HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

	}
	
	@RequestMapping(value = "/user/dataApproval/overtime/historyApprovalById/{dataApprovalId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<OvertimeHistoryApprovalDTO>> getHistoryApprovalOvertime(
			@PathVariable("dataApprovalId") String dataApprovalId, //@AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request,@AuthenticationPrincipal Jwt jwt) {

		List<OvertimeHistoryApprovalDTO> findByEmployeeAndModule = overtimeService.findHistoryApprovalByApprovalIdOvertime(dataApprovalId,request,jwt);
		return new ResponseEntity<>(findByEmployeeAndModule, HttpStatus.OK);
	}
	
}
