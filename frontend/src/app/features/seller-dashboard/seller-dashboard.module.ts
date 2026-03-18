import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { SellerOverviewComponent } from './overview/seller-overview.component';
import { RfqManagementComponent } from './rfq-management/rfq-management.component';
import { QuoteManagementComponent } from './quote-management/quote-management.component';
import { OrdersManagementComponent } from './orders-management/orders-management.component';
import { SellerEarningsComponent } from './earnings/seller-earnings.component';
import { SellerSettingsComponent } from './settings/seller-settings.component';
import { SellerDashboardLayoutComponent } from './layout/seller-dashboard-layout.component';

const routes: Routes = [
  {
    path: '',
    component: SellerDashboardLayoutComponent,
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', component: SellerOverviewComponent },
      { path: 'rfqs', component: RfqManagementComponent },
      { path: 'quotes', component: QuoteManagementComponent },
      { path: 'orders', component: OrdersManagementComponent },
      { path: 'earnings', component: SellerEarningsComponent },
      { path: 'settings', component: SellerSettingsComponent }
    ]
  }
];

@NgModule({
  declarations: [
    SellerDashboardLayoutComponent,
    SellerOverviewComponent,
    RfqManagementComponent,
    QuoteManagementComponent,
    OrdersManagementComponent,
    SellerEarningsComponent,
    SellerSettingsComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forChild(routes)
  ]
})
export class SellerDashboardModule {}
