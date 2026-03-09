import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NavController } from '@ionic/angular';
import { HttpService } from 'src/app/services/http/http.service';
import { MainService } from 'src/app/services/main/main.service';
import { NotificationService } from 'src/app/services/notification/notification.service';
import { SwalService } from 'src/app/services/swal/swal.service';

@Component({
  selector: 'app-overtime-self-service',
  templateUrl: './overtime.page.html',
  styleUrls: ['./overtime.page.scss'],
})
export class OvertimePage {

  startDate: any = null;
  endDate: any = null;
  remark?: string;

  listType: any = [];

  totalHour: any = "00:00";
  messageValidation: string = "";

  isSkeletonLoading: boolean = false;
  isAdmin: boolean = true;
  employee: any;
  employmentId: any;

  constructor(
    public mainService: MainService,
    private httpService: HttpService,
    private navCtrl: NavController,
    private router: ActivatedRoute,
    private swalService: SwalService,
    private notificationService: NotificationService
  ) { }

  ionViewWillEnter() {
    this.router.queryParams.subscribe((param) => {
      this.isAdmin = param["isAdmin"] == "true";
      this.employmentId = this.mainService.decrypt(param["employmentId"]);
    });
        if (this.isAdmin) {
          this.isSkeletonLoading = true;
          this.getSelectedEmplDetail();
        }
  }

  getSelectedEmplDetail() {
    const urlApi = this.mainService.getMainUrl() + '/api/admin/employee/find/detail?q=' + this.employee.employeeNo;

    this.httpService.getUrlApi(urlApi, false, (res: any, status: string) => {
      if (status === 'success') {
        this.handleSuccessEmplDetail(res);
      } else {
        this.httpService.handlingHttpError(res);
      }
    });
  }

  handleSuccessEmplDetail(data: any) {
    if (data != null && data.content.length > 0) {
      this.employee = data.content[0];
    }
    this.isSkeletonLoading = false;
  }

  doCalculateOvertime() {
    if (this.startDate != null && this.endDate != null) {
      let epochTimeDifference = Math.ceil((new Date(this.endDate).setSeconds(0) / 1000 - new Date(this.startDate).setSeconds(0) / 1000)); // in seconds

      let hours: any = Math.floor(epochTimeDifference / 3600);
      epochTimeDifference %= 3600;

      let minutes: any = Math.floor(epochTimeDifference / 60);

      let total = '';

      if (hours < 10)
        hours = '0' + hours;

      if (minutes < 10)
        minutes = '0' + minutes;

      if (epochTimeDifference < 0) {
        total = '00:00';
      } else {
        total = hours + ':' + minutes;
      }

      this.totalHour = total;
    }
  }

  verificationForm() {
    let epochStart = Math.floor((new Date(this.startDate)).getTime() / 1000);
    let epochEnd = Math.floor((new Date(this.endDate)).getTime() / 1000);

    if (epochStart >= epochEnd) {
      this.messageValidation = "End time must be greater than start time!";
      return false;
    } else if ((epochEnd - epochStart) / 3600 > 24) {
      this.messageValidation = "Total hours can't be more than 24 hours!";
      return false;
    }

    return true;

  }

  onSubmit() {
    this.swalService.present("Are you sure to submit?", "question", (res: any) => {
      if (res) {
        if (this.verificationForm()) {
          let urlApi = this.mainService.getMainUrl();

          if (this.isAdmin) {
            urlApi += '/api/admin/dataApproval/overtime/submit';
          } else {
            urlApi += '/api/user/dataApproval/overtime/submit';
          }

          let dataPost: any = {
            "totalHoursGross": this.totalHour + ":00",
            "startDate": this.startDate,
            "endDate": this.endDate,
            "remark": this.remark
          };

          if (this.isAdmin) {
            dataPost.employmentRequestee = this.employmentId;
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
        } else {
          this.swalService.present(this.messageValidation, "error");
        }
      }
    })
  }

  handleSuccessSubmit(data: any) {
    this.mainService.dismissLoading();
    this.swalService.present(data.message, "success");
    if (this.isAdmin) {
      this.navCtrl.navigateBack('/administration/transactions');
    } else {
      let payload: any = {
        url: "/self-service/absence/add-absence",
        contents: "Ada pengajuan Overtime yang harus anda tindak lanjuti. silahkan check di menu Approval HC EAZY anda.",
        headings: "Pengajuan Request Overtime"
      };
      this.notificationService.postPushNotification(data, payload);
      this.navCtrl.back();
    }
  }
}
