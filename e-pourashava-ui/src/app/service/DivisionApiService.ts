import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../common/service/api.service';
import { ApiResponse } from '../common/model/api-response.model';
import { Division } from '../model/dto/division.model';

@Injectable({ providedIn: 'root' })
export class DivisionApiService {
  private baseUrl = '/divisions';

  constructor(private api: ApiService) {}

  getAll(): Observable<Division[]> {
    return this.api.get<ApiResponse<Division[]>>(this.baseUrl).pipe(map(res => res.data));
  }

  getById(id: number): Observable<Division> {
    return this.api.get<ApiResponse<Division>>(`${this.baseUrl}/${id}`).pipe(map(res => res.data));
  }

  search(name: string): Observable<Division[]> {
    return this.api.get<ApiResponse<Division[]>>(`${this.baseUrl}/search`, { name }).pipe(map(res => res.data));
  }

  create(division: Division): Observable<Division> {
    return this.api.post<ApiResponse<Division>>(this.baseUrl, division).pipe(map(res => res.data));
  }

  update(id: number, division: Division): Observable<Division> {
    return this.api.put<ApiResponse<Division>>(`${this.baseUrl}/${id}`, division).pipe(map(res => res.data));
  }

  delete(id: number): Observable<void> {
    return this.api.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => undefined));
  }
}
