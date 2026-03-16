import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { OrderTrackingComponent } from './order-tracking/order-tracking.component';

const routes: Routes = [
  { path: 'tracking', component: OrderTrackingComponent }
];

@NgModule({
  declarations: [OrderTrackingComponent],
  imports: [CommonModule, RouterModule.forChild(routes)]
})
export class OrdersModule {}
