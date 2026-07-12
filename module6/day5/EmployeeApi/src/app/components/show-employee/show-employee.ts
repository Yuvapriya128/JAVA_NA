import {Component, inject, Input, signal, WritableSignal} from '@angular/core';
import {EmployeeServices} from '../services/employee-services';
import {FormsModule} from '@angular/forms';
import EmployeeResponseDTO from '../dto/EmployeeResponseDTO';

@Component({
  selector: 'app-show-employee',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './show-employee.html',
  styleUrls: ['./show-employee.css'],
})
export class ShowEmployee {
  @Input()
  employee!: EmployeeResponseDTO;

  employeeService:EmployeeServices=inject(EmployeeServices);
  protected status:WritableSignal<{loading:boolean,error?:string,success?:boolean}>=signal({loading:false,error:'',success:false});

 // employees:WritableSignal<EmployeeResponseDTO[]>=signal([]);

  remove(){
    this.status.set({loading:true});
    if(this.employee.id!==undefined){
      this.employeeService.delete(this.employee.id).subscribe({
        next:()=>{
          //console.log('Employee deleted successfully');
          //filter
          this.employeeService.employees.set(this.employeeService.employees().filter(emp => emp.id !== this.employee.id));
          this.status.set({loading:false,success:true});
        },
        error:(error)=>{
          console.error('Error removing employee',error);
          this.status.set({loading:false,error:'Error deleting employee'});
        }
      })
    }
  }

}

