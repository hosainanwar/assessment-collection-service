import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Word } from '../models/models';

@Injectable({ providedIn: 'root' })
export class WordApiService {
  private baseUrl = '/api/v1/words';

  constructor(private http: HttpClient) {}

  getAll(): Observable<ApiResponse<Word[]>> {
    return this.http.get<ApiResponse<Word[]>>(this.baseUrl);
  }

  getById(id: number): Observable<ApiResponse<Word>> {
    return this.http.get<ApiResponse<Word>>(`${this.baseUrl}/${id}`);
  }

  getBySubdomain(subdomain: string): Observable<ApiResponse<Word[]>> {
    return this.http.get<ApiResponse<Word[]>>(`${this.baseUrl}/by-subdomain/${subdomain}`);
  }

  search(wordName: string, subdomain: string, page: number = 0, size: number = 20): Observable<ApiResponse<any>> {
    let params: any = { page, size };
    if (wordName) params.wordName = wordName;
    if (subdomain) params.subdomain = subdomain;
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/search`, { params });
  }

  create(word: Word): Observable<ApiResponse<Word>> {
    return this.http.post<ApiResponse<Word>>(this.baseUrl, word);
  }

  update(id: number, word: Word): Observable<ApiResponse<Word>> {
    return this.http.put<ApiResponse<Word>>(`${this.baseUrl}/${id}`, word);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
