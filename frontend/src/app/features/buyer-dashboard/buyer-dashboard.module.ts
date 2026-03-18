import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { BuyerDashboardLayoutComponent } from './layout/buyer-dashboard-layout.component';
import { BuyerOverviewComponent } from './overview/buyer-overview.component';
import { BuyerRfqManagementComponent } from './rfq-management/rfq-management.component';
import { BuyerQuoteComparisonComponent } from './quote-comparison/quote-comparison.component';
import { BuyerOrdersComponent } from './orders/buyer-orders.component';
import { BuyerReviewsComponent } from './reviews/buyer-reviews.component';
import { BuyerNotificationsComponent } from './notifications/buyer-notifications.component';
import { BuyerSettingsComponent } from './settings/buyer-settings.component';
import { SellerProfileCardComponent } from './seller-profile-card/seller-profile-card.component';

const routes: Routes = [
  {
    path: '',
    component: BuyerDashboardLayoutComponent,
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', component: BuyerOverviewComponent },
      { path: 'rfqs', component: BuyerRfqManagementComponent },
      { path: 'quotes', component: BuyerQuoteComparisonComponent },
      { path: 'orders', component: BuyerOrdersComponent },
      { path: 'reviews', component: BuyerReviewsComponent },
      { path: 'notifications', component: BuyerNotificationsComponent },
      { path: 'settings', component: BuyerSettingsComponent }
    ]
  }
];

@NgModule({
  declarations: [
    BuyerDashboardLayoutComponent,
    BuyerOverviewComponent,
    BuyerRfqManagementComponent,
    BuyerQuoteComparisonComponent,
    BuyerOrdersComponent,
    BuyerReviewsComponent,
    BuyerNotificationsComponent,
    BuyerSettingsComponent,
    SellerProfileCardComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forChild(routes)
  ]
})
export class BuyerDashboardModule {}
