import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { SellerService } from '../../../core/services/seller.service';
import { SellerProfile } from '../../../core/models/seller.model';

@Component({
  standalone: false,
  selector: 'app-seller-settings',
  templateUrl: './seller-settings.component.html'
})
export class SellerSettingsComponent implements OnInit {
  profile: SellerProfile | null = null;
  settingsForm!: FormGroup;
  loading = true;
  saving = false;
  errorMessage = '';
  successMessage = '';

  constructor(private sellerService: SellerService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.sellerService.getProfile().subscribe({
      next: (data) => {
        this.profile = data;
        this.settingsForm = this.fb.group({
          companyName: [data.companyName || ''],
          address: [data.address || ''],
          description: [data.description || ''],
          phone: [data.phone || ''],
          deliveryZones: [data.deliveryZones || ''],
          shippingMethods: [data.shippingMethods || '']
        });
        this.loading = false;
      },
      error: () => {
        this.settingsForm = this.fb.group({
          companyName: [''],
          address: [''],
          description: [''],
          phone: [''],
          deliveryZones: [''],
          shippingMethods: ['']
        });
        this.loading = false;
      }
    });
  }

  saveProfile(): void {
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.sellerService.updateProfile(this.settingsForm.value).subscribe({
      next: (updated) => {
        this.profile = updated;
        this.successMessage = 'Profile updated successfully!';
        this.saving = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to save profile.';
        this.saving = false;
      }
    });
  }
}
