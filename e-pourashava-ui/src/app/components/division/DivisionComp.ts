import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DivisionApiService } from '../../service/DivisionApiService';
import { Division } from '../../model/dto/division.model';

@Component({
  selector: 'app-division',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>বিভাগ তালিকা</h4>
      <button class="btn btn-primary" (click)="openModal()">+ নতুন বিভাগ</button>
    </div>

    <div class="row mb-3">
      <div class="col-md-4">
        <input type="text" class="form-control" placeholder="বিভাগ খুঁজুন..." [(ngModel)]="searchName" (keyup.enter)="search()">
      </div>
      <div class="col-md-2">
        <button class="btn btn-outline-primary" (click)="search()">খুঁজুন</button>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-striped table-hover">
        <thead class="table-dark">
          <tr><th>#</th><th>বিভাগের নাম</th><th>কার্যক্রম</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let d of divisions; let i = index">
            <td>{{ i + 1 }}</td>
            <td>{{ d.name }}</td>
            <td>
              <button class="btn btn-sm btn-warning me-1" (click)="editModal(d)">সম্পাদনা</button>
              <button class="btn btn-sm btn-danger" (click)="deleteItem(d.id!)">মুছুন</button>
            </td>
          </tr>
          <tr *ngIf="divisions.length === 0"><td colspan="3" class="text-center text-muted">কোনো বিভাগ পাওয়া যায়নি</td></tr>
        </tbody>
      </table>
    </div>

    <div class="modal fade" [class.show]="showModal" [style.display]="showModal ? 'block' : 'none'" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ editId ? 'বিভাগ সম্পাদনা' : 'নতুন বিভাগ' }}</h5>
            <button type="button" class="btn-close" (click)="closeModal()"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">বিভাগের নাম *</label>
              <input type="text" class="form-control" [(ngModel)]="formData.name" placeholder="বিভাগের নাম লিখুন">
            </div>
            <div *ngIf="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" (click)="closeModal()">বাতিল</button>
            <button type="button" class="btn btn-primary" (click)="save()" [disabled]="saving">{{ saving ? 'সংরক্ষণ হচ্ছে...' : 'সংরক্ষণ করুন' }}</button>
          </div>
        </div>
      </div>
    </div>
    <div class="modal-backdrop fade show" *ngIf="showModal" (click)="closeModal()"></div>
  `,
  styles: [`.modal.show { z-index: 1050; }`]
})
export class DivisionComp implements OnInit {
  divisions: Division[] = [];
  showModal = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  searchName = '';
  formData: Division = { name: '' };

  constructor(private service: DivisionApiService) {}
  ngOnInit() { this.loadAll(); }

  loadAll() { this.service.getAll().subscribe({ next: (data) => this.divisions = data, error: (err) => this.errorMessage = err.error?.message }); }
  search() { if (this.searchName.trim()) { this.service.search(this.searchName).subscribe({ next: (data) => this.divisions = data }); } else { this.loadAll(); } }
  openModal() { this.editId = null; this.formData = { name: '' }; this.errorMessage = ''; this.showModal = true; }
  editModal(d: Division) { this.editId = d.id!; this.formData = { ...d }; this.errorMessage = ''; this.showModal = true; }
  closeModal() { this.showModal = false; this.editId = null; }
  save() {
    if (!this.formData.name.trim()) { this.errorMessage = 'বিভাগের নাম আবশ্যক'; return; }
    this.saving = true;
    const obs = this.editId ? this.service.update(this.editId, this.formData) : this.service.create(this.formData);
    obs.subscribe({ next: () => { this.saving = false; this.closeModal(); this.loadAll(); }, error: (err) => { this.saving = false; this.errorMessage = err.error?.message; } });
  }
  deleteItem(id: number) {
    if (!confirm('আপনি কি এই বিভাগ মুছে ফেলতে চান?')) return;
    this.service.delete(id).subscribe({ next: () => this.loadAll(), error: (err) => alert(err.error?.message) });
  }
}
