import { Component } from '@angular/core';
import { ActivatedRoute, NavigationExtras } from '@angular/router';
import { NavController } from '@ionic/angular';
import { HttpService } from 'src/app/services/http/http.service';
import { MainService } from 'src/app/services/main/main.service';

@Component({
  selector: 'app-overtime-approval',
  templateUrl: './overtime.page.html',
  styleUrls: ['./overtime.page.scss'],
})
export class OvertimePage {
  action: any;
  canRefresh: boolean = false;
  isSkeletonLoading: boolean = true;
  isLoadMoreShow: boolean = false;
  isSpinnerShow: boolean = false;
  isEmpty: boolean = false;
  indexPaging: number = 0;
  size = this.mainService.dataDisplaySize;

  filterName: any;
  startDate: any;
  endDate: any;
  status: any;
  listData: any = [];
  globalConstant: any = {};

  constructor(
    public mainService: MainService,
    private httpService: HttpService,
    private navCtrl: NavController,
    private activatedRoute: ActivatedRoute,
  ) { }

  ionViewWillEnter() {
    this.indexPaging = 0;
    this.listData = [];
    this.activatedRoute.queryParams.subscribe((params) => {
      this.action = params["action"];
      if (this.action != "Need Approval") {
        this.startDate = new Date(new Date().getFullYear(), new Date().getMonth(), 1);
        this.endDate = new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0);
      }
      this.getListData();
    });
    this.globalConstant = this.mainService.globalConstant;
  }

  doRefresh(event: any) {
    this.indexPaging = 0;
    this.isSkeletonLoading = true;
    this.getListData();
    setTimeout(() => {
      event.target.complete();
    }, 500);
  }

  filter() {
    let data: any = {
      filterName: this.filterName,
      startDate: this.startDate,
      endDate: this.endDate,
      status: this.status
    }

    if (this.action == "Need Approval") {
      data.type = "Approval";
    } else {
      data.type = "My Request";
    }

    this.mainService.filterModal(data, (res: any) => {
      if (res.data != undefined) {
        this.filterName = res.data.filterName;
        this.startDate = res.data.startDate;
        this.endDate = res.data.endDate;
        this.status = res.data.status;
        this.isSkeletonLoading = true;
        this.listData = [];
        this.indexPaging = 0;
        this.getListData();
      }
    })
  }

  loadMore() {
    this.isSpinnerShow = true;
    this.indexPaging++;
    this.getListData();
  }

  getListData() {
    let urlApi = this.mainService.getMainUrl() + "/api/user/dataApproval/overtime";

    if (this.action == "Need Approval") {
      urlApi += "/needapproval";
    } else if (this.action == "Approval History") {
      urlApi += "/historyApproval";
    } else {
      urlApi += "/myrequest";
    }

    urlApi += "?page=" + this.indexPaging + "&size=" + this.size;

    if (this.action == "Need Approval") {
      if (this.filterName != undefined && this.filterName != '' && this.filterName != null) {
        urlApi = urlApi + '&ppl=' + this.filterName;
      }
    } else {
      if (this.filterName != undefined && this.filterName != '' && this.filterName != null) {
        urlApi = urlApi + '&requestNo=' + this.filterName;
      }
    }

    if (this.startDate != undefined && this.startDate != '' && this.startDate != null) {
      urlApi = urlApi + '&requestDateStart=' + this.mainService.convertDate(this.startDate, this.mainService.globalConstant.dateFormatToDB);
    }

    if (this.endDate != undefined && this.endDate != '' && this.endDate != null) {
      urlApi = urlApi + '&requestDateEnd=' + this.mainService.convertDate(this.endDate, this.mainService.globalConstant.dateFormatToDB);
    }

    if (this.status != undefined && this.status != '' && this.status != null) {
      urlApi = urlApi + '&status=' + this.status;
    }

    this.httpService.getUrlApi(urlApi, false, (res: any, status: any) => {
      if (status == "success") {
        if (this.indexPaging == 0) {
          this.listData = res.content;
        } else {
          res.content.forEach((el: any) => this.listData.push(el));
        }

        this.isEmpty = res.empty;
        this.isLoadMoreShow = !res.last;
      } else {
        this.isEmpty = true;
        this.httpService.handlingHttpError(res);
      }
      this.isSkeletonLoading = false;
      this.isSpinnerShow = false;
    });
  }

  goToDetail(id: any) {
    let params: NavigationExtras = {
      queryParams: {
        id: this.mainService.encrypt(id),
        action: this.action
      },
    };

    this.navCtrl.navigateForward("/request-detail/overtime", params);
  }
}
