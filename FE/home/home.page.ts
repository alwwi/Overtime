import { Component } from '@angular/core';
import { MainService } from '../services/main/main.service';
import { IonRouterOutlet, ModalController, NavController, Platform } from '@ionic/angular';
import { MenuComponent } from '../components/menu/menu.component';
import { HttpService } from '../services/http/http.service';
import { environment } from 'src/environments/environment';
import { NavigationEnd, NavigationExtras, Router } from '@angular/router';
import { App } from '@capacitor/app';
import { Toast } from '@capacitor/toast';
import OneSignal from 'onesignal-cordova-plugin';
import { NotificationService } from '../services/notification/notification.service';
import { SwalService } from '../services/swal/swal.service';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {
  iconList: any[] = [
    {
      icon: "../../assets/icon/general/checkin-checkout.svg",
      name: "Check In",
      url: "checkinout",
      authority: ["Check In Out"],
    },
    {
      icon: "../../assets/icon/self-service/self-service.png",
      name: "Self Service",
      url: "self-service",
      authority: [],
    },
    {
      icon: "../../assets/icon/self-service/overtime.svg",
      name: "Overtime",
      url: "self-service/overtime",
      authority: ["Overtime"],
    },
    {
      icon: "../../assets/icon/general/checkin-checkout.svg",
      name: "Check Out",
      url: "checkinout",
      authority: ["Check In Out"],
    },
  ];

  defaultMenu: any = [];
  favoriteMenu: any = [];
  newsFeeds: any = [];
  lastDateAttemptSignRule: any;
  disabledRefresh: boolean = false;
  isSkeletonLoading: boolean = true;
  isSkeletonLoadingFavMenu: boolean = true;
  isSkeletonLoadingNews: boolean = true;
  isRefresh: boolean;
  isSkeletonLoadingProfile: boolean = true;
  listPersonalInfo: any = {};
  minYear: number | null = null;
  today: any = new Date();
  surveySession: any = [];

  dataGeneral = {
    periode: null,
    startDate: null,
    endDate: null,
    minYear: null,
    documentType: null,
    mtfOnly: false,
    notifLogin: false,
    showOnManager: false,
    showOnProfile: false,
    status: 'Not Started',
  }

  constructor(
    public mainService: MainService,
    private modalCtrl: ModalController,
    private navCtrl: NavController,
    private httpService: HttpService,
    private routerOutlet: IonRouterOutlet,
    private router: Router,
    private notificationService: NotificationService,
    private swallService: SwalService
  ) {
    this.handleBackButton();
  }

  ionViewWillEnter() {
    // this.checkVersion.initVersionCheck();
    this.isSkeletonLoading = true;
    setTimeout(() => {
      this.initPage();
    }, 500);
    this.mainService.checkUserLogin();
  }


  ionViewWillLeave() {
    App.removeAllListeners();
  }

  async handleBackButton() {
    this.router.events.subscribe(async (event) => {
      if (event instanceof NavigationEnd) {
        App.addListener('backButton', ({ canGoBack }) => {
          if (event.url == "/tabs/home") {
            if (!this.routerOutlet.canGoBack()) {
              if (this.mainService.countExit == 0) {
                this.mainService.countExit++;
                Toast.show({
                  text: "Press again to exit!",
                  duration: "short",
                });
                setTimeout(() => {
                  this.mainService.countExit = 0;
                }, 2000);
              } else {
                App.exitApp();
              }
            }
          }
        });
      }
    });
  }

  doRefresh(event: any) {
    this.isRefresh = true;
    this.isSkeletonLoading = true;
    this.isSkeletonLoadingFavMenu = true;
    this.isSkeletonLoadingNews = true;
    setTimeout(() => {
      this.initPage();
      event.target.complete();
    }, 500);
  }

  async initPage() {
    this.mainService.profile = JSON.parse(await this.mainService.getUserRef());
    console.log("CEK PROFILE : ", this.mainService.profile);
    this.isSkeletonLoadingProfile = false;
    this.mainService.checkMenu(this.iconList).then((res) => {
      this.iconList = res;

      let flagOvt = this.mainService.profile?.employee?.flagOvertime;

      if (flagOvt === 0 || flagOvt == null){
        this.iconList = this.iconList.filter((menu: any) => menu.name !== 'Overtime');
      }
    })
    this.getFavoriteMenu();
    // this.getEppActive();

    if (this.mainService.profile.isAvailableDocument == true) {
      this.navCtrl.navigateForward("employee-agreement");
    }

    // if ((this.mainService.profile.employee["attemptRegulationChecklist"] == false
    //   || this.mainService.profile.employee["lastAttemptRegulation"] == null) && this.mainService.profile.isActiveEpp == true) {
    //   this.navCtrl.navigateForward("show-rule");
    // }

    //survey alert
    this.showSurveyAlert();

    if (this.mainService.platformName != "web") {
      this.registerDevice(
        await OneSignal.User.getOnesignalId()
      );
    }

    setTimeout(() => {
      if (!this.mainService.alreadyPromptPassword) {
        this.checkChangePw();
      }

      if (this.mainService.platformName == "android") {
        if (!this.mainService.isModalUpdateVersionOpen) {
          this.checkVersionApp();
        }
      }

      this.isSkeletonLoading = false;
    }, 2000);
  }

  getData() {
    let urlApi = this.mainService.getMainUrl() + '/api/empsurvey/session/';

    this.httpService.getUrlApi(
      urlApi,
      false,
      (result: any, status: string) => {
        if (status === 'success') {
          if (result != null && result != '') {
            this.dataGeneral = result;
            this.minYear = result.minYear;
          }
        } else {
          this.navCtrl.back();
          this.httpService.handlingHttpError(result);
        }
      });
  }

  showSurveyAlert() {
    this.getPersonalInfo().then(() => {
      this.getSessionStatus(this.listPersonalInfo.year_service).then(() => {
        const urlApi = this.mainService.getMainUrl() + '/api/user/check-survey-reminder';

        this.httpService.getUrlApi(urlApi, false, (res: any, status: string) => {
          if (status === 'success') {
            if (res.surveys && res.surveys.length > 0) {
              const sessionMinYearMap = {};
              this.surveySession.forEach(session => {
                sessionMinYearMap[session.id] = session.minYear;
              });

              const eligibleSurveys = res.surveys.filter(survey => {
                const minYearRequired = sessionMinYearMap[survey.sessionId];
                if (minYearRequired !== undefined) {
                  return parseInt(this.listPersonalInfo.year_service) >= parseInt(minYearRequired);
                }
                return false;
              });

              if (eligibleSurveys.length > 0) {
                this.showNextSurvey(eligibleSurveys, 0);
              }
            }
          } else {
            this.httpService.handlingHttpError(res);
          }
        });
      });
    });
  }

  getPersonalInfo() {
    return new Promise((resolve, reject) => {
      const urlApi = this.mainService.getMainUrl() + '/api/empsurvey/eligibilitysurvey/year';

      this.httpService.getUrlApi(urlApi, false, (res: any, status: string) => {
        if (status === 'success') {
          this.listPersonalInfo = res[0];
          resolve(this.listPersonalInfo);
        } else {
          this.httpService.handlingHttpError(res);
          reject("Error fetching personal info");
        }
      });
    });
  }


  showNextSurvey(surveys: any[], index: number) {
    if (index >= surveys.length) {
      return; // Semua survey sudah ditampilkan
    }

    const survey = surveys[index];

    if (survey.needSurvey && survey.notifLogin) {
      const message = `Anda belum mengisikan Employee Survey "${survey.surveyName}", Mohon untuk mengisikan survey terlebih dahulu.`;
      this.swallService.present(message, 'question', (result) => {
        if (result) {
          // Navigasi ke halaman survey dengan parameter session ID
          this.navCtrl.navigateForward('employeesurvey', {
            queryParams: {
              sessionId: survey.sessionId
            }
          });
        } else {
          // Jika user menekan "Cancel", tampilkan alert untuk survey berikutnya
          this.showNextSurvey(surveys, index + 1);
        }
      });
    } else {
      // Jika survey ini tidak perlu ditampilkan, lanjut ke survey berikutnya
      this.showNextSurvey(surveys, index + 1);
    }
  }

  async getSessionStatus(yearOfService: string) {
    const urlApi = this.mainService.getMainUrl() + '/api/empsurvey/sessionbyidlist?yearOfService=' + yearOfService;

    this.httpService.getUrlApi(urlApi, false, (res: any, status: string) => {
      if (status === 'success') {
        if (Array.isArray(res)) {
          this.surveySession = res
            .map((item: any) => {
              const today = this.mainService.convertDate(this.today, "yyyy-MM-dd");
              const startDate = item.startDate;
              const endDate = item.endDate;
              const isEnabled = (today >= startDate && today <= endDate);
              const isEligible = parseInt(yearOfService) >= parseInt(item.minYear);

              return {
                ...item,
                isEnabled,
                isEligible
              };
            });
        }
        this.isSkeletonLoading = false;
      } else {
        this.httpService.handlingHttpError(res);
        this.isSkeletonLoading = false;
      }
    });
  }


  async getProfile() {
    const urlApi =
      this.mainService.getMainUrl() +
      '/api/myprofile';
    this.httpService.getUrlApi(urlApi, false, (res: any, status: string) => {
      if (status === 'success') {
      } else {
        this.httpService.handlingHttpError(res);
      }
    });

    setTimeout(() => {
      this.checkChangePw();
    }, 2000);
  }

  getNewsFeeds() {
    this.newsFeeds = [];
    const urlApi =
      this.mainService.getMainUrl() +
      '/api/user/news/current';
    this.httpService.getUrlApi(urlApi, false, (res: any, status: string) => {
      if (status === 'success') {
        this.handleNewsFeeds(res);
      } else {
        this.httpService.handlingHttpError(res);
      }
    });
    this.isRefresh = false;
    this.isSkeletonLoadingNews = false;
  }

  handleNewsFeeds(data: any) {
    for (let i = 0; i < data.length; i++) {
      const el = data[i];
      if (el.active) {
        this.newsFeeds.push(el);
      }
    }
  }

  // getEppActive() {
  //   if (this.mainService.profile != null && this.mainService.profile != undefined) {
  //     this.lastDateAttemptSignRule = new Date(this.mainService.profile.employee["lastAttemptRegulation"]).getTime();
  //     if (this.mainService.profile.employee["lastAttemptRegulation"] == null) {
  //       this.lastDateAttemptSignRule = new Date().getTime();
  //     } else {
  //       this.lastDateAttemptSignRule = this.mainService.profile.employee["lastAttemptRegulation"];
  //     }

  //     const urlApi =
  //       this.mainService.getMainUrl() +
  //       '/api/admin/epp/find?lastDateAttempt=' + this.lastDateAttemptSignRule;
  //     this.httpService.getUrlApi(urlApi, false, (res: any, status: string) => {
  //       if (status === 'success') {
  //       } else {
  //       }
  //     });
  //   }
  // }

  async getFavoriteMenu() {
    this.defaultMenu = [];

    await this.mainService.checkMenu(this.mainService.defaultMenu).then(res => {
      this.defaultMenu = res;
    });

    let dataMenu = JSON.parse(
      this.mainService.decrypt(localStorage.getItem("favMenu"))
    );

    if (dataMenu !== null) {
      await this.mainService.checkMenu(dataMenu).then(res => {
        dataMenu = res;
      });

      dataMenu = dataMenu.filter((data: any) => data.isShow === true);

      this.favoriteMenu = dataMenu != null ? dataMenu : [];
    };

    setTimeout(() => {
      this.isSkeletonLoadingFavMenu = false;
    }, 1000);

    (this.newsFeeds.length == 0 || this.isRefresh)
      ? this.getNewsFeeds()
      : this.newsFeeds;
  }

  filterMenu(arr1: any, arr2: any) {
    let res = [];
    res = arr1.filter((el: any) => {
      return !arr2.find((element: any) => {
        return element.url == el.url;
      });
    });
    return res;
  }

  async openMenu(type: any) {
    if (type == 'edit') {
      const modal = await this.modalCtrl.create({
        component: MenuComponent,
        cssClass: 'modal-fullscreen',
        componentProps: {
          defaultMenu: this.defaultMenu,
          edit: type == "edit" ? true : false,
        },
      });

      modal.onDidDismiss().then(() => {
        this.getFavoriteMenu();
      });

      return await modal.present();
    } else {
      this.navCtrl.navigateForward("/menu");
    }
  }

  gotoMenu(url: string, name?: string) {
    let params: NavigationExtras = {
      queryParams: {
        name: name,
      },
    };

    if (url !== '/tabs/balance' && url !== '/tabs/request') {
      params.queryParams['isFromHome'] = true;
    }
    this.navCtrl.navigateForward([url], params);
  }

  checkChangePw() {
    if (this.mainService.profile != undefined) {
      if (this.mainService.profile.isChangePassword == null || !this.mainService.profile.isChangePassword) {
        this.mainService.alreadyPromptPassword = true;
        this.navCtrl.navigateForward("change-password");
      }
    }
  }

  checkVersionApp() {
    if (this.mainService.profile != undefined) {
      if (this.mainService.profile.talentsParameter[1].value != null && this.mainService.profile.talentsParameter[1].value > environment.version) {
        this.mainService.showUpdateVersion((res) => {
          this.mainService.logout();
        });
      }
    }
  }

  registerDevice(device: any) {
    if (this.mainService.profile.employee["deviceID"] == null && device != this.mainService.profile.employee["deviceID"]) {
      this.notificationService.createOneSignalUser(device);
    } else {
      OneSignal.login(this.mainService.profile.employee["deviceID"]);
    }
  }

  goToNewsFeedDetail(id: any) {
    let params: NavigationExtras = {
      queryParams: {
        id: this.mainService.encrypt(id),
        isFromHome: true
      },
    };
    this.navCtrl.navigateForward("/news-feed/news-feed-detail", params);
  }

}
