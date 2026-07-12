import {inject, Injectable, Service, signal, WritableSignal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import EmployeeResponseDTO from '../dto/EmployeeResponseDTO';
import EmployeeRequestDTO from '../dto/EmployeeRequestDTO';

@Injectable({
  providedIn: 'root'
})
export class EmployeeServices {
  private apiUrl = 'http://localhost:8080/api/employees';
  private http:HttpClient=inject(HttpClient);
 public  employees:WritableSignal<EmployeeRequestDTO[]>=signal([]);

  getAll():Observable<EmployeeResponseDTO[]>{
    return this.http.get<EmployeeResponseDTO[]>(this.apiUrl);
  }
  getById(id:number):Observable<EmployeeResponseDTO>{
    return this.http.get<EmployeeResponseDTO>(`${this.apiUrl}/${id}`);
  }
  add(employee: EmployeeRequestDTO): Observable<EmployeeRequestDTO> {
    return this.http.post<EmployeeRequestDTO>(this.apiUrl, employee);
  }
  update(id:number,employee: EmployeeRequestDTO): Observable<EmployeeRequestDTO> {
    return this.http.put<EmployeeRequestDTO>(`${this.apiUrl}/${id}`, employee);
  }
  delete(id:number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
