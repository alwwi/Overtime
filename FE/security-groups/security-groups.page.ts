import { Component, OnInit } from '@angular/core';
import { NavigationExtras } from '@angular/router';
import { NavController } from '@ionic/angular';
import { HttpService } from 'src/app/services/http/http.service';
import { MainService } from 'src/app/services/main/main.service';
import { SwalService } from 'src/app/services/swal/swal.service';

@Component({
  selector: 'app-security-groups',
  templateUrl: './security-groups.page.html',
  styleUrls: ['./security-groups.page.scss'],
})
export class SecurityGroupsPage {

  canRefresh: boolean = false;
  isSkeletonLoading: boolean = true;
  isEmpty: boolean = false;

  filterName: any;
  code: any;
  listData: any = [];

  constructor(
    public mainService: MainService,
    private swalService: SwalService,
    private httpService: HttpService,
    private navCtrl: NavController
  ) { }

  ionViewWillEnter() {
        this.getAbsenceType();
  }

  doRefresh(event: any) {
    this.isSkeletonLoading = true;
    this.getAbsenceType();
    setTimeout(() => {
      event.target.complete();
    }, 500);
  }

  filter() {
    let data = {
      type: "Approval Groups",
      filterName: this.filterName,
      code: this.code
    }

    this.mainService.filterModal(data, (res: any) => {
      if (res.data != undefined) {
        this.filterName = res.data.filterName;
        this.isSkeletonLoading = true;
        this.listData = [];
        this.getAbsenceType();
      }
    })
  }

  getAbsenceType() {
    let urlApi = this.mainService.getMainUrl() +
      "/api/admin/secGroups";

    this.httpService.getUrlApi(urlApi, false, (res: any, status: any) => {
      if (status == "success") {
        this.listData = res;
        if (this.filterName != null && this.filterName != "" && this.filterName != undefined) {
          this.listData = this.listData.filter((res: any) => res.groupName.toLowerCase().includes(this.filterName.toLowerCase()));
        }

        this.isEmpty = this.listData.length == 0;
      } else {
        this.isEmpty = true;
        this.httpService.handlingHttpError(res);
      }
      this.isSkeletonLoading = false;
    });
  }

  onRemove(data: any) {
    this.swalService.present("Are you sure to delete this Security Group?", "question", (res: any) => {
      if (res) {
        let urlApi = this.mainService.getMainUrl() +
          '/api/admin/secGroups/delete';

        let dataPost: any = {
          id: data.id
        };

        this.httpService.postUrlApi(
          urlApi,
          JSON.stringify(dataPost),
          true,
          null,
          (result: any, status: string) => {
            if (status === 'success') {
              this.successDelete(result);
            } else {
              this.httpService.handlingHttpError(result);
            }
          });
      }
    });
  }

  successDelete(res: any) {
    this.mainService.dismissLoading();
    this.swalService.present(res.message, "success", () => {
      this.isSkeletonLoading = true;
      this.listData = [];
      this.getAbsenceType();
    });
  }

  goToDetail(id: any) {
    let params: NavigationExtras = {
      queryParams: {
        id: this.mainService.encrypt(id),
      },
    };
    if (id == null) {
      this.navCtrl.navigateForward(["/administration/settings/security/security-groups/add-security-group"]);
    } else {
      this.navCtrl.navigateForward(["/administration/settings/security/security-groups/add-security-group"], params);
    }
  }
}
