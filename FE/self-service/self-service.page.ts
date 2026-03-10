import { Component } from '@angular/core';
import { MainService } from '../services/main/main.service';

@Component({
  selector: 'app-self-service',
  templateUrl: './self-service.page.html',
  styleUrls: ['./self-service.page.scss'],
})

export class SelfServicePage {

  listMenu: any[] = [
    {
      title: "Absence",
      value: "Absence",
      url: "/self-service/absence",
      icon: "../../assets/icon/self-service/absence.svg",
      authority: [],
    },
    {
      title: "Overtime",
      value: "Overtime",
      url: "/self-service/overtime",
      icon: "../../assets/icon/self-service/overtime.svg",
      authority: ["Overtime"],
    },
    {
      title: "Payroll",
      value: "Payroll",
      url: "/self-service/payroll",
      icon: "../../assets/icon/self-service/payroll.svg",
      authority: [],
    },
    {
      title: "Payslip",
      value: "Payslip",
      url: "/self-service/payslip",
      icon: "../../assets/icon/self-service/payslip.svg",
      authority: [],
    },
    {
      title: "SPT 1721A1",
      value: "SPT 1721A1",
      url: "/self-service/sptai",
      icon: "../../assets/icon/self-service/spt.svg",
      authority: [],
    },
    {
      title: "FKP",
      value: "FKP",
      url: "/self-service/fkp",
      icon: "../../assets/icon/self-service/fkp.svg",
      authority: ["Request for Employee"],
    },
    {
      title: "MPP",
      value: "MPP",
      url: "/self-service/mpp",
      icon: "../../assets/icon/self-service/mpp.svg",
      authority: ["Switching MPP", "Additional MPP"],
    },
    {
      title: "Warning Letter",
      value: "Warning Letter",
      url: "/self-service/warning-letter",
      icon: "../../assets/icon/self-service/warning-letter.png",
      authority: [],
    },
    {
      title: 'Employee Survey',
      value: 'Employee Survey',
      url: '/employeesurvey',
      icon: '../../assets/icon/general/employee-survey.svg',
      authority: [],
    },
    {
      title: "Loan",
      value: "Loan",
      url: "/self-service/loan",
      icon: "../../assets/icon/self-service/loan.png",
      authority: ["Employee"],
    },
    {
      title: "Employee Agreement",
      value: "Employee Agreement",
      url: "/self-service/self-service-employee-agreement",
      icon: "../../assets/icon/self-service/employee-agreement.png",
      authority: [],
    },
    {
      title: "HC Connect",
      value: "HC Connect",
      url: "/self-service/hc-connect",
      icon: "../../../assets/icon/administration/reading_book.png",
      authority: [],
    }
  ];

  constructor(
    public mainService: MainService
  ) { }

  ionViewWillEnter() {
    this.mainService.checkMenu(this.listMenu).then(res => {
      this.listMenu = res;

      let flagOvt = this.mainService.profile?.employee?.flagOvertime;

      if(flagOvt === 0 || flagOvt !== null){
        this.listMenu = this.listMenu.filter((menu: any) => menu.title !== 'Overtime');
      }
    })
  }

  downloadUserGuide() {
    this.mainService.openInAppBrowser(this.mainService.driveLink.guideSelfServiceUrl);
  };
}
