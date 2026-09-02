import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RoleApiService } from '../../service/RoleApiService';
import { Permission, Role } from '../../model/dto/role.model';

@Component({
  selector: 'app-role-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center mb-3">
      <h4>{{ isEdit ? 'রোল সম্পাদনা' : 'নতুন রোল' }}</h4>
      <button class="btn btn-outline-secondary" (click)="goBack()">ফিরে যান</button>
    </div>

    <div class="card">
      <div class="card-body">
        <form (ngSubmit)="onSubmit()" #roleForm="ngForm">
          <div class="row">
            <div class="col-md-4 mb-3">
              <label class="form-label">কোড <span class="text-danger">*</span></label>
              <input class="form-control" [(ngModel)]="form.code" name="code" required [disabled]="isSystem" placeholder="WARD_CLERK">
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">বাংলা নাম <span class="text-danger">*</span></label>
              <input class="form-control" [(ngModel)]="form.nameBn" name="nameBn" required>
            </div>
            <div class="col-md-4 mb-3">
              <label class="form-label">ইংরেজি নাম <span class="text-danger">*</span></label>
              <input class="form-control" [(ngModel)]="form.nameEn" name="nameEn" required>
            </div>
          </div>
          <div class="mb-3">
            <label class="form-label">বর্ণনা</label>
            <textarea class="form-control" [(ngModel)]="form.description" name="description" rows="2"></textarea>
          </div>

          <h6 class="mt-3">পারমিশন</h6>
          <div *ngFor="let module of modules" class="mb-3">
            <div class="fw-semibold mb-1">{{ module }}</div>
            <div class="d-flex flex-wrap gap-3">
              <label class="form-check" *ngFor="let perm of permissionsByModule[module]">
                <input class="form-check-input" type="checkbox"
                       [checked]="selected.has(perm.code)"
                       (change)="toggle(perm.code)">
                {{ perm.action }}
              </label>
            </div>
          </div>

          <div *ngIf="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

          <button class="btn btn-primary" type="submit" [disabled]="roleForm.invalid || saving">
            {{ saving ? 'সংরক্ষণ হচ্ছে...' : 'সংরক্ষণ করুন' }}
          </button>
        </form>
      </div>
    </div>
  `
})
export class RoleFormComponent implements OnInit {
  isEdit = false;
  isSystem = false;
  editId: number | null = null;
  saving = false;
  errorMessage = '';
  form: Role = { code: '', nameBn: '', nameEn: '', description: '', status: true };
  permissions: Permission[] = [];
  selected = new Set<string>();
  modules: string[] = [];
  permissionsByModule: Record<string, Permission[]> = {};

  constructor(
    private service: RoleApiService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.service.getPermissions().subscribe({
      next: perms => {
        this.permissions = perms;
        this.permissionsByModule = {};
        perms.forEach(p => {
          (this.permissionsByModule[p.module] ||= []).push(p);
        });
        this.modules = Object.keys(this.permissionsByModule);
      }
    });
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.editId = +id;
      this.service.getById(this.editId).subscribe({
        next: role => {
          this.form = role;
          this.isSystem = !!role.isSystem;
          role.permissions?.forEach(p => this.selected.add(p.code));
        }
      });
    }
  }

  toggle(code: string) {
    if (this.selected.has(code)) {
      this.selected.delete(code);
    } else {
      this.selected.add(code);
    }
  }

  onSubmit() {
    if (this.selected.size === 0) {
      this.errorMessage = 'অন্তত একটি পারমিশন আবশ্যক';
      return;
    }
    this.saving = true;
    const payload = {
      code: this.form.code,
      nameBn: this.form.nameBn,
      nameEn: this.form.nameEn,
      description: this.form.description,
      status: this.form.status,
      permissionCodes: Array.from(this.selected)
    };
    const obs = this.editId
      ? this.service.update(this.editId, payload)
      : this.service.create(payload);
    obs.subscribe({
      next: () => this.router.navigate(['/roles']),
      error: err => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'সংরক্ষণে সমস্যা হয়েছে';
      }
    });
  }

  goBack() {
    this.router.navigate(['/roles']);
  }
}
