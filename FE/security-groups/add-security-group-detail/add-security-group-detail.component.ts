import { Component, OnInit } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { HttpService } from 'src/app/services/http/http.service';
import { MainService } from 'src/app/services/main/main.service';
import { SwalService } from 'src/app/services/swal/swal.service';

@Component({
  selector: 'app-add-security-group-detail',
  templateUrl: './add-security-group-detail.component.html',
  styleUrls: ['./add-security-group-detail.component.scss'],
})
export class AddSecurityGroupDetailComponent implements OnInit {

  id: any;
  secGroupId: any;
  type: any;
  secGroupDetail: any;

  listRole: any = [
    {
      title: "-- Select --",
      value: "",
    },
    {
      title: "Approver",
      value: "Approver",
    },
    {
      title: "Viewer",
      value: "Viewer",
    },
  ];

  listEmploymentStatus: any = [
    {
      title: "-- Select --",
      value: "",
    },
    {
      title: "Permanent",
      value: "Permanent",
    },
    {
      title: "Contract 1",
      value: "Contract 1",
    },
    {
      title: "Contract 2",
      value: "Contract 2",
    },
    {
      title: "Probation",
      value: "Probation",
    },
    {
      title: "Trainee",
      value: "Trainee",
    },
    {
      title: "Out Source Head Count",
      value: "Out Source Head Count",
    },
    {
      title: "Out Source Non Head Count",
      value: "Out Source Non Head Count",
    },
  ];

  listWorkLocationType: any = [
    {
      title: "-- Select --",
      value: "",
    },
    {
      title: "HO",
      value: "HO",
    },
    {
      title: "Branch",
      value: "Branch",
    },
    {
      title: "Fleet",
      value: "Fleet",
    },
    {
      title: "Regional",
      value: "Regional",
    },
    {
      title: "Satelite",
      value: "Satelite",
    },
    {
      title: "Pool",
      value: "Pool",
    },
    {
      title: "POS",
      value: "POS",
    }
  ];

  listAutority: any = [];
  talentsParameter: any;

  constructor(
    private modalCtrl: ModalController,
    public mainService: MainService,
    private swalService: SwalService,
    private httpService: HttpService
  ) { }

  ngOnInit() {
    this.secGroupDetail = {
      name: null,
      positionLevelName: null,
      positionLevelId: null,
      organizationName: null,
      organizationId: null,
      gradeName: null,
      gradeId: null,
      employeeName: null,
      employeeId: null,
      companyOfficeName: null,
      companyOfficeId: null,
      jobTitleName: null,
      jobTitleId: null,
    }
    if (this.id != null) {
      this.getData();
    }

    if (this.type == "Details") {
      this.getAuthorityList();
    }
  }

  close() {
    this.modalCtrl.dismiss();
  }

  getData() {
    let urlApi = this.mainService.getMainUrl() + '/api/admin/secGroups/' + this.secGroupId;
    if (this.type == "Details") {
      urlApi += "/detail/" + this.id;
    } else if (this.type == "Matrix") {
      urlApi += "/matrix/" + this.id;
    }

    this.httpService.getUrlApi(
      urlApi,
      false,
      (result: any, status: string) => {
        if (status === 'success') {
          this.secGroupDetail = result;
          if (this.type == "Details") {
            this.talentsParameter = result.authority;
          } else {
            this.secGroupDetail = result;
            this.secGroupDetail.positionLevelName = result.positionLevel != null ? result.positionLevel.name : null;
            this.secGroupDetail.organizationName = result.organization != null ? result.organization.name : null;
            this.secGroupDetail.gradeName = result.grade != null ? result.grade.name : null;
            this.secGroupDetail.employeeName = result.vwEmpAssignment != null ? result.vwEmpAssignment.name : null;
            this.secGroupDetail.worklocationName = result.worklocation != null ? result.worklocation.name : null;
            this.secGroupDetail.jobTitleName = result.jobTitle != null ? result.jobTitle.name : null;
            this.secGroupDetail.employmentStatus = result.employmnetStatus;
          }
        } else {
          this.modalCtrl.dismiss();
          this.httpService.handlingHttpError(result);
        }
      });
  }

  getAuthorityList() {
    let urlApi = this.mainService.getMainUrl() + '/api/talentsParameter?groupKey=AUTHORITY_LIST';

    this.httpService.getUrlApi(
      urlApi,
      false,
      (result: any, status: string) => {
        if (status === 'success') {
          if (result != null) {
            this.listAutority = result;
          }

        } else {
          this.modalCtrl.dismiss();
          this.httpService.handlingHttpError(result);
        }
      });
  }

  openSearchMenu(type: string) {
    let data = {
      searchType: type,
    }
    this.mainService.searchModal(data, (res: any) => {
      if (res.data != undefined) {
        if (data.searchType == "Position Level") {
          this.secGroupDetail.positionLevelName = res.data.name;
          this.secGroupDetail.positionLevelId = res.data.id;
        } else if (data.searchType == "Organization") {
          this.secGroupDetail.organizationName = res.data.name;
          this.secGroupDetail.organizationId = res.data.id;
        } else if (data.searchType == "Grade") {
          this.secGroupDetail.gradeName = res.data.name;
          this.secGroupDetail.gradeId = res.data.id;
        } else if (data.searchType == "Approver") {
          this.secGroupDetail.employeeName = res.data.name;
          this.secGroupDetail.employeeId = res.data.employeeId;
        } else if (data.searchType == "Work Location") {
          this.secGroupDetail.worklocationName = res.data.name;
          this.secGroupDetail.worklocationId = res.data.id;
        } else {
          this.secGroupDetail.jobTitleName = res.data.name;
          this.secGroupDetail.jobTitleId = res.data.id;
        }
      }
    });
  }

  onSubmit() {
    this.swalService.present("Are you sure to submit?", "question", (res: any) => {
      if (res) {
        let urlApi = this.mainService.getMainUrl();

        let dataPost: any = {};

        if (this.type == "Details") {
          urlApi += "/api/admin/secGroupsDetail";
          dataPost.authority = this.talentsParameter;
        } else if (this.type == "Matrix") {
          urlApi += "/api/admin/secGroupsMatrix";
          dataPost = this.secGroupDetail;
        }

        dataPost.groupId = this.secGroupId;

        if (this.id != null) {
          dataPost.id = this.id;
        }

        this.httpService.postUrlApi(
          urlApi,
          JSON.stringify(dataPost),
          true,
          null,
          (result: any, status: string) => {
            if (status === 'success') {
              this.handleSuccessSubmit(result);
            } else {
              this.httpService.handlingHttpError(result);
            }
          });
      }
    });
  }

  handleSuccessSubmit(data: any) {
    this.mainService.dismissLoading();
    this.modalCtrl.dismiss(data.message);
  }
}
