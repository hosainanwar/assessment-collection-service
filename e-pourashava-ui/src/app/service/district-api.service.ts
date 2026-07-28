import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../common/service/api.service';
import { ApiResponse } from '../common/model/api-response.model';
import { District } from '../model/dto/district.model';

@Injectable({ providedIn: 'root' })
export class DistrictApiService {
  private url = '/districts';

  constructor(private api: ApiService) {}

  getAll(): Observable<District[]> {
    return this.api.get<ApiResponse<District[]>>(this.url)
      .pipe(map(res => res.data));
  }

  getById(id: number): Observable<District> {
    return this.api.get<ApiResponse<District>>(`${this.url}/${id}`)
      .pipe(map(res => res.data));
  }

  getByDivisionId(divisionId: number): Observable<District[]> {
    return this.api.get<ApiResponse<District[]>>(`${this.url}/by-division/${divisionId}`)
      .pipe(map(res => res.data));
  }

  create(district: District): Observable<District> {
    return this.api.post<ApiResponse<District>>(this.url, district)
      .pipe(map(res => res.data));
  }

  update(id: number, district: District): Observable<District> {
    return this.api.put<ApiResponse<District>>(`${this.url}/${id}`, district)
      .pipe(map(res => res.data));
  }

  delete(id: number): Observable<void> {
    return this.api.delete<ApiResponse<void>>(`${this.url}/${id}`)
      .pipe(map(() => undefined));
  }
}
