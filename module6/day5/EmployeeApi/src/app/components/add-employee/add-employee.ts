import {Component, inject, signal, WritableSignal} from '@angular/core';
import {EmployeeServices} from '../services/employee-services';
import {FormsModule} from '@angular/forms';
import EmployeeResponseDTO from '../dto/EmployeeResponseDTO';
import EmployeeRequestDTO from '../dto/EmployeeRequestDTO';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-add-employee',
  imports: [FormsModule,CommonModule],
  templateUrl: './add-employee.html',
  styleUrl: './add-employee.css',
})
export class AddEmployee {

  employeeService:EmployeeServices=inject(EmployeeServices);
  protected status:WritableSignal<{loading:boolean,error?:string,success?:boolean}>=signal({loading:false,error:'',success:false});
 // employees:WritableSignal<EmployeeResponseDTO[]>=signal([]);

  public newEmployee:EmployeeRequestDTO={
    id:0,
    name:'',
    salary:0
  };

  addEmployee(){
    this.status.set({loading:true,error:'',success:false});
    this.employeeService.add(this.newEmployee).subscribe(
      {
        next: (data) => {
          this.status.set({loading:false,success:true});
          this.employeeService.employees.set([...this.employeeService.employees(), data]);
          console.log(data);
          this.clearForm(); // Add this line
        },
        error: (err) => {
          this.status.set({loading:false,error:err.message});
          console.log(err);
        },
        complete: () => {
          console.log('Add completed');
        }
      }
    );
  }
  clearForm() {
    this.newEmployee = {
      id: 0,
      name: '',
      salary: 0
    };
  }
}
