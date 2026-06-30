// Shared API types mirroring the backend contract (ApiResponse envelope,
// pagination, and the resource shapes used by the portals).

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  correlationId?: string;
  timestamp?: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInSeconds: number;
}

export interface CurrentUser {
  username: string;
  tenantId: number;
  authorities: string[];
}

export interface Patient {
  id: number;
  mrn: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  email?: string;
  phone?: string;
  status: string;
}

export interface CreatePatientInput {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  email?: string;
  phone?: string;
}

export interface Provider {
  id: number;
  code: string;
  firstName: string;
  lastName: string;
  specialty?: string;
  status: string;
}

export interface Appointment {
  id: number;
  patientId: number;
  providerId: number;
  scheduledStart: string;
  scheduledEnd: string;
  status: string;
  reason?: string;
}
