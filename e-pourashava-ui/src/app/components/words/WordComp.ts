import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WordApiService } from '../../service/WordApiService';
import { Word } from '../../model/dto/word.model';

@Component({
  selector: 'app-words',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>ওয়ার্ড তালিকা</h4>
      <button class="btn btn-primary" (click)="openModal()">+ নতুন ওয়ার্ড</button>
    </div>

    <div class="row mb-3">
      <div class="col-md-4">
        <input type="text" class="form-control" placeholder="সাবডোমেইন দিয়ে খুঁজুন..."
               [(ngModel)]="searchSubdomain" (keyup.enter)="loadBySubdomain()">
      </div>
      <div class="col-md-2">
        <button class="btn btn-outline-primary" (click)="loadBySubdomain()">খুঁজুন</button>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-striped table-hover">
        <thead class="table-dark">
          <tr>
            <th>#</th>
            <th>ওয়ার্ডের নাম</th>
            <th>সাবডোমেইন</th>
            <th>তৈরি করেছেন</th>
            <th>কার্যক্রম</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let word of words; let i = index">
            <td>{{ i + 1 }}</td>
            <td>{{ word.wordName }}</td>
            <td>{{ word.subdomain }}</td>
            <td>{{ word.createdBy || '-' }}</td>
            <td>
              <button class="btn btn-sm btn-warning me-1" (click)="editModal(word)">সম্পাদনা</button>
              <button class="btn btn-sm btn-danger" (click)="deleteWord(word.id!)">মুছুন</button>
            </td>
          </tr>
          <tr *ngIf="words.length === 0">
            <td colspan="5" class="text-center text-muted">কোনো ওয়ার্ড পাওয়া যায়নি</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="modal fade" [class.show]="showModal" [style.display]="showModal ? 'block' : 'none'" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ editId ? 'ওয়ার্ড সম্পাদনা' : 'নতুন ওয়ার্ড' }}</h5>
            <button type="button" class="btn-close" (click)="closeModal()"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">ওয়ার্ডের নাম *</label>
              <input type="text" class="form-control" [(ngModel)]="formData.wordName" placeholder="ওয়ার্ডের নাম লিখুন">
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
            <button type="button" class="btn btn-primary" (click)="saveWord()" [disabled]="saving">
              {{ saving ? 'সংরক্ষণ হচ্ছে...' : 'সংরক্ষণ করুন' }}
            </button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal-backdrop fade show" *ngIf="showModal" (click)="closeModal()"></div>
  `,
  styles: [`.modal.show { z-index: 1050; }`]
})
export class WordComp implements OnInit {
  words: Word[] = [];
  showModal = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  searchSubdomain = '';
  formData: Word = { wordName: '', subdomain: '', createdBy: '' };

  constructor(private wordService: WordApiService) {}

  ngOnInit() { this.loadAll(); }

  loadAll() {
    this.wordService.getAll().subscribe({
      next: (data) => this.words = data,
      error: (err) => this.errorMessage = err.error?.message || 'লোড করতে সমস্যা হয়েছে'
    });
  }

  loadBySubdomain() {
    if (this.searchSubdomain.trim()) {
      this.wordService.getBySubdomain(this.searchSubdomain).subscribe({
        next: (data) => this.words = data,
        error: (err) => this.errorMessage = err.error?.message || 'লোড করতে সমস্যা হয়েছে'
      });
    } else {
      this.loadAll();
    }
  }

  openModal() {
    this.editId = null;
    this.formData = { wordName: '', subdomain: 'sreepur', createdBy: '' };
    this.errorMessage = '';
    this.showModal = true;
  }

  editModal(word: Word) {
    this.editId = word.id!;
    this.formData = { ...word };
    this.errorMessage = '';
    this.showModal = true;
  }

  closeModal() { this.showModal = false; this.editId = null; }

  saveWord() {
    if (!this.formData.wordName.trim() || !this.formData.subdomain.trim()) {
      this.errorMessage = 'ওয়ার্ডের নাম এবং সাবডোমেইন আবশ্যক';
      return;
    }
    this.saving = true;
    const obs = this.editId
      ? this.wordService.update(this.editId, this.formData)
      : this.wordService.create(this.formData);
    obs.subscribe({
      next: () => { this.saving = false; this.closeModal(); this.loadAll(); },
      error: (err) => { this.saving = false; this.errorMessage = err.error?.message || 'সংরক্ষণে সমস্যা হয়েছে'; }
    });
  }

  deleteWord(id: number) {
    if (!confirm('আপনি কি এই ওয়ার্ড মুছে ফেলতে চান?')) return;
    this.wordService.delete(id).subscribe({
      next: () => this.loadAll(),
      error: (err) => alert(err.error?.message || 'মুছে ফেলায় সমস্যা')
    });
  }
}
