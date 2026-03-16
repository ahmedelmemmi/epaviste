import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { BuyerDashboardComponent } from './dashboard/buyer-dashboard.component';
import { RfqCreateComponent } from './rfq-create/rfq-create.component';
import { QuoteCompareComponent } from './quote-compare/quote-compare.component';

const routes: Routes = [
  { path: 'dashboard', component: BuyerDashboardComponent },
  { path: 'rfq/new', component: RfqCreateComponent },
  { path: 'rfq/:id/quotes', component: QuoteCompareComponent }
];

@NgModule({
  declarations: [BuyerDashboardComponent, RfqCreateComponent, QuoteCompareComponent],
  imports: [CommonModule, ReactiveFormsModule, RouterModule.forChild(routes)]
})
export class BuyerModule {}
