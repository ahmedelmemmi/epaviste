import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { AuthGuard } from './core/guards/auth.guard';
import { BuyerGuard } from './core/guards/buyer.guard';
import { SellerGuard } from './core/guards/seller.guard';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'auth', loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule) },
  {
    path: 'buyer',
    loadChildren: () => import('./features/buyer/buyer.module').then(m => m.BuyerModule),
    canActivate: [AuthGuard, BuyerGuard]
  },
  {
    path: 'seller',
    loadChildren: () => import('./features/seller/seller.module').then(m => m.SellerModule),
    canActivate: [AuthGuard, SellerGuard]
  },
  {
    path: 'seller-dashboard',
    loadChildren: () => import('./features/seller-dashboard/seller-dashboard.module').then(m => m.SellerDashboardModule),
    canActivate: [AuthGuard, SellerGuard]
  },
  {
    path: 'orders',
    loadChildren: () => import('./features/orders/orders.module').then(m => m.OrdersModule),
    canActivate: [AuthGuard]
  },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
