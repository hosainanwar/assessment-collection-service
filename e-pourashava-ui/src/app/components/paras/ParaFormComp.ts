import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ParaApiService } from '../../service/ParaApiService';
import { WordApiService } from '../../service/WordApiService';
import { Para } from '../../model/dto/para.model';
import { Word } from '../../model/dto/word.model';

@Component({
  selector: 'app-para-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>{{ isEdit ? 'পাড়া সম্পাদনা' : 'নতুন পাড়া' }}</h4>
      <button class="btn btn-outline-secondary" (click)="goBack()">
        <i class="bi bi-arrow-left me-1"></i>ফিরে যান
      </button>
    </div>

    <div class="card">
      <div class="card-body">
        <form (ngSubmit)="onSubmit()" #paraForm="ngForm">
          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">পাড়ার নাম <span class="text-danger">*</span></label>
              <input type="text" 
                     class="form-control"
                     [(ngModel)]="formData.pbrName"
                     name="pbrName"
                     placeholder="পাড়ার নাম লিখুন"
                     required
                     #pbrNameField="ngModel">
              <div class="text-danger small" *ngIf="pbrNameField.invalid && pbrNameField.touched">
                <span *ngIf="pbrNameField.errors?.['required']">পাড়ার নাম আবশ্যক</span>
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
            <label class="form-label">ওয়ার্ড <span class="text-danger">*</span></label>
            <select class="form-select" 
                    [(ngModel)]="formData.wordId"
                    name="wordId"
                    required
                    #wordField="ngModel">
              <option [ngValue]="null">ওয়ার্ড নির্বাচন করুন</option>
              <option *ngFor="let w of allWords" [ngValue]="w.id">{{ w.wordName }}</option>
            </select>
            <div class="text-danger small" *ngIf="wordField.invalid && wordField.touched">
              <span *ngIf="wordField.errors?.['required']">ওয়ার্ড নির্বাচন করুন</span>
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
            <button type="submit" class="btn btn-primary" [disabled]="paraForm.invalid || saving">
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
export class ParaFormComponent implements OnInit {
  isEdit = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  allWords: Word[] = [];
  formData: Para = { pbrName: '', wordId: null as any, subdomain: 'sreepur', createdBy: '' };

  constructor(
    private paraService: ParaApiService,
    private wordService: WordApiService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.loadWords();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.editId = +id;
      this.loadPara();
    }
  }

  loadWords() {
    this.wordService.getAll().subscribe({
      next: (data) => this.allWords = data
    });
  }

  loadPara() {
    this.paraService.getById(this.editId!).subscribe({
      next: (data) => this.formData = data,
      error: (err) => this.errorMessage = err.error?.message || 'লোড করতে সমস্যা হয়েছে'
    });
  }

  onSubmit() {
    if (!this.formData.pbrName.trim() || !this.formData.wordId || !this.formData.subdomain.trim()) {
      this.errorMessage = 'পাড়ার নাম, ওয়ার্ড এবং সাবডোমেইন আবশ্যক';
      return;
    }

    this.saving = true;
    const obs = this.editId 
      ? this.paraService.update(this.editId, this.formData)
      : this.paraService.create(this.formData);

    obs.subscribe({
      next: () => {
        this.saving = false;
        this.router.navigate(['/paras']);
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'সংরক্ষণে সমস্যা হয়েছে';
      }
    });
  }

  goBack() {
    this.router.navigate(['/paras']);
  }
}
