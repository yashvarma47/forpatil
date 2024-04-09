import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
// import { DatePipe } from '@angular/common';
import { AccountService } from 'src/app/Services/account.service';
import { TransactionService } from 'src/app/Services/transaction.service';
import { UserService } from 'src/app/Services/user.service';
import { Chart, registerables } from 'node_modules/chart.js';
Chart.register(...registerables);
 
export interface PieChartData {
  total_fuel_expense: number;
  total_food_expense: number;
  total_travel_expense: number;
  total_bills_expense: number;
  total_entertainment_expense: number;
  total_loan_expense: number;
  total_recharge_expense: number;
  total_shopping_expense: number;
  total_miscellaneous_expense: number;
}
 
@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
 
 
 
export class UserDashboardComponent implements OnInit {
  dropdownOptionsFrom: any;
 
  formattedAmount: string = '';
  balanceAmount: number = 0;
  dataAfterDecimal: string = '';
  chooseAcc: string = '';
  chooseAccNo: string = '';
  dropdownOptionschooseAcc: any[] = [];
  dropdownOptionschooseAccNo: any[] = [];
  customerAccounts: any[] = [];
  customerDetails: any = {};
  selectedAccountType: string = "";
  selectedAccount: string = "";
  showBalance: boolean = false;
  accountHolder: any;
  pieChartData: number[] = [];
  foodExpense: number = 0;
  fuelExpense: number = 0;
  travelExpense: number = 0;
  billsExpense: number = 0;
  entertainmentExpense: number = 0;
  loanExpense: number = 0;
  rechargeExpense: number = 0;
  shoppingExpense: number = 0;
  miscellaneousExpense: number = 0;
  accountType: string = "";
  selectedTimeframe: string = "year";
  todayDate: Date = new Date();
  year: number = this.todayDate.getFullYear();
  month: number = this.todayDate.getMonth() + 1;
  quarter: number = Math.ceil(this.month / 3);
  dates: number[] = [];
  expenses: number[] = [];
  categoryOptions: string[] = [];
  selectedOption: string = "";
  myChart :any;
 
  constructor(private router: Router, private accountService: AccountService, private userService: UserService, private transactionService: TransactionService) { }
 
  updatePieChart(data: any): void {
    this.foodExpense = parseFloat((data.total_food_expense || 0).toFixed(2));
    this.fuelExpense = parseFloat((data.total_fuel_expense || 0).toFixed(2));
    this.travelExpense = parseFloat((data.total_travel_expense || 0).toFixed(2));
    this.billsExpense = parseFloat((data.total_bills_expense || 0).toFixed(2));
    this.entertainmentExpense = parseFloat((data.total_entertainment_expense || 0).toFixed(2));
    this.loanExpense = parseFloat((data.total_loan_expense || 0).toFixed(2));
    this.rechargeExpense = parseFloat((data.total_recharge_expense || 0).toFixed(2));
    this.shoppingExpense = parseFloat((data.total_shopping_expense || 0).toFixed(2));
    this.miscellaneousExpense = parseFloat((data.total_miscellaneous_expense || 0).toFixed(2));
 
    // Update the pieChartData array
    this.pieChartData = [
      this.foodExpense,
      this.fuelExpense,
      this.travelExpense,
      this.billsExpense,
      this.entertainmentExpense,
      this.loanExpense,
      this.rechargeExpense,
      this.shoppingExpense,
      this.miscellaneousExpense
    ];
 
    this.RenderPieChart(this.pieChartData);
  }
 
  prepareDataForLineChart() {
    // Temporary array to store day parts
   
    this.dates = [];
    this.expenses = [];
    this.transactionService.getExpensesByCategory((parseInt(this.chooseAccNo)), this.accountType, this.selectedOption).subscribe(
      (response: any) => {
        if (response && response.status === 200) {
          // 
         
 
          // Temporary array to store day parts
          const tempDates: number[] = [];
 
          // Extract day parts and store them in the temporary array
          for (const date in response.data) {
            if (response.data.hasOwnProperty(date)) {
              const day = parseInt(date.split("-")[2]);
              tempDates.push(day);
            }
          }
 
          // Sort the day parts in ascending order
          const sortedDates = tempDates.sort((a, b) => a - b);
 
          // Iterate through the sorted dates to populate the final arrays
          sortedDates.forEach((day) => {
            for (const date in response.data) {
             
              if (response.data.hasOwnProperty(date)) {
                const dayPart = parseInt(date.split("-")[2]);
                if (day === dayPart) {
                  this.dates.push(day);
                  this.expenses.push(response.data[date]);
                }
              }
            }
 
          });
 
          // Now you have the dates in ascending order and their corresponding expenses
          
          
          this.RenderLineChart();
        }
      }
    );
  }
 
  onOptionSelect(selectedOption: string): void {
 
    this.selectedOption = selectedOption;
    
 
    // Render the line chart
    this.prepareDataForLineChart();
   
  }
 
