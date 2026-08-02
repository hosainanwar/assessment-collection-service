import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { WordApiService } from '../../service/WordApiService';
import { Word } from '../../model/dto/word.model';

@Component({
  selector: 'app-word-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>{{ isEdit ? 'ওয়ার্ড সম্পাদনা' : 'নতুন ওয়ার্ড' }}</h4>
      <button class="btn btn-outline-secondary" (click)="goBack()">
        <i class="bi bi-arrow-left me-1"></i>ফিরে যান
      </button>
    </div>

    <div class="card">
      <div class="card-body">
        <form (ngSubmit)="onSubmit()" #wordForm="ngForm">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">ওয়ার্ডের নাম <span class="text-danger">*</span></label>
              <input type="text" 
                     class="form-control"
                     [(ngModel)]="formData.wordName"
                     name="wordName"
                     placeholder="ওয়ার্ডের নাম লিখুন"
                     required
                     #wordNameField="ngModel">
              <div class="text-danger small" *ngIf="wordNameField.invalid && wordNameField.touched">
                <span *ngIf="wordNameField.errors?.['required']">ওয়ার্ডের নাম আবশ্যক</span>
              </div>
            </div>

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
          </div>

          <div class="mb-3">
            <label class="form-label">তৈরি করেছেন</label>
            <input type="text" 
                   class="form-control"
                   [(ngModel)]="formData.createdBy"
                   name="createdBy"
                   placeholder="তৈরি করেছেন">
          </div>

          <div *ngIf="errorMessage" class="alert alert-danger">
            <i class="bi bi-exclamation-triangle me-2"></i>{{ errorMessage }}
          </div>

          <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary" [disabled]="wordForm.invalid || saving">
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
export class WordFormComponent implements OnInit {
  isEdit = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  formData: Word = { wordName: '', subdomain: '', createdBy: '' };

  constructor(
    private service: WordApiService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.editId = +id;
      this.loadWord();
    }
  }

  loadWord() {
    this.service.getById(this.editId!).subscribe({
      next: (data) => this.formData = data,
      error: (err) => this.errorMessage = err.error?.message || 'লোড করতে সমস্যা হয়েছে'
    });
  }

  onSubmit() {
    if (!this.formData.wordName.trim() || !this.formData.subdomain.trim()) {
      this.errorMessage = 'ওয়ার্ডের নাম এবং সাবডোমেইন আবশ্যক';
      return;
    }

    this.saving = true;
    const obs = this.editId 
      ? this.service.update(this.editId, this.formData)
      : this.service.create(this.formData);

    obs.subscribe({
      next: () => {
        this.saving = false;
        this.router.navigate(['/words']);
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'সংরক্ষণে সমস্যা হয়েছে';
      }
    });
  }

  goBack() {
    this.router.navigate(['/words']);
  }
}
