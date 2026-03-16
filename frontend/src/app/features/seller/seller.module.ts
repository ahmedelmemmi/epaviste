import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { SellerDashboardComponent } from './dashboard/seller-dashboard.component';
import { RfqListComponent } from './rfq-list/rfq-list.component';

const routes: Routes = [
  { path: 'dashboard', component: SellerDashboardComponent },
  { path: 'rfqs', component: RfqListComponent }
];

@NgModule({
  declarations: [SellerDashboardComponent, RfqListComponent],
  imports: [CommonModule, ReactiveFormsModule, RouterModule.forChild(routes)]
})
export class SellerModule {}
