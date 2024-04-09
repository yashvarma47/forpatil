import { NgModule, Component } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
 
//    Customer Management
import { BlockAccountComponent } from './Admin/customer-management/admin-customer-management/block-account/block-account.component';
import { CloseAccountComponent } from './Admin/customer-management/admin-customer-management/close-account/close-account.component';
import { DeleteAccountComponent } from './Admin/customer-management/admin-customer-management/delete-account/delete-account.component';
//    Admin Enquiry
import { AdminEnquiriesComponent } from './Admin/enquiries/admin-enquiries/admin-enquiries.component';
import { AdminAccountClosureComponent } from './Admin/enquiries/admin-account-closure/admin-account-closure.component';
//    Admin Home
import { AdminHomeComponent } from './Admin/home/admin-home/admin-home.component';
//     Registration
import { CreateCustomerComponent } from './Admin/registration/admin-registration/create-customer/create-customer.component';
import { OpenAccountComponent } from './Admin/registration/admin-registration/open-account/open-account.component';
import { AdminTransferComponent } from './Admin/transfer/admin-transfer/admin-transfer.component';
 
//    Login Componentd
import { ForgotPasswordComponent } from './Login-Fields/forgot-password/forgot-password.component';
import { ForgotUseridComponent } from './Login-Fields/forgot-userid/forgot-userid.component';
import { LoginComponent } from './Login-Fields/login/login.component';
import { ResetPasswordComponent } from './Login-Fields/reset-password/reset-password.component';
 
//    User Accounts
import { AccountClosureComponent } from './User/account/account-closure/account-closure.component';
import { AccountDetailsComponent } from './User/account/account-details/account-details.component';
import { ViewAccountBalanceComponent } from './User/account/view-account-balance/view-account-balance.component';
 
//    User Profile
import { ChangePasswordComponent } from './User/profile/change-password/change-password.component';
import { ManageNotificationsComponent } from './User/profile/manage-notifications/manage-notifications.component';
import { SetupBudgetComponent } from './User/profile/setup-budget/setup-budget.component';
import { SetupProfileComponent } from './User/profile/setup-profile/setup-profile.component';
import { TransactionSummaryComponent } from './User/transaction-summary/transaction-summary.component';
import { TransferComponent } from './User/transfer/transfer/transfer.component';
import { UserDashboardComponent } from './User/user-dashboard/user-dashboard.component';
import { TransferWithinMyAccountComponent } from './User/transfer/transfer-within-my-account/transfer-within-my-account.component';
import { AddBeneficiaryComponent } from './User/transfer/add-beneficiary/add-beneficiary.component';
import { TransferWithinMyBankComponent } from './User/transfer/transfer-within-my-bank/transfer-within-my-bank.component';
import { AuthGuard } from './auth.guard';
import { UnblockpopupComponent } from './Admin/customer-management/unblockpopup/unblockpopup.component';
import { UnblockaccountComponent } from './Admin/customer-management/unblockaccount/unblockaccount.component';
 
 
 
 
const routes: Routes = [
 
              // Login Routing
  {path: '', component: LoginComponent},
  {path: 'forgot-userid', component: ForgotUseridComponent},
  {path: 'forgot-password', component: ForgotPasswordComponent},
  {path: 'forgot-password/reset-password', component: ResetPasswordComponent},
 
              // User Routing
  { path: 'user-dashboard', component: UserDashboardComponent},
  {path : 'transaction-summary', component:TransactionSummaryComponent},
  {path : 'transfer', component:TransferComponent},
                // Account
  { path: 'accountdetails', component: AccountDetailsComponent},
  { path : 'view-account-balance', component:ViewAccountBalanceComponent},
  {path: 'account-closure', component:AccountClosureComponent},
 
            //Profile
  {path:'setup-profile', component:SetupProfileComponent},
  { path: 'change-password', component: ChangePasswordComponent},
  { path: 'setup-budget', component: SetupBudgetComponent},
  { path: 'manage-notifications', component: ManageNotificationsComponent},
 
  { path: 'selfAccTransfer', component: TransferWithinMyAccountComponent},
  { path: 'transferWithinBank', component: TransferWithinMyBankComponent},
  {path: 'add-benficiary',component:AddBeneficiaryComponent},
 
  { path: 'create-customer', component: CreateCustomerComponent},
        //Teller Dashboard
  { path: 'admin-home', component:AdminHomeComponent},
  // { path: 'create-customer', component:CreateCustomerComponent},
  { path: 'open-account',component:OpenAccountComponent},
  { path: 'block-account',component:BlockAccountComponent},
  { path: 'close-account',component:CloseAccountComponent},
  { path: 'delete-account',component:DeleteAccountComponent},
 
 
 
          // Adminn Transfer
  {path: 'admin-transfer',component:AdminTransferComponent},
  {path: 'admin-enquiries',component:AdminEnquiriesComponent},
  { path: 'unblock-account',component:UnblockaccountComponent},
  { path: 'unblockpopup',component:UnblockpopupComponent},
 
 
 
 
 
 
 
 
 
];
 
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }