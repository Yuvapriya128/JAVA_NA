import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import EmployeeRequestDTO from '../dto/EmployeeRequestDTO';
import EmployeeResponseDTO from '../dto/EmployeeResponseDTO';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private readonly apiUrl = 'http://localhost:8080/api/employees';
  private readonly http = inject(HttpClient);

  getAll(): Observable<EmployeeResponseDTO[]> {
    return this.http.get<EmployeeResponseDTO[]>(this.apiUrl);
  }

  getById(id: number): Observable<EmployeeResponseDTO> {
    return this.http.get<EmployeeResponseDTO>(`${this.apiUrl}/${id}`);
  }

  add(employee: EmployeeRequestDTO): Observable<EmployeeResponseDTO> {
    return this.http.post<EmployeeResponseDTO>(this.apiUrl, employee);
  }

  update(id: number, employee: EmployeeRequestDTO): Observable<EmployeeResponseDTO> {
    return this.http.put<EmployeeResponseDTO>(`${this.apiUrl}/${id}`, employee);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

