import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../common/service/api.service';
import { ApiResponse } from '../common/model/api-response.model';
import { Division } from '../model/dto/division.model';

@Injectable({ providedIn: 'root' })
export class DivisionApiService {
  private url = '/divisions';

  constructor(private api: ApiService) {}

  getAll(): Observable<Division[]> {
    return this.api.get<ApiResponse<Division[]>>(this.url)
      .pipe(map(res => res.data));
  }

  getById(id: number): Observable<Division> {
    return this.api.get<ApiResponse<Division>>(`${this.url}/${id}`)
      .pipe(map(res => res.data));
  }

  create(division: Division): Observable<Division> {
    return this.api.post<ApiResponse<Division>>(this.url, division)
      .pipe(map(res => res.data));
  }

  update(id: number, division: Division): Observable<Division> {
    return this.api.put<ApiResponse<Division>>(`${this.url}/${id}`, division)
      .pipe(map(res => res.data));
  }

  delete(id: number): Observable<void> {
    return this.api.delete<ApiResponse<void>>(`${this.url}/${id}`)
      .pipe(map(() => undefined));
  }
}
