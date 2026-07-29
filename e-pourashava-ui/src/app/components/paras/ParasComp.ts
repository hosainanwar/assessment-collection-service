import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ParaApiService } from '../../service/para-api.service';
import { WordApiService } from '../../service/word-api.service';
import { Para } from '../../model/dto/para.model';
import { Word } from '../../model/dto/word.model';

@Component({
  selector: 'app-paras',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>পাড়া তালিকা</h4>
      <button class="btn btn-primary" (click)="openModal()">+ নতুন পাড়া</button>
    </div>

    <div class="row mb-3">
      <div class="col-md-4">
        <select class="form-select" [(ngModel)]="filterWordId" (change)="loadFiltered()">
          <option value="">সব ওয়ার্ড</option>
          <option *ngFor="let w of allWords" [value]="w.id">{{ w.wordName }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <input type="text" class="form-control" placeholder="সাবডোমেইন..." [(ngModel)]="filterSubdomain" (keyup.enter)="loadFiltered()">
      </div>
      <div class="col-md-2">
        <button class="btn btn-outline-primary" (click)="loadFiltered()">খুঁজুন</button>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-striped table-hover">
        <thead class="table-dark">
          <tr>
            <th>#</th>
            <th>পাড়ার নাম</th>
            <th>ওয়ার্ড</th>
            <th>সাবডোমেইন</th>
            <th>তৈরি করেছেন</th>
            <th>কার্যক্রম</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let para of paras; let i = index">
            <td>{{ i + 1 }}</td>
            <td>{{ para.pbrName }}</td>
            <td>{{ para.wordName || '-' }}</td>
            <td>{{ para.subdomain }}</td>
            <td>{{ para.createdBy || '-' }}</td>
            <td>
              <button class="btn btn-sm btn-warning me-1" (click)="editModal(para)">সম্পাদনা</button>
              <button class="btn btn-sm btn-danger" (click)="deletePara(para.id!)">মুছুন</button>
            </td>
          </tr>
          <tr *ngIf="paras.length === 0">
            <td colspan="6" class="text-center text-muted">কোনো পাড়া পাওয়া যায়নি</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="modal fade" [class.show]="showModal" [style.display]="showModal ? 'block' : 'none'" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ editId ? 'পাড়া সম্পাদনা' : 'নতুন পাড়া' }}</h5>
            <button type="button" class="btn-close" (click)="closeModal()"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">পাড়ার নাম *</label>
              <input type="text" class="form-control" [(ngModel)]="formData.pbrName" placeholder="পাড়ার নাম লিখুন">
            </div>
            <div class="mb-3">
              <label class="form-label">ওয়ার্ড *</label>
              <select class="form-select" [(ngModel)]="formData.wordId">
                <option [ngValue]="null">ওয়ার্ড নির্বাচন করুন</option>
                <option *ngFor="let w of allWords" [ngValue]="w.id">{{ w.wordName }}</option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">সাবডোমেইন *</label>
              <input type="text" class="form-control" [(ngModel)]="formData.subdomain" placeholder="সাবডোমেইন লিখুন">
            </div>
            <div class="mb-3">
              <label class="form-label">তৈরি করেছেন</label>
              <input type="text" class="form-control" [(ngModel)]="formData.createdBy" placeholder="তৈরি করেছেন">
            </div>
            <div *ngIf="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="closeModal()">বাতিল</button>
            <button type="button" class="btn btn-primary" (click)="savePara()" [disabled]="saving">
              {{ saving ? 'সংরক্ষণ হচ্ছে...' : 'সংরক্ষণ করুন' }}
            </button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal-backdrop fade show" *ngIf="showModal" (click)="closeModal()"></div>
  `,
  styles: [`
    .modal.show { z-index: 1050; }
  `]
})
export class ParasComp implements OnInit {
  paras: Para[] = [];
  allWords: Word[] = [];
  showModal = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  filterWordId = '';
  filterSubdomain = '';

  formData: Para = { pbrName: '', wordId: null as any, subdomain: 'sreepur', createdBy: '' };

  constructor(
    private paraService: ParaApiService,
    private wordService: WordApiService
  ) {}

  ngOnInit() {
    this.loadWords();
    this.loadAll();
  }

  loadWords() {
    this.wordService.getAll().subscribe(res => this.allWords = res);
  }

  loadAll() {
    this.paraService.getAll().subscribe(res => this.paras = res);
  }

  loadFiltered() {
    if (this.filterWordId && this.filterSubdomain) {
      this.paraService.getByWordIdAndSubdomain(+this.filterWordId, this.filterSubdomain).subscribe(res => this.paras = res);
    } else if (this.filterWordId) {
      this.paraService.getByWordId(+this.filterWordId).subscribe(res => this.paras = res);
    } else if (this.filterSubdomain) {
      this.paraService.getBySubdomain(this.filterSubdomain).subscribe(res => this.paras = res);
    } else {
      this.loadAll();
    }
  }

  openModal() {
    this.editId = null;
    this.formData = { pbrName: '', wordId: null as any, subdomain: 'sreepur', createdBy: '' };
    this.errorMessage = '';
    this.showModal = true;
  }

  editModal(para: Para) {
    this.editId = para.id!;
    this.formData = { ...para };
    this.errorMessage = '';
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.editId = null;
  }

  savePara() {
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
        this.closeModal();
        this.loadAll();
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'সংরক্ষণে সমস্যা হয়েছে';
      }
    });
  }

  deletePara(id: number) {
    if (!confirm('আপনি কি এই পাড়া মুছে ফেলতে চান?')) return;
    this.paraService.delete(id).subscribe({
      next: () => this.loadAll(),
      error: (err) => alert(err.error?.message || 'মুছে ফেলায় সমস্যা')
    });
  }
}
