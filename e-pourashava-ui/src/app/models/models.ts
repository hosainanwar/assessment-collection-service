export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface Word {
  id?: number;
  wordName: string;
  createdBy?: string;
  subdomain: string;
}

export interface Para {
  id?: number;
  pbrName: string;
  wordId: number;
  wordName?: string;
  createdBy?: string;
  subdomain: string;
}
