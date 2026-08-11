import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../common/service/api.service';
import { ApiResponse } from '../common/model/api-response.model';
import { Word } from '../model/dto/word.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class WordApiService {
  private baseUrl = '/words';

  constructor(private api: ApiService, private authService: AuthService) {}

  getAll(): Observable<Word[]> {
    const subdomain = this.authService.getSubdomain();
    return this.api.get<ApiResponse<Word[]>>(this.baseUrl, { subdomain }).pipe(map(res => res.data));
  }

  getById(id: number): Observable<Word> {
    return this.api.get<ApiResponse<Word>>(`${this.baseUrl}/${id}`).pipe(map(res => res.data));
  }

  getBySubdomain(subdomain: string): Observable<Word[]> {
    return this.api.get<ApiResponse<Word[]>>(`${this.baseUrl}/by-subdomain/${subdomain}`).pipe(map(res => res.data));
  }

  search(wordName: string, subdomain: string): Observable<Word[]> {
    return this.api.get<ApiResponse<Word[]>>(`${this.baseUrl}/search`, { wordName, subdomain }).pipe(map(res => res.data));
  }

  create(word: Word): Observable<Word> {
    return this.api.post<ApiResponse<Word>>(this.baseUrl, word).pipe(map(res => res.data));
  }

  update(id: number, word: Word): Observable<Word> {
    return this.api.put<ApiResponse<Word>>(`${this.baseUrl}/${id}`, word).pipe(map(res => res.data));
  }

  delete(id: number): Observable<void> {
    return this.api.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => undefined));
  }
}
