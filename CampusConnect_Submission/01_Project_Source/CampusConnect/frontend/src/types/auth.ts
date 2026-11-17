export type Role = 'ROLE_ADMIN' | 'ROLE_FACULTY' | 'ROLE_STUDENT';

export interface User {
  userId: number;
  personId: number;
  username: string;
  fullName: string;
  email: string;
  role: Role;
  roleSpecificCode?: string;
  departmentName?: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  userId: number;
  personId: number;
  username: string;
  fullName: string;
  email: string;
  role: Role;
}
