import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PouroshovaInfoApiService } from '../../service/PouroshovaInfoApiService';
import { PouroshovaInfo } from '../../model/dto/pouroshova-info.model';

@Component({
  selector: 'app-pouroshova-info',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>পৌরসভা তথ্য</h4>
      <button class="btn btn-primary" (click)="openModal()">+ নতুন তথ্য</button>
    </div>

    <div class="row mb-3">
      <div class="col-md-4">
        <input type="text" class="form-control" placeholder="সাবডোমেইন দিয়ে খুঁজুন..." [(ngModel)]="searchSubdomain" (keyup.enter)="loadBySubdomain()">
      </div>
      <div class="col-md-2">
        <button class="btn btn-outline-primary" (click)="loadBySubdomain()">খুঁজুন</button>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-striped table-hover">
        <thead class="table-dark">
          <tr><th>#</th><th>পৌরসভার নাম</th><th>মেয়র</th><th>থানা</th><th>জেলা</th><th>সাবডোমেইন</th><th>কার্যক্রম</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let info of infos; let i = index">
            <td>{{ i + 1 }}</td>
            <td>{{ info.pouroshovaName }}</td>
            <td>{{ info.meyorName }}</td>
            <td>{{ info.psName }}</td>
            <td>{{ info.dsName }}</td>
            <td>{{ info.subdomain }}</td>
            <td>
              <button class="btn btn-sm btn-warning me-1" (click)="editModal(info)">সম্পাদনা</button>
              <button class="btn btn-sm btn-danger" (click)="deleteItem(info.id!)">মুছুন</button>
            </td>
          </tr>
          <tr *ngIf="infos.length === 0"><td colspan="7" class="text-center text-muted">কোনো তথ্য পাওয়া যায়নি</td></tr>
        </tbody>
      </table>
    </div>

    <div class="modal fade" [class.show]="showModal" [style.display]="showModal ? 'block' : 'none'" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ editId ? 'তথ্য সম্পাদনা' : 'নতুন তথ্য' }}</h5>
            <button type="button" class="btn-close" (click)="closeModal()"></button>
          </div>
          <div class="modal-body">
            <div class="row">
              <div class="col-md-6 mb-3"><label class="form-label">পৌরসভার নাম *</label><input type="text" class="form-control" [(ngModel)]="formData.pouroshovaName"></div>
              <div class="col-md-6 mb-3"><label class="form-label">মেয়রের নাম *</label><input type="text" class="form-control" [(ngModel)]="formData.meyorName"></div>
            </div>
            <div class="row">
              <div class="col-md-4 mb-3"><label class="form-label">থানা *</label><input type="text" class="form-control" [(ngModel)]="formData.psName"></div>
              <div class="col-md-4 mb-3"><label class="form-label">জেলা *</label><input type="text" class="form-control" [(ngModel)]="formData.dsName"></div>
              <div class="col-md-4 mb-3"><label class="form-label">সাবডোমেইন *</label><input type="text" class="form-control" [(ngModel)]="formData.subdomain"></div>
            </div>
            <div class="row">
              <div class="col-md-6 mb-3"><label class="form-label">স্বাক্ষরের নাম *</label><input type="text" class="form-control" [(ngModel)]="formData.signatureName"></div>
              <div class="col-md-6 mb-3"><label class="form-label">কর নির্ধারকের নাম</label><input type="text" class="form-control" [(ngModel)]="formData.korNirdharokName"></div>
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
export class PouroshovaInfoComp implements OnInit {
  infos: PouroshovaInfo[] = [];
  showModal = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  searchSubdomain = '';
  formData: PouroshovaInfo = { pouroshovaName: '', meyorName: '', psName: '', dsName: '', signatureName: '', subdomain: '' };

  constructor(private service: PouroshovaInfoApiService) {}
  ngOnInit() { this.loadAll(); }

  loadAll() {
    this.service.getAll().subscribe({
      next: (data) => this.infos = data,
      error: (err) => this.errorMessage = err.error?.message
    });
  }

  loadBySubdomain() {
    if (this.searchSubdomain.trim()) {
      this.service.getBySubdomain(this.searchSubdomain).subscribe({ next: (data) => this.infos = data ? [data] : [], error: (err) => this.errorMessage = err.error?.message });
    } else {
      this.loadAll();
    }
  }

  openModal() { this.editId = null; this.formData = { pouroshovaName: '', meyorName: '', psName: '', dsName: '', signatureName: '', subdomain: '' }; this.errorMessage = ''; this.showModal = true; }
  editModal(info: PouroshovaInfo) { this.editId = info.id!; this.formData = { ...info }; this.errorMessage = ''; this.showModal = true; }
  closeModal() { this.showModal = false; this.editId = null; }
  save() {
    if (!this.formData.pouroshovaName.trim() || !this.formData.meyorName.trim() || !this.formData.subdomain.trim()) { this.errorMessage = 'প্রয়োজনীয় ক্ষেত্র খালি রাখা যাবে না'; return; }
    this.saving = true;
    const obs = this.editId ? this.service.update(this.editId, this.formData) : this.service.create(this.formData);
    obs.subscribe({ next: () => { this.saving = false; this.closeModal(); }, error: (err) => { this.saving = false; this.errorMessage = err.error?.message; } });
  }
  deleteItem(id: number) {
    if (!confirm('আপনি কি এই তথ্য মুছে ফেলতে চান?')) return;
    this.service.delete(id).subscribe({ next: () => this.infos = this.infos.filter(i => i.id !== id), error: (err) => alert(err.error?.message) });
  }
}
