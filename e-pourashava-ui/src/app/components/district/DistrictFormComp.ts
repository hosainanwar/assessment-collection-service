import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { DistrictApiService } from '../../service/DistrictApiService';
import { DivisionApiService } from '../../service/DivisionApiService';
import { District } from '../../model/dto/district.model';
import { Division } from '../../model/dto/division.model';

@Component({
  selector: 'app-district-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>{{ isEdit ? 'জেলা সম্পাদনা' : 'নতুন জেলা' }}</h4>
      <button class="btn btn-outline-secondary" (click)="goBack()">
        <i class="bi bi-arrow-left me-1"></i>ফিরে যান
      </button>
    </div>

    <div class="card">
      <div class="card-body">
        <form (ngSubmit)="onSubmit()" #districtForm="ngForm">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">জেলার নাম <span class="text-danger">*</span></label>
              <input type="text" 
                     class="form-control"
                     [(ngModel)]="formData.name"
                     name="name"
                     placeholder="জেলার নাম লিখুন"
                     required
                     minlength="2"
                     #nameField="ngModel">
              <div class="text-danger small" *ngIf="nameField.invalid && nameField.touched">
                <span *ngIf="nameField.errors?.['required']">জেলার নাম আবশ্যক</span>
                <span *ngIf="nameField.errors?.['minlength']">জেলার নাম কমপক্ষে ২ অক্ষর হতে হবে</span>
              </div>
            </div>

            <div class="col-md-6 mb-3">
              <label class="form-label">ইংরেজি নাম <span class="text-danger">*</span></label>
              <input type="text" 
                     class="form-control"
                     [(ngModel)]="formData.enName"
                     name="enName"
                     placeholder="ইংরেজি নাম লিখুন"
                     required
                     minlength="2"
                     #enNameField="ngModel">
              <div class="text-danger small" *ngIf="enNameField.invalid && enNameField.touched">
                <span *ngIf="enNameField.errors?.['required']">ইংরেজি নাম আবশ্যক</span>
                <span *ngIf="enNameField.errors?.['minlength']">ইংরেজি নাম কমপক্ষে ২ অক্ষর হতে হবে</span>
              </div>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">বিভাগ <span class="text-danger">*</span></label>
            <select class="form-select" 
                    [(ngModel)]="formData.divisionId"
                    name="divisionId"
                    required
                    #divisionField="ngModel">
              <option [ngValue]="null">বিভাগ নির্বাচন করুন</option>
              <option *ngFor="let d of allDivisions" [ngValue]="d.id">{{ d.name }}</option>
            </select>
            <div class="text-danger small" *ngIf="divisionField.invalid && divisionField.touched">
              <span *ngIf="divisionField.errors?.['required']">বিভাগ নির্বাচন করুন</span>
            </div>
          </div>

          <div *ngIf="errorMessage" class="alert alert-danger">
            <i class="bi bi-exclamation-triangle me-2"></i>{{ errorMessage }}
          </div>

          <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary" [disabled]="districtForm.invalid || saving">
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
export class DistrictFormComponent implements OnInit {
  isEdit = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  allDivisions: Division[] = [];
  formData: District = { name: '', enName: '', divisionId: null as any };

  constructor(
    private service: DistrictApiService,
    private divisionService: DivisionApiService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.loadDivisions();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.editId = +id;
      this.loadDistrict();
    }
  }

  loadDivisions() {
    this.divisionService.getAll().subscribe({
      next: (data) => this.allDivisions = data
    });
  }

  loadDistrict() {
    this.service.getById(this.editId!).subscribe({
      next: (data) => this.formData = data,
      error: (err) => this.errorMessage = err.error?.message || 'লোড করতে সমস্যা হয়েছে'
    });
  }

  onSubmit() {
    if (!this.formData.name.trim() || !this.formData.enName.trim() || !this.formData.divisionId) {
      this.errorMessage = 'সব ক্ষেত্র আবশ্যক';
      return;
    }

    this.saving = true;
    const obs = this.editId 
      ? this.service.update(this.editId, this.formData)
      : this.service.create(this.formData);

    obs.subscribe({
      next: () => {
        this.saving = false;
        this.router.navigate(['/districts']);
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'সংরক্ষণে সমস্যা হয়েছে';
      }
    });
  }

  goBack() {
    this.router.navigate(['/districts']);
  }
}
