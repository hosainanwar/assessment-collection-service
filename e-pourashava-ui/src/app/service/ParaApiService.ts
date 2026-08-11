import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../common/service/api.service';
import { ApiResponse } from '../common/model/api-response.model';
import { Para } from '../model/dto/para.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class ParaApiService {
  private baseUrl = '/paras';

  constructor(private api: ApiService, private authService: AuthService) {}

  getAll(): Observable<Para[]> {
    const subdomain = this.authService.getSubdomain();
    return this.api.get<ApiResponse<Para[]>>(this.baseUrl, { subdomain }).pipe(map(res => res.data));
  }

  getById(id: number): Observable<Para> {
    return this.api.get<ApiResponse<Para>>(`${this.baseUrl}/${id}`).pipe(map(res => res.data));
  }

  getByWordId(wordId: number): Observable<Para[]> {
    return this.api.get<ApiResponse<Para[]>>(`${this.baseUrl}/by-word/${wordId}`).pipe(map(res => res.data));
  }

  getBySubdomain(subdomain: string): Observable<Para[]> {
    return this.api.get<ApiResponse<Para[]>>(`${this.baseUrl}/by-subdomain/${subdomain}`).pipe(map(res => res.data));
  }

  getByWordIdAndSubdomain(wordId: number, subdomain: string): Observable<Para[]> {
    return this.api.get<ApiResponse<Para[]>>(`${this.baseUrl}/by-word/${wordId}/subdomain/${subdomain}`).pipe(map(res => res.data));
  }

  create(para: Para): Observable<Para> {
    return this.api.post<ApiResponse<Para>>(this.baseUrl, para).pipe(map(res => res.data));
  }

  update(id: number, para: Para): Observable<Para> {
    return this.api.put<ApiResponse<Para>>(`${this.baseUrl}/${id}`, para).pipe(map(res => res.data));
  }

  delete(id: number): Observable<void> {
    return this.api.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => undefined));
  }
}
