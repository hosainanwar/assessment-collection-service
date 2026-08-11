import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { UserApiService } from '../../service/UserApiService';
import { User } from '../../model/dto/user.model';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>{{ isEdit ? 'ব্যবহারকারী সম্পাদনা' : 'নতুন ব্যবহারকারী' }}</h4>
      <button class="btn btn-outline-secondary" (click)="goBack()">
        <i class="bi bi-arrow-left me-1"></i>ফিরে যান
      </button>
    </div>

    <div class="card">
      <div class="card-body">
        <form (ngSubmit)="onSubmit()" #userForm="ngForm">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">নাম <span class="text-danger">*</span></label>
              <input type="text" class="form-control" [(ngModel)]="formData.name" name="name" required #nameField="ngModel">
              <div class="text-danger small" *ngIf="nameField.invalid && nameField.touched">নাম আবশ্যক</div>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">ইউজারনাম <span class="text-danger">*</span></label>
              <input type="text" class="form-control" [(ngModel)]="formData.username" name="username" required [disabled]="isEdit" #usernameField="ngModel">
              <div class="text-danger small" *ngIf="usernameField.invalid && usernameField.touched">ইউজারনাম আবশ্যক</div>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">ইমেইল <span class="text-danger">*</span></label>
              <input type="email" class="form-control" [(ngModel)]="formData.email" name="email" required email #emailField="ngModel">
              <div class="text-danger small" *ngIf="emailField.invalid && emailField.touched">সঠিক ইমেইল দিন</div>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">{{ isEdit ? 'পাসওয়ার্ড (খালি রাখুন পরিবর্তন না করতে)' : 'পাসওয়ার্ড' }} <span *ngIf="!isEdit" class="text-danger">*</span></label>
              <input type="password" class="form-control" [(ngModel)]="formData.password" name="password" [required]="!isEdit" #passwordField="ngModel">
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">সাবডোমেইন <span class="text-danger">*</span></label>
              <input type="text" class="form-control" [(ngModel)]="formData.subdomain" name="subdomain" required placeholder="যেমন: sreepur" #subdomainField="ngModel">
              <div class="text-danger small" *ngIf="subdomainField.invalid && subdomainField.touched">সাবডোমেইন আবশ্যক</div>
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">রোল</label>
              <select class="form-select" [(ngModel)]="formData.role" name="role">
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
                <option value="SUPER_ADMIN">SUPER_ADMIN</option>
              </select>
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">বিভাগ</label>
              <input type="text" class="form-control" [(ngModel)]="formData.department" name="department">
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">পদবী</label>
              <input type="text" class="form-control" [(ngModel)]="formData.designation" name="designation">
            </div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">মোবাইল নম্বর</label>
              <input type="text" class="form-control" [(ngModel)]="formData.mobileNo" name="mobileNo">
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">স্ট্যাটাস</label>
              <select class="form-select" [(ngModel)]="formData.status" name="status">
                [ngValue]="true"
                <option [ngValue]="true">সক্রিয়</option>
                <option [ngValue]="false">নিষ্ক্রিয়</option>
              </select>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">ঠিকানা</label>
            <textarea class="form-control" [(ngModel)]="formData.address" name="address" rows="2"></textarea>
          </div>

          <div *ngIf="errorMessage" class="alert alert-danger">
            <i class="bi bi-exclamation-triangle me-2"></i>{{ errorMessage }}
          </div>

          <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary" [disabled]="userForm.invalid || saving">
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
export class UserFormComponent implements OnInit {
  isEdit = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  formData: User = { 
    name: '', 
    username: '', 
    email: '', 
    password: '',
    subdomain: '',
    role: 'USER',
    status: true
  };

  constructor(
    private service: UserApiService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.editId = +id;
      this.loadUser();
    }
  }

  loadUser() {
    this.service.getById(this.editId!).subscribe({
      next: (data) => {
        this.formData = { ...data, password: '' };
      },
      error: (err) => this.errorMessage = err.error?.message || 'লোড করতে সমস্যা হয়েছে'
    });
  }

  onSubmit() {
    if (!this.formData.name?.trim() || !this.formData.username?.trim() || 
        !this.formData.email?.trim() || !this.formData.subdomain?.trim()) {
      this.errorMessage = 'সব ক্ষেত্র আবশ্যক';
      return;
    }

    if (!this.isEdit && !this.formData.password) {
      this.errorMessage = 'পাসওয়ার্ড আবশ্যক';
      return;
    }

    this.saving = true;
    const obs = this.editId 
      ? this.service.update(this.editId, this.formData)
      : this.service.create(this.formData);

    obs.subscribe({
      next: () => {
        this.saving = false;
        this.router.navigate(['/users']);
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'সংরক্ষণে সমস্যা হয়েছে';
      }
    });
  }

  goBack() {
    this.router.navigate(['/users']);
  }
}
