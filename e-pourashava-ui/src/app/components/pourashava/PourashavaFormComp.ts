import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PourashavaApiService } from '../../service/PourashavaApiService';
import { DivisionApiService } from '../../service/DivisionApiService';
import { DistrictApiService } from '../../service/DistrictApiService';
import { Pourashava } from '../../model/dto/pourashava.model';
import { Division } from '../../model/dto/division.model';
import { District } from '../../model/dto/district.model';

@Component({
  selector: 'app-pourashava-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>{{ isEdit ? 'পৌরসভা সম্পাদনা' : 'নতুন পৌরসভা' }}</h4>
      <button class="btn btn-outline-secondary" (click)="goBack()">
        <i class="bi bi-arrow-left me-1"></i>ফিরে যান
      </button>
    </div>

    <div class="card">
      <div class="card-body">
        <form (ngSubmit)="onSubmit()" #pourashavaForm="ngForm">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">বাংলা নাম <span class="text-danger">*</span></label>
              <input type="text" 
                     class="form-control"
                     [(ngModel)]="formData.bnName"
                     name="bnName"
                     placeholder="বাংলা নাম লিখুন"
                     required
                     #bnNameField="ngModel">
              <div class="text-danger small" *ngIf="bnNameField.invalid && bnNameField.touched">
                <span *ngIf="bnNameField.errors?.['required']">বাংলা নাম আবশ্যক</span>
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
                     #enNameField="ngModel">
              <div class="text-danger small" *ngIf="enNameField.invalid && enNameField.touched">
                <span *ngIf="enNameField.errors?.['required']">ইংরেজি নাম আবশ্যক</span>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">সাবডোমেইন <span class="text-danger">*</span></label>
              <input type="text" 
                     class="form-control"
                     [(ngModel)]="formData.subdomain"
                     name="subdomain"
                     placeholder="সাবডোমেইন লিখুন"
                     required
                     #subdomainField="ngModel">
              <div class="text-danger small" *ngIf="subdomainField.invalid && subdomainField.touched">
                <span *ngIf="subdomainField.errors?.['required']">সাবডোমেইন আবশ্যক</span>
              </div>
            </div>

            <div class="col-md-6 mb-3">
              <label class="form-label">IP ঠিকানা <span class="text-danger">*</span></label>
              <input type="text" 
                     class="form-control"
                     [(ngModel)]="formData.ipAddress"
                     name="ipAddress"
                     placeholder="IP ঠিকানা লিখুন"
                     required
                     #ipField="ngModel">
              <div class="text-danger small" *ngIf="ipField.invalid && ipField.touched">
                <span *ngIf="ipField.errors?.['required']">IP ঠিকানা আবশ্যক</span>
              </div>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">বিভাগ <span class="text-danger">*</span></label>
              <select class="form-select" 
                      [(ngModel)]="formData.divisionId"
                      name="divisionId"
                      required
                      (change)="onDivisionChange()"
                      #divisionField="ngModel">
                <option [ngValue]="null">বিভাগ নির্বাচন করুন</option>
                <option *ngFor="let d of allDivisions" [ngValue]="d.id">{{ d.name }}</option>
              </select>
              <div class="text-danger small" *ngIf="divisionField.invalid && divisionField.touched">
                <span *ngIf="divisionField.errors?.['required']">বিভাগ নির্বাচন করুন</span>
              </div>
            </div>

            <div class="col-md-6 mb-3">
              <label class="form-label">জেলা <span class="text-danger">*</span></label>
              <select class="form-select" 
                      [(ngModel)]="formData.districtId"
                      name="districtId"
                      required
                      #districtField="ngModel">
                <option [ngValue]="null">জেলা নির্বাচন করুন</option>
                <option *ngFor="let d of filteredDistricts" [ngValue]="d.id">{{ d.name }}</option>
              </select>
              <div class="text-danger small" *ngIf="districtField.invalid && districtField.touched">
                <span *ngIf="districtField.errors?.['required']">জেলা নির্বাচন করুন</span>
              </div>
            </div>
          </div>

          <div *ngIf="errorMessage" class="alert alert-danger">
            <i class="bi bi-exclamation-triangle me-2"></i>{{ errorMessage }}
          </div>

          <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary" [disabled]="pourashavaForm.invalid || saving">
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
export class PourashavaFormComponent implements OnInit {
  isEdit = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  allDivisions: Division[] = [];
  allDistricts: District[] = [];
  filteredDistricts: District[] = [];
  formData: Pourashava = { 
    bnName: '', 
    enName: '', 
    subdomain: '', 
    divisionId: null as any, 
    districtId: null as any 
  };

  constructor(
    private service: PourashavaApiService,
    private divisionService: DivisionApiService,
    private districtService: DistrictApiService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.loadDivisions();
    this.loadDistricts();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.editId = +id;
      this.loadPourashava();
    }
  }

  loadDivisions() {
    this.divisionService.getAll().subscribe({
      next: (data) => this.allDivisions = data
    });
  }

  loadDistricts() {
    this.districtService.getAll().subscribe({
      next: (data) => this.allDistricts = data
    });
  }

  loadPourashava() {
    this.service.getById(this.editId!).subscribe({
      next: (data) => {
        this.formData = data;
        this.onDivisionChange();
      },
      error: (err) => this.errorMessage = err.error?.message || 'লোড করতে সমস্যা হয়েছে'
    });
  }

  onDivisionChange() {
    if (this.formData.divisionId) {
      this.filteredDistricts = this.allDistricts.filter(d => d.divisionId === +this.formData.divisionId);
    } else {
      this.filteredDistricts = [];
    }
  }

  onSubmit() {
    if (!this.formData.bnName.trim() || !this.formData.enName.trim() || 
        !this.formData.subdomain.trim() || !this.formData.divisionId || !this.formData.districtId) {
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
        this.router.navigate(['/pourashavas']);
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'সংরক্ষণে সমস্যা হয়েছে';
      }
    });
  }

  goBack() {
    this.router.navigate(['/pourashavas']);
  }
}
