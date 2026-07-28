import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../common/service/api.service';
import { ApiResponse } from '../common/model/api-response.model';
import { Pourashava } from '../model/dto/pourashava.model';

@Injectable({ providedIn: 'root' })
export class PourashavaApiService {
  private url = '/pourashavas';

  constructor(private api: ApiService) {}

  getAll(): Observable<Pourashava[]> {
    return this.api.get<ApiResponse<Pourashava[]>>(this.url)
      .pipe(map(res => res.data));
  }

  getById(id: number): Observable<Pourashava> {
    return this.api.get<ApiResponse<Pourashava>>(`${this.url}/${id}`)
      .pipe(map(res => res.data));
  }

  getBySubdomain(subdomain: string): Observable<Pourashava> {
    return this.api.get<ApiResponse<Pourashava>>(`${this.url}/by-subdomain/${subdomain}`)
      .pipe(map(res => res.data));
  }

  create(pourashava: Pourashava): Observable<Pourashava> {
    return this.api.post<ApiResponse<Pourashava>>(this.url, pourashava)
      .pipe(map(res => res.data));
  }

  update(id: number, pourashava: Pourashava): Observable<Pourashava> {
    return this.api.put<ApiResponse<Pourashava>>(`${this.url}/${id}`, pourashava)
      .pipe(map(res => res.data));
  }

  delete(id: number): Observable<void> {
    return this.api.delete<ApiResponse<void>>(`${this.url}/${id}`)
      .pipe(map(() => undefined));
  }
}
