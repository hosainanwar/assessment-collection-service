import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Para } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ParaApiService {
  private baseUrl = '/api/v1/paras';

  constructor(private http: HttpClient) {}

  getAll(): Observable<ApiResponse<Para[]>> {
    return this.http.get<ApiResponse<Para[]>>(this.baseUrl);
  }

  getById(id: number): Observable<ApiResponse<Para>> {
    return this.http.get<ApiResponse<Para>>(`${this.baseUrl}/${id}`);
  }

  getByWordId(wordId: number): Observable<ApiResponse<Para[]>> {
    return this.http.get<ApiResponse<Para[]>>(`${this.baseUrl}/by-word/${wordId}`);
  }

  getBySubdomain(subdomain: string): Observable<ApiResponse<Para[]>> {
    return this.http.get<ApiResponse<Para[]>>(`${this.baseUrl}/by-subdomain/${subdomain}`);
  }

  getByWordAndSubdomain(wordId: number, subdomain: string): Observable<ApiResponse<Para[]>> {
    return this.http.get<ApiResponse<Para[]>>(`${this.baseUrl}/by-word/${wordId}/subdomain/${subdomain}`);
  }

  create(para: Para): Observable<ApiResponse<Para>> {
    return this.http.post<ApiResponse<Para>>(this.baseUrl, para);
  }

  update(id: number, para: Para): Observable<ApiResponse<Para>> {
    return this.http.put<ApiResponse<Para>>(`${this.baseUrl}/${id}`, para);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
