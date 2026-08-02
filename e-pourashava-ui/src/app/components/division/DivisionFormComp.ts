import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { DivisionApiService } from '../../service/DivisionApiService';
import { Division } from '../../model/dto/division.model';

@Component({
  selector: 'app-division-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>{{ isEdit ? 'বিভাগ সম্পাদনা' : 'নতুন বিভাগ' }}</h4>
      <button class="btn btn-outline-secondary" (click)="goBack()">
        <i class="bi bi-arrow-left me-1"></i>ফিরে যান
      </button>
    </div>

    <div class="card">
      <div class="card-body">
        <form (ngSubmit)="onSubmit()" #divisionForm="ngForm">
          <div class="mb-3">
            <label class="form-label">বিভাগের নাম <span class="text-danger">*</span></label>
            <input type="text" 
                   class="form-control"
                   [(ngModel)]="formData.name"
                   name="name"
                   placeholder="বিভাগের নাম লিখুন"
                   required
                   minlength="2"
                   #nameField="ngModel">
            <div class="text-danger small" *ngIf="nameField.invalid && nameField.touched">
              <span *ngIf="nameField.errors?.['required']">বিভাগের নাম আবশ্যক</span>
              <span *ngIf="nameField.errors?.['minlength']">বিভাগের নাম কমপক্ষে ২ অক্ষর হতে হবে</span>
            </div>
          </div>

          <div *ngIf="errorMessage" class="alert alert-danger">
            <i class="bi bi-exclamation-triangle me-2"></i>{{ errorMessage }}
          </div>

          <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary" [disabled]="divisionForm.invalid || saving">
              <span *ngIf="saving" class="spinner-border spinner-border-sm me-1"></span>
              <i *ngIf="!saving" class="bi bi-check-lg me-1"></i>
              {{ saving ? 'সংরক্ষণ হচ্ছে...' : 'সংরক্ষণ করুন' }}
            </button>
            <button type="button" class="btn btn-outline-secondary" (click)="goBack()">বাতিল</button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class DivisionFormComponent implements OnInit {
  isEdit = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  formData: Division = { name: '' };

  constructor(
    private service: DivisionApiService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.editId = +id;
      this.loadDivision();
    }
  }

  loadDivision() {
    this.service.getById(this.editId!).subscribe({
      next: (data) => this.formData = data,
      error: (err) => this.errorMessage = err.error?.message || 'লোড করতে সমস্যা হয়েছে'
    });
  }

  onSubmit() {
    if (!this.formData.name.trim()) {
      this.errorMessage = 'বিভাগের নাম আবশ্যক';
      return;
    }

    this.saving = true;
    const obs = this.editId 
      ? this.service.update(this.editId, this.formData)
      : this.service.create(this.formData);

    obs.subscribe({
      next: () => {
        this.saving = false;
        this.router.navigate(['/divisions']);
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'সংরক্ষণে সমস্যা হয়েছে';
      }
    });
  }

  goBack() {
    this.router.navigate(['/divisions']);
  }
}
