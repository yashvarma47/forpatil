import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
 
import { BrowserAnimationsModuleConfig } from '@angular/platform-browser/animations';
import { MatMenuModule } from '@angular/material/menu';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatAccordion } from '@angular/material/expansion';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon'; // Import MatIconModule
 
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
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
import { HeaderComponent } from './User/user-dashboard/header/header.component';
import { SidenavComponent } from './User/user-dashboard/sidenav/sidenav.component';
import { TransferWithinMyAccountComponent } from './User/transfer/transfer-within-my-account/transfer-within-my-account.component';
import { TransferWithinMyBankComponent } from './User/transfer/transfer-within-my-bank/transfer-within-my-bank.component';
import { RouterModule } from '@angular/router';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { AdminSidenavComponent } from './Admin/admin-sidenav/admin-sidenav.component';
import { AddBeneficiaryComponent } from './User/transfer/add-beneficiary/add-beneficiary.component';
import { DenyConfirmationComponent } from './Admin/customer-management/admin-customer-management/close-account/deny-confirmation/deny-confirmation.component';
import { AuthGuard } from './auth.guard';
import { UnblockaccountComponent } from './Admin/customer-management/unblockaccount/unblockaccount.component';
import { UnblockpopupComponent } from './Admin/customer-management/unblockpopup/unblockpopup.component';
 
@NgModule({
  declarations: [
    AppComponent,
    BlockAccountComponent,
    CloseAccountComponent,
    DeleteAccountComponent,
    AdminEnquiriesComponent,
    AdminAccountClosureComponent,
    AdminHomeComponent,
    CreateCustomerComponent,
    OpenAccountComponent,
    AdminTransferComponent,
    AddBeneficiaryComponent,
    ForgotPasswordComponent,
    ForgotUseridComponent,
    LoginComponent,
    ResetPasswordComponent,
    AccountClosureComponent,
    AccountDetailsComponent,
    ViewAccountBalanceComponent,
    ChangePasswordComponent,
    ManageNotificationsComponent,
    SetupBudgetComponent,
    SetupProfileComponent,
    TransactionSummaryComponent,
    TransferComponent,
    UserDashboardComponent,
    HeaderComponent,
    SidenavComponent,
    TransferWithinMyAccountComponent,
    AdminSidenavComponent,
    TransferWithinMyBankComponent,
    DenyConfirmationComponent,
    UnblockaccountComponent,
    UnblockpopupComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatMenuModule,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    HttpClientModule,
    RouterModule,
    MatSnackBarModule,
    MatTableModule,
    MatExpansionModule,
    MatListModule,
    MatIconModule,
   
   
  ],
  providers: [AuthGuard],
  bootstrap: [AppComponent]
})
export class AppModule { }