  yearbudget() {
    this.selectedTimeframe = 'year';
    this.accountType = this.chooseAcc;
    this.transactionService.getYearlyExpenses((parseInt(this.chooseAccNo)), this.accountType, this.year).subscribe(
      (response: any) => {
        if (response && response.status === 200) {
          
 
          this.updatePieChart(response.data);
        }
      }
    );
  }
 
  monthbudget() {
    this.selectedTimeframe = 'month';
    this.accountType = this.chooseAcc;
    
    this.transactionService.getMonthlyExpenses((parseInt(this.chooseAccNo)), this.accountType, this.month).subscribe(
      (response: any) => {
        if (response && response.status === 200) {
          this.updatePieChart(response.data);
          this.categoryOptions = [];
          for (const key in response.data) {
 
            if (response.data.hasOwnProperty(key)) {
              if (key.includes('_expense')) {
                const category = key.replace('total_', '').replace('_expense', '');
                this.categoryOptions.push(category);
 
              }
            }
          }
          
        }
        this.prepareDataForLineChart()
      }
    );
  }
  weekbudget() {
    this.selectedTimeframe = 'week';
    this.accountType = this.chooseAcc;
 
    this.transactionService.getWeeklyExpenses((parseInt(this.chooseAccNo)), this.accountType).subscribe(
      (response: any) => {
        if (response && response.status === 200) {
          this.updatePieChart(response.data);
        }
      }
    );
  }
  yesterdaybudget() {
    this.selectedTimeframe = 'yesterday';
 
    this.accountType = this.chooseAcc;
 
    this.transactionService.getTodaysOrYesterdaysExpenses((parseInt(this.chooseAccNo)), this.accountType, this.selectedTimeframe).subscribe(
      (response: any) => {
        if (response && response.status === 200) {
          this.updatePieChart(response.data);
        }
      }
    );
 
  }
  todaybudget() {
    this.selectedTimeframe = 'today';
    this.accountType = this.chooseAcc;
 
    this.transactionService.getTodaysOrYesterdaysExpenses((parseInt(this.chooseAccNo)), this.accountType, this.selectedTimeframe).subscribe(
      (response: any) => {
        if (response && response.status === 200) {
          this.updatePieChart(response.data);
        }
      }
    );
  }
 
 
  formatAmount(amount: number) {
    const truncatedAmount = Math.floor(amount);
    return truncatedAmount.toString();
}
 
  getDigitsAfterDecimal(amount: number): string {
    const numberString = amount.toFixed(2); 
    const splitNumber = numberString.split('.');
    if (splitNumber.length === 2) {
      return splitNumber[1];
    } else {
      return '00';
    }
  }
 
  generatePDF(): void {
    
  }
 
  transaction(): void {
    this.router.navigate(['transaction-summary']);
  }
  ngOnInit(): void {
    const storedUserId = localStorage.getItem('userId') ?? '';
 
    this.userService.userId$.subscribe(userId => {
      const numericUserId = parseInt(userId || storedUserId, 10);
 
      localStorage.setItem('userId', numericUserId.toString());
 
      this.accountService.getCustomerDetails(numericUserId).subscribe(
        (response: any) => {
          if (response && response.statusCode === 200) {
            // if (this.dropdownOptionschooseAcc.length > 0) {
            //   this.chooseAcc = this.dropdownOptionschooseAcc[0].value;
            //   this.monthbudget(); // Render data for the month
            // }
            this.updateUI(response);
          } else {
            console.error('API Error:', response && response.statusMessage);
          }
        },
        (error) => {
          console.error('API Error:', error);
        }
      );
    });
    this.formattedAmount = this.formatAmount(this.balanceAmount);
    // this.RenderChart();
    // this.monthbudget();
    this.RenderLineChart();

  }
  // updateUI(response: any): void {
  //   this.customerDetails = response.data;
  //   this.customerAccounts = response.accounts;
  //   this.dropdownOptionschooseAcc = [...new Set(this.customerAccounts.map((acc: { accounttype: any }) => acc.accounttype))]
  //     .map((accountType: any) => ({ value: accountType, label: accountType }));
 
  //   if (this.dropdownOptionschooseAcc.length > 0) {
  //     this.chooseAcc = this.dropdownOptionschooseAcc[0].value;
  //     this.onAccountTypeChange();
  //   }
  // }

  updateUI(response: any): void {
    this.customerDetails = response.data;
    this.customerAccounts = response.accounts;
    this.dropdownOptionschooseAcc = [...new Set(this.customerAccounts.map((acc: { accounttype: any }) => acc.accounttype))]
      .map((accountType: any) => ({ value: accountType, label: accountType }));
  
    if (this.dropdownOptionschooseAcc.length > 0) {
      this.chooseAcc = this.dropdownOptionschooseAcc[0].value;
      this.onAccountTypeChange();
      // Call updatePieChart here with initial data
      this.updatePieChart({
        total_food_expense: 0,
        total_fuel_expense: 0,
        total_travel_expense: 0,
        total_bills_expense: 0,
        total_entertainment_expense: 0,
        total_loan_expense: 0,
        total_recharge_expense: 0,
        total_shopping_expense: 0,
        total_miscellaneous_expense: 0
      });
    }
  }
 
  onViewClick() {
    this.showBalance = true;
  }
 
  // onAccountTypeChange(): void {
  //   if (this.chooseAcc) {
  //     const filteredAccounts = this.customerAccounts.filter(
  //       (acc: { accounttype: string }) => acc.accounttype === this.chooseAcc
  //     );
 
  //     this.dropdownOptionschooseAccNo = filteredAccounts.map((acc: { accountnumber: any }) => ({
  //       value: acc.accountnumber,
  //       label: acc.accountnumber.toString(),
  //     }));
 
  //     if (this.dropdownOptionschooseAccNo.length > 0) {
  //       this.chooseAccNo = this.dropdownOptionschooseAccNo[0].value;
  //       this.getSelectedAccount(this.chooseAccNo);
  //       this.onAccountNumberChange();
  //     }
  //   }
  // }

  onAccountTypeChange(): void {
    if (this.chooseAcc) {
      const filteredAccounts = this.customerAccounts.filter(
        (acc: { accounttype: string }) => acc.accounttype === this.chooseAcc
      );
  
      this.dropdownOptionschooseAccNo = filteredAccounts.map((acc: { accountnumber: any }) => ({
        value: acc.accountnumber,
        label: acc.accountnumber.toString(),
      }));
  
      if (this.dropdownOptionschooseAccNo.length > 0) {
        this.chooseAccNo = this.dropdownOptionschooseAccNo[0].value;
        this.getSelectedAccount(this.chooseAccNo);
        // this.monthbudget();
        this.onAccountNumberChange();
      }
    }
  }
 
  
  onSelectAccountNo(event: any): void {
    const selectedIndex = (event.target as HTMLSelectElement).selectedIndex;
 
    if (this.dropdownOptionschooseAccNo.length > 0) {
      this.chooseAccNo = this.dropdownOptionschooseAccNo[selectedIndex].value;
      const selectedAccount = this.getSelectedAccount(this.chooseAccNo);
      this.updateAccountDetails(selectedAccount);
      this.onAccountNumberChange();
    }
  }

onAccountNumberChange(): void {
  if (this.chooseAccNo) {
    const selectedAccount = this.getSelectedAccount(this.chooseAccNo);
    this.updateAccountDetails(selectedAccount);
    this.monthbudget();
  }
}
 
  getSelectedAccount(accountNumber: string): any {
    return this.customerAccounts.find((acc: { accountnumber: string }) => acc.accountnumber === accountNumber);
  }
  updateAccountDetails(selectedAccount: any): void {
    if (selectedAccount) {
 
      this.accountHolder = `${this.customerDetails.firstName} ${this.customerDetails.lastName}`;
      
 
      // this.customerDetails.branch = "Kharadi, Pune";
      // this.customerDetails.ifscCode = "RBA000123";
      this.balanceAmount = selectedAccount.totalbalance;
      
 
 
    }
  }
 
 
  RenderLineChart() {
    const canvas = document.getElementById('linechart');
  if (canvas instanceof HTMLCanvasElement) {
    canvas.height = 200; // Set height to 200px
    canvas.width = 839; // Set width to 839px
 
    // Check if a chart instance already exists
    if (this.myChart) {
      this.myChart.destroy(); // Destroy the existing chart instance
    }
 
      this.myChart = new Chart(canvas, {
        type: 'line',
        data: {
          labels: this.dates,
          datasets: [{
            label: this.selectedOption.toUpperCase() +' Expense',
            data: this.expenses,
            borderColor: ['rgb(75, 192, 192)'],
          }]
        },
      });
    } else {
      console.error('Canvas element with id "linechart" not found');
    }
  }
 
 
  // Pie Chart
  RenderPieChart(dataValues: number[]) {
    const existingChart = Chart.getChart('piechart');
    if (existingChart) {
      // If a chart exists, destroy it
      existingChart.destroy();
    }
    const myChart = new Chart('piechart', {
      type: 'pie',
      data: {
        labels: [
          'Food',
          'Fuel',
          'Travel',
          'Bills',
          'Entertainment',
          'Loan',
          'Recharge',
          'Shopping',
          'Miscellaneous'
        ],
        datasets: [{
          data: dataValues,
          backgroundColor: [
            'rgba(0, 137, 192, 1)',
            'rgba(179, 205, 35, 1)',
            'rgba(221, 118, 59, 1)',
            'rgba(255, 97, 50, 1)',
            'rgba(255, 170, 85, 1)',
            'rgba(226, 247, 145, 1)',
            'rgba(154, 224, 160, 1)',
            'rgba(52, 197, 166, 1)',
            'rgba(254, 140, 254, 1)'
          ],
          borderColor: [
            'rgb(255, 255, 255)',
          ],
          // borderWidth: 5,
          borderRadius: 2,
          hoverOffset: 100,
          // weight: ,
        }]
      },
      options: {
        plugins: {
          legend: {
            display: false,
          },
        },
        radius: '80%',
        layout: {
          padding: {
            top: 20,
            bottom: 20,
            left: 20,
            right: 20,
          }
        },
        elements: {
          arc: {
            borderWidth: 3,
          }
        },
      }
    })
  }
 
 
 
}