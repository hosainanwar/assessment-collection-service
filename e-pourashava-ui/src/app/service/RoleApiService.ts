import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../common/service/api.service';
import { ApiResponse } from '../common/model/api-response.model';
import { Permission, Role } from '../model/dto/role.model';

@Injectable({ providedIn: 'root' })
export class RoleApiService {
  private baseUrl = '/roles';

  constructor(private api: ApiService) {}

  getAll(): Observable<Role[]> {
    return this.api.get<ApiResponse<Role[]>>(this.baseUrl).pipe(map(res => res.data));
  }

  getAssignable(): Observable<Role[]> {
    return this.api.get<ApiResponse<Role[]>>(`${this.baseUrl}/assignable`).pipe(map(res => res.data));
  }

  getById(id: number): Observable<Role> {
    return this.api.get<ApiResponse<Role>>(`${this.baseUrl}/${id}`).pipe(map(res => res.data));
  }

  getPermissions(): Observable<Permission[]> {
    return this.api.get<ApiResponse<Permission[]>>(`${this.baseUrl}/permissions`).pipe(map(res => res.data));
  }

  create(role: Partial<Role>): Observable<Role> {
    return this.api.post<ApiResponse<Role>>(this.baseUrl, role).pipe(map(res => res.data));
  }

  update(id: number, role: Partial<Role>): Observable<Role> {
    return this.api.put<ApiResponse<Role>>(`${this.baseUrl}/${id}`, role).pipe(map(res => res.data));
  }

  delete(id: number): Observable<void> {
    return this.api.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => undefined));
  }
}
