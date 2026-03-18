import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BuyerService } from '../../../core/services/buyer.service';
import { BuyerProfile, Vehicle, VehicleRequest } from '../../../core/models/buyer.model';

@Component({
  standalone: false,
  selector: 'app-buyer-settings',
  templateUrl: './buyer-settings.component.html'
})
export class BuyerSettingsComponent implements OnInit {
  profile: BuyerProfile | null = null;
  vehicles: Vehicle[] = [];
  profileForm!: FormGroup;
  vehicleForm!: FormGroup;
  loading = true;
  saving = false;
  addingVehicle = false;
  errorMessage = '';
  successMessage = '';
  showVehicleForm = false;

  constructor(private buyerService: BuyerService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.vehicleForm = this.fb.group({
      brand: ['', Validators.required],
      model: ['', Validators.required],
      year: ['', [Validators.required, Validators.min(1900), Validators.max(new Date().getFullYear() + 1)]],
      vin: ['']
    });
    this.loadProfile();
    this.loadVehicles();
  }

  loadProfile(): void {
    this.buyerService.getProfile().subscribe({
      next: (data) => {
        this.profile = data;
        this.profileForm = this.fb.group({
          name: [data.name || '', Validators.required],
          phone: [data.phone || '']
        });
        this.loading = false;
      },
      error: () => {
        this.profileForm = this.fb.group({ name: ['', Validators.required], phone: [''] });
        this.loading = false;
      }
    });
  }

  loadVehicles(): void {
    this.buyerService.getVehicles().subscribe({
      next: (data) => { this.vehicles = data; },
      error: () => {}
    });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) return;
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.buyerService.updateProfile(this.profileForm.value).subscribe({
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

  addVehicle(): void {
    if (this.vehicleForm.invalid) return;
    this.addingVehicle = true;
    const req: VehicleRequest = this.vehicleForm.value;
    this.buyerService.addVehicle(req).subscribe({
      next: (v) => {
        this.vehicles.unshift(v);
        this.vehicleForm.reset();
        this.showVehicleForm = false;
        this.addingVehicle = false;
        this.successMessage = 'Vehicle added to your garage!';
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to add vehicle.';
        this.addingVehicle = false;
      }
    });
  }

  deleteVehicle(id: number): void {
    if (!confirm('Remove this vehicle from your garage?')) return;
    this.buyerService.deleteVehicle(id).subscribe({
      next: () => {
        this.vehicles = this.vehicles.filter(v => v.id !== id);
        this.successMessage = 'Vehicle removed.';
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to remove vehicle.';
      }
    });
  }
}
