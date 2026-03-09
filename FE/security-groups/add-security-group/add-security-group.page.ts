import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MainService } from 'src/app/services/main/main.service';
import { HttpService } from 'src/app/services/http/http.service';
import { SwalService } from 'src/app/services/swal/swal.service';
import { ModalController, NavController } from '@ionic/angular';
import { AddSecurityGroupDetailComponent } from '../add-security-group-detail/add-security-group-detail.component';

@Component({
  selector: 'app-add-security-group',
  templateUrl: './add-security-group.page.html',
  styleUrls: ['./add-security-group.page.scss'],
})
export class AddSecurityGroupPage {

  id: any;

  segmentValue: string = "General";
  listSegment: any = [
    {
      title: "General",
      value: "General",
    },
    {
      title: "Details",
      value: "Details",
    },
    {
      title: "Matrix",
      value: "Matrix",
    },
  ];

  isSkeletonLoading: boolean = false;
  listData: any = [];
  name: any;
  isEmpty: boolean = false;
  messageValidation: string = "";

  constructor(
    private activatedRoute: ActivatedRoute,
    public mainService: MainService,
    private httpService: HttpService,
    private swalService: SwalService,
    private navCtrl: NavController,
    private modalCtrl: ModalController
  ) { }

  ionViewWillEnter() {
        this.name = null;
        this.activatedRoute.queryParams.subscribe((params) => {
          this.id = this.mainService.decrypt(params["id"]);
        })
        if (this.id == null) {
          this.listSegment.splice(1, 2);
        }
        if (this.id != null) {
          this.getData();
        }
  }

  selectValue(event: any) {
    this.segmentValue = event.detail.value;
    this.isSkeletonLoading = false;
    this.isEmpty = false;
    this.listData = [];
    this.name = null;

    if (this.segmentValue == "General") {
      this.getData();
    } else {
      this.isEmpty = false;
      this.isSkeletonLoading = true;
      this.getListData();
    }
  }

  getData() {
    let urlApi = this.mainService.getMainUrl() + '/api/admin/secGroups/' + this.id;

    this.httpService.getUrlApi(
      urlApi,
      false,
      (result: any, status: string) => {
        if (status === 'success') {
          this.name = result.groupName;
        } else {
          this.navCtrl.back();
          this.httpService.handlingHttpError(result);
        }
      });
  }

  getListData() {
    let urlApi = this.mainService.getMainUrl();

    if (this.segmentValue == "Details") {
      urlApi += '/api/admin/secGroups/' + this.id + '/detail';
    } else if (this.segmentValue == "Matrix") {
      urlApi += '/api/admin/secGroups/' + this.id + '/matrix';
    }

    this.httpService.getUrlApi(
      urlApi,
      false,
      (result: any, status: string) => {
        if (status === 'success') {
          this.listData = result ?? [];
          this.isEmpty = this.listData.length == 0;
        } else {
          this.httpService.handlingHttpError(result);
        }
        this.isSkeletonLoading = false;
      });
  }

  onSubmit() {
    this.swalService.present("Are you sure to submit?", "question", (res: any) => {
      if (res) {
        let urlApi = this.mainService.getMainUrl() + '/api/admin/secGroups';

        let dataPost: any = {
          groupName: this.name
        };

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
    this.swalService.present(data.message, "success");
    if (this.id == null) {
      this.navCtrl.back();
    }
  }

  onRemove(data: any) {
    this.swalService.present(`Are you sure to delete this Security ${this.segmentValue} ?`, "question", (res: any) => {
      if (res) {
        let urlApi = this.mainService.getMainUrl();

        if (this.segmentValue == "Details") {
          urlApi += '/api/admin/secGroupsDetail/delete';
        } else if (this.segmentValue == "Matrix") {
          urlApi += '/api/admin/secGroupsMatrix/delete';
        }

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
      this.getListData();
    });
  }

  async addDetail(id: any) {
    const modal = await this.modalCtrl.create({
      component: AddSecurityGroupDetailComponent,
      cssClass: 'modal-search',
      mode: 'ios',
      componentProps: {
        id,
        secGroupId: this.id,
        type: this.segmentValue
      },
    });

    modal.onDidDismiss().then((data) => {
      if (data.data != undefined) {
        this.swalService.present(data.data, "success", () => {
          this.isSkeletonLoading = true;
          this.listData = [];
          this.getListData();
        });
      }
    });

    return await modal.present();
  }
}
