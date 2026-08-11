import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiService } from '../common/service/api.service';
import { ApiResponse } from '../common/model/api-response.model';
import { User } from '../model/dto/user.model';

@Injectable({ providedIn: 'root' })
export class UserApiService {
  private baseUrl = '/users';

  constructor(private api: ApiService) {}

  getAll(): Observable<User[]> {
    return this.api.get<ApiResponse<User[]>>(this.baseUrl).pipe(map(res => res.data));
  }

  getById(id: number): Observable<User> {
    return this.api.get<ApiResponse<User>>(`${this.baseUrl}/${id}`).pipe(map(res => res.data));
  }

  create(user: User): Observable<User> {
    return this.api.post<ApiResponse<User>>(this.baseUrl, user).pipe(map(res => res.data));
  }

  update(id: number, user: User): Observable<User> {
    return this.api.put<ApiResponse<User>>(`${this.baseUrl}/${id}`, user).pipe(map(res => res.data));
  }

  delete(id: number): Observable<void> {
    return this.api.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`).pipe(map(() => undefined));
  }
}
