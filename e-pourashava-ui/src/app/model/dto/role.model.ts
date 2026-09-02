export interface Permission {
  id?: number;
  code: string;
  module: string;
  action: string;
  description?: string;
}

export interface Role {
  id?: number;
  code: string;
  nameBn: string;
  nameEn: string;
  description?: string;
  isSystem?: boolean;
  status?: boolean;
  permissions?: Permission[];
  permissionCodes?: string[];
}
