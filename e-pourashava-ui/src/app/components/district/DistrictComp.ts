import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DistrictApiService } from '../../service/DistrictApiService';
import { DivisionApiService } from '../../service/DivisionApiService';
import { District } from '../../model/dto/district.model';
import { Division } from '../../model/dto/division.model';

@Component({
  selector: 'app-district',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>জেলা তালিকা</h4>
      <button class="btn btn-primary" (click)="openModal()">+ নতুন জেলা</button>
    </div>

    <div class="row mb-3">
      <div class="col-md-4">
        <select class="form-select" [(ngModel)]="filterDivisionId" (change)="loadFiltered()">
          <option value="">সব বিভাগ</option>
          <option *ngFor="let d of allDivisions" [value]="d.id">{{ d.name }}</option>
        </select>
      </div>
      <div class="col-md-4">
        <input type="text" class="form-control" placeholder="জেলা খুঁজুন..." [(ngModel)]="searchName" (keyup.enter)="loadFiltered()">
      </div>
      <div class="col-md-2">
        <button class="btn btn-outline-primary" (click)="loadFiltered()">খুঁজুন</button>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-striped table-hover">
        <thead class="table-dark">
          <tr><th>#</th><th>জেলার নাম</th><th>ইংরেজি নাম</th><th>বিভাগ</th><th>কার্যক্রম</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let d of districts; let i = index">
            <td>{{ i + 1 }}</td>
            <td>{{ d.name }}</td>
            <td>{{ d.enName }}</td>
            <td>{{ d.divisionName || '-' }}</td>
            <td>
              <button class="btn btn-sm btn-warning me-1" (click)="editModal(d)">সম্পাদনা</button>
              <button class="btn btn-sm btn-danger" (click)="deleteItem(d.id!)">মুছুন</button>
            </td>
          </tr>
          <tr *ngIf="districts.length === 0"><td colspan="5" class="text-center text-muted">কোনো জেলা পাওয়া যায়নি</td></tr>
        </tbody>
      </table>
    </div>

    <div class="modal fade" [class.show]="showModal" [style.display]="showModal ? 'block' : 'none'" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ editId ? 'জেলা সম্পাদনা' : 'নতুন জেলা' }}</h5>
            <button type="button" class="btn-close" (click)="closeModal()"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3"><label class="form-label">জেলার নাম *</label><input type="text" class="form-control" [(ngModel)]="formData.name"></div>
            <div class="mb-3"><label class="form-label">ইংরেজি নাম *</label><input type="text" class="form-control" [(ngModel)]="formData.enName"></div>
            <div class="mb-3"><label class="form-label">বিভাগ *</label>
              <select class="form-select" [(ngModel)]="formData.divisionId">
                <option [ngValue]="null">বিভাগ নির্বাচন করুন</option>
                <option *ngFor="let d of allDivisions" [ngValue]="d.id">{{ d.name }}</option>
              </select>
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
export class DistrictComp implements OnInit {
  districts: District[] = [];
  allDivisions: Division[] = [];
  showModal = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  searchName = '';
  filterDivisionId = '';
  formData: District = { name: '', enName: '', divisionId: null as any };

  constructor(private service: DistrictApiService, private divisionService: DivisionApiService) {}
  ngOnInit() { this.loadDivisions(); this.loadAll(); }

  loadDivisions() { this.divisionService.getAll().subscribe({ next: (data) => this.allDivisions = data }); }
  loadAll() { this.service.getAll().subscribe({ next: (data) => this.districts = data, error: (err) => this.errorMessage = err.error?.message }); }
  loadFiltered() {
    if (this.filterDivisionId) { this.service.getByDivisionId(+this.filterDivisionId).subscribe({ next: (data) => this.districts = data }); }
    else { this.loadAll(); }
  }
  openModal() { this.editId = null; this.formData = { name: '', enName: '', divisionId: null as any }; this.errorMessage = ''; this.showModal = true; }
  editModal(d: District) { this.editId = d.id!; this.formData = { ...d }; this.errorMessage = ''; this.showModal = true; }
  closeModal() { this.showModal = false; this.editId = null; }
  save() {
    if (!this.formData.name.trim() || !this.formData.enName.trim() || !this.formData.divisionId) { this.errorMessage = 'সব ক্ষেত্র আবশ্যক'; return; }
    this.saving = true;
    const obs = this.editId ? this.service.update(this.editId, this.formData) : this.service.create(this.formData);
    obs.subscribe({ next: () => { this.saving = false; this.closeModal(); this.loadAll(); }, error: (err) => { this.saving = false; this.errorMessage = err.error?.message; } });
  }
  deleteItem(id: number) {
    if (!confirm('আপনি কি এই জেলা মুছে ফেলতে চান?')) return;
    this.service.delete(id).subscribe({ next: () => this.loadAll(), error: (err) => alert(err.error?.message) });
  }
}
