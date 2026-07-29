import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PourashavaApiService } from '../../service/PourashavaApiService';
import { DivisionApiService } from '../../service/DivisionApiService';
import { DistrictApiService } from '../../service/DistrictApiService';
import { Pourashava } from '../../model/dto/pourashava.model';
import { Division } from '../../model/dto/division.model';
import { District } from '../../model/dto/district.model';

@Component({
  selector: 'app-pourashava',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>পৌরসভা তালিকা</h4>
      <button class="btn btn-primary" (click)="openModal()">+ নতুন পৌরসভা</button>
    </div>

    <div class="table-responsive">
      <table class="table table-striped table-hover">
        <thead class="table-dark">
          <tr><th>#</th><th>বাংলা নাম</th><th>ইংরেজি নাম</th><th>সাবডোমেইন</th><th>বিভাগ</th><th>জেলা</th><th>কার্যক্রম</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let p of pourashavas; let i = index">
            <td>{{ i + 1 }}</td>
            <td>{{ p.bnName }}</td>
            <td>{{ p.enName }}</td>
            <td>{{ p.subdomain }}</td>
            <td>{{ p.divisionName || '-' }}</td>
            <td>{{ p.districtName || '-' }}</td>
            <td>
              <button class="btn btn-sm btn-warning me-1" (click)="editModal(p)">সম্পাদনা</button>
              <button class="btn btn-sm btn-danger" (click)="deleteItem(p.id!)">মুছুন</button>
            </td>
          </tr>
          <tr *ngIf="pourashavas.length === 0"><td colspan="7" class="text-center text-muted">কোনো পৌরসভা পাওয়া যায়নি</td></tr>
        </tbody>
      </table>
    </div>

    <div class="modal fade" [class.show]="showModal" [style.display]="showModal ? 'block' : 'none'" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ editId ? 'পৌরসভা সম্পাদনা' : 'নতুন পৌরসভা' }}</h5>
            <button type="button" class="btn-close" (click)="closeModal()"></button>
          </div>
          <div class="modal-body">
            <div class="row">
              <div class="col-md-6 mb-3"><label class="form-label">বাংলা নাম *</label><input type="text" class="form-control" [(ngModel)]="formData.bnName"></div>
              <div class="col-md-6 mb-3"><label class="form-label">ইংরেজি নাম *</label><input type="text" class="form-control" [(ngModel)]="formData.enName"></div>
            </div>
            <div class="row">
              <div class="col-md-6 mb-3"><label class="form-label">সাবডোমেইন *</label><input type="text" class="form-control" [(ngModel)]="formData.subdomain"></div>
              <div class="col-md-6 mb-3"><label class="form-label">IP *</label><input type="text" class="form-control" [(ngModel)]="formData.ipAddress"></div>
            </div>
            <div class="row">
              <div class="col-md-6 mb-3"><label class="form-label">বিভাগ *</label>
                <select class="form-select" [(ngModel)]="formData.divisionId" (change)="onDivisionChange()">
                  <option [ngValue]="null">নির্বাচন করুন</option>
                  <option *ngFor="let d of allDivisions" [ngValue]="d.id">{{ d.name }}</option>
                </select>
              </div>
              <div class="col-md-6 mb-3"><label class="form-label">জেলা *</label>
                <select class="form-select" [(ngModel)]="formData.districtId">
                  <option [ngValue]="null">নির্বাচন করুন</option>
                  <option *ngFor="let d of filteredDistricts" [ngValue]="d.id">{{ d.name }}</option>
                </select>
              </div>
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
export class PourashavaComp implements OnInit {
  pourashavas: Pourashava[] = [];
  allDivisions: Division[] = [];
  allDistricts: District[] = [];
  filteredDistricts: District[] = [];
  showModal = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  formData: Pourashava = { bnName: '', enName: '', subdomain: '', divisionId: null as any, districtId: null as any };

  constructor(private service: PourashavaApiService, private divisionService: DivisionApiService, private districtService: DistrictApiService) {}
  ngOnInit() { this.loadDivisions(); this.loadAll(); }

  loadDivisions() { this.divisionService.getAll().subscribe({ next: (data) => this.allDivisions = data }); }
  loadDistricts() { this.districtService.getAll().subscribe({ next: (data) => this.allDistricts = data }); }
  loadAll() { this.service.getAll().subscribe({ next: (data) => this.pourashavas = data, error: (err) => this.errorMessage = err.error?.message }); }

  onDivisionChange() {
    if (this.formData.divisionId) {
      this.districtService.getByDivisionId(this.formData.divisionId).subscribe({ next: (data) => this.filteredDistricts = data });
    } else {
      this.filteredDistricts = [];
    }
  }

  openModal() { this.editId = null; this.formData = { bnName: '', enName: '', subdomain: '', divisionId: null as any, districtId: null as any }; this.errorMessage = ''; this.showModal = true; this.loadDistricts(); }
  editModal(p: Pourashava) { this.editId = p.id!; this.formData = { ...p }; this.errorMessage = ''; this.showModal = true; this.loadDistricts(); if (p.divisionId) { this.onDivisionChange(); } }
  closeModal() { this.showModal = false; this.editId = null; }
  save() {
    if (!this.formData.bnName.trim() || !this.formData.enName.trim() || !this.formData.subdomain.trim() || !this.formData.divisionId || !this.formData.districtId) { this.errorMessage = 'সব ক্ষেত্র আবশ্যক'; return; }
    this.saving = true;
    const obs = this.editId ? this.service.update(this.editId, this.formData) : this.service.create(this.formData);
    obs.subscribe({ next: () => { this.saving = false; this.closeModal(); this.loadAll(); }, error: (err) => { this.saving = false; this.errorMessage = err.error?.message; } });
  }
  deleteItem(id: number) {
    if (!confirm('আপনি কি এই পৌরসভা মুছে ফেলতে চান?')) return;
    this.service.delete(id).subscribe({ next: () => this.loadAll(), error: (err) => alert(err.error?.message) });
  }
}
