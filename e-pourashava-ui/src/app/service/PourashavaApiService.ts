import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../common/service/api.service';
import { ApiResponse } from '../common/model/api-response.model';
import { Pourashava } from '../model/dto/pourashava.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class PourashavaApiService {
  private baseUrl = '/pourashavas';

  constructor(private api: ApiService, private authService: AuthService) {}

  getAll(): Observable<Pourashava[]> {
    const subdomain = this.authService.getSubdomain();
    return this.api.get<ApiResponse<Pourashava[]>>(this.baseUrl, { subdomain }).pipe(map(res => res.data));
  }

  getById(id: number): Observable<Pourashava> {
    return this.api.get<ApiResponse<Pourashava>>(`${this.baseUrl}/${id}`).pipe(map(res => res.data));
  }

  getBySubdomain(subdomain: string): Observable<Pourashava> {
    return this.api.get<ApiResponse<Pourashava>>(`${this.baseUrl}/by-subdomain/${subdomain}`).pipe(map(res => res.data));
  }

  getByDistrictId(districtId: number): Observable<Pourashava[]> {
    return this.api.get<ApiResponse<Pourashava[]>>(`${this.baseUrl}/by-district/${districtId}`).pipe(map(res => res.data));
  }

  getByDivisionId(divisionId: number): Observable<Pourashava[]> {
    return this.api.get<ApiResponse<Pourashava[]>>(`${this.baseUrl}/by-division/${divisionId}`).pipe(map(res => res.data));
  }

  create(pourashava: Pourashava): Observable<Pourashava> {
    return this.api.post<ApiResponse<Pourashava>>(this.baseUrl, pourashava).pipe(map(res => res.data));
  }

  update(id: number, pourashava: Pourashava): Observable<Pourashava> {
    return this.api.put<ApiResponse<Pourashava>>(`${this.baseUrl}/${id}`, pourashava).pipe(map(res => res.data));
  }

  delete(id: number): Observable<void> {
    return this.api.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => undefined));
  }
}
