import { apiClient } from './client';
import type {
  ApiResponse,
  Appointment,
  CreatePatientInput,
  CurrentUser,
  PageResponse,
  Patient,
  Provider,
  TokenResponse,
} from '@/types';

// Thin typed wrappers over the backend REST endpoints. Each unwraps the
// ApiResponse envelope and returns the payload.

export async function login(username: string, password: string): Promise<TokenResponse> {
  const { data } = await apiClient.post<ApiResponse<TokenResponse>>('/v1/auth/login', {
    username,
    password,
  });
  return data.data;
}

export async function fetchCurrentUser(): Promise<CurrentUser> {
  const { data } = await apiClient.get<ApiResponse<CurrentUser>>('/v1/auth/me');
  return data.data;
}

export async function fetchPatients(query: string, page: number): Promise<PageResponse<Patient>> {
  const { data } = await apiClient.get<ApiResponse<PageResponse<Patient>>>('/v1/patients', {
    params: { q: query || undefined, page, size: 10 },
  });
  return data.data;
}

export async function createPatient(input: CreatePatientInput): Promise<Patient> {
  const { data } = await apiClient.post<ApiResponse<Patient>>('/v1/patients', input);
  return data.data;
}

export async function fetchProviders(): Promise<PageResponse<Provider>> {
  const { data } = await apiClient.get<ApiResponse<PageResponse<Provider>>>('/v1/providers', {
    params: { page: 0, size: 50 },
  });
  return data.data;
}

export async function fetchAppointments(page: number): Promise<PageResponse<Appointment>> {
  const { data } = await apiClient.get<ApiResponse<PageResponse<Appointment>>>('/v1/appointments', {
    params: { page, size: 10 },
  });
  return data.data;
}

export interface BookAppointmentInput {
  patientId: number;
  providerId: number;
  scheduledStart: string;
  scheduledEnd: string;
  reason?: string;
}

export async function bookAppointment(input: BookAppointmentInput): Promise<Appointment> {
  const { data } = await apiClient.post<ApiResponse<Appointment>>('/v1/appointments', input);
  return data.data;
}
