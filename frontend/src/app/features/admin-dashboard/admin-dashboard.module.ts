import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { AdminDashboardLayoutComponent } from './layout/admin-dashboard-layout.component';
import { AdminOverviewComponent } from './overview/admin-overview.component';
import { AdminUsersComponent } from './users/admin-users.component';
import { AdminSellersComponent } from './sellers/admin-sellers.component';
import { AdminRfqsComponent } from './rfqs/admin-rfqs.component';
import { AdminQuotesComponent } from './quotes/admin-quotes.component';
import { AdminOrdersComponent } from './orders/admin-orders.component';
import { AdminPaymentsComponent } from './payments/admin-payments.component';
import { AdminDisputesComponent } from './disputes/admin-disputes.component';
import { AdminReviewsComponent } from './reviews/admin-reviews.component';
import { AdminNotificationsComponent } from './notifications/admin-notifications.component';

const routes: Routes = [
  {
    path: '',
    component: AdminDashboardLayoutComponent,
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', component: AdminOverviewComponent },
      { path: 'users', component: AdminUsersComponent },
      { path: 'sellers', component: AdminSellersComponent },
      { path: 'rfqs', component: AdminRfqsComponent },
      { path: 'quotes', component: AdminQuotesComponent },
      { path: 'orders', component: AdminOrdersComponent },
      { path: 'payments', component: AdminPaymentsComponent },
      { path: 'disputes', component: AdminDisputesComponent },
      { path: 'reviews', component: AdminReviewsComponent },
      { path: 'notifications', component: AdminNotificationsComponent }
    ]
  }
];

@NgModule({
  declarations: [
    AdminDashboardLayoutComponent,
    AdminOverviewComponent,
    AdminUsersComponent,
    AdminSellersComponent,
    AdminRfqsComponent,
    AdminQuotesComponent,
    AdminOrdersComponent,
    AdminPaymentsComponent,
    AdminDisputesComponent,
    AdminReviewsComponent,
    AdminNotificationsComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forChild(routes)
  ]
})
export class AdminDashboardModule {}
