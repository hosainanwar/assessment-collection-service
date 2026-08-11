import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../common/service/api.service';
import { ApiResponse } from '../common/model/api-response.model';
import { PouroshovaInfo } from '../model/dto/pouroshova-info.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class PouroshovaInfoApiService {
  private baseUrl = '/pouroshova-infos';

  constructor(private api: ApiService, private authService: AuthService) {}

  getAll(): Observable<PouroshovaInfo[]> {
    const subdomain = this.authService.getSubdomain();
    return this.api.get<ApiResponse<PouroshovaInfo[]>>(this.baseUrl, { subdomain }).pipe(map(res => res.data));
  }

  getById(id: number): Observable<PouroshovaInfo> {
    return this.api.get<ApiResponse<PouroshovaInfo>>(`${this.baseUrl}/${id}`).pipe(map(res => res.data));
  }

  getBySubdomain(subdomain: string): Observable<PouroshovaInfo> {
    return this.api.get<ApiResponse<PouroshovaInfo>>(`${this.baseUrl}/by-subdomain/${subdomain}`).pipe(map(res => res.data));
  }

  create(info: PouroshovaInfo): Observable<PouroshovaInfo> {
    return this.api.post<ApiResponse<PouroshovaInfo>>(this.baseUrl, info).pipe(map(res => res.data));
  }

  update(id: number, info: PouroshovaInfo): Observable<PouroshovaInfo> {
    return this.api.put<ApiResponse<PouroshovaInfo>>(`${this.baseUrl}/${id}`, info).pipe(map(res => res.data));
  }

  delete(id: number): Observable<void> {
    return this.api.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => undefined));
  }
}
