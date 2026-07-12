import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {EmployeeServices} from '../services/employee-services';
import {ShowEmployee} from '../show-employee/show-employee';
import {AddEmployee} from '../add-employee/add-employee';
import {UpdateEmployee} from '../update-employee/update-employee';
import EmployeeResponseDTO from '../dto/EmployeeResponseDTO';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-show-employees',
  standalone: true,
  imports: [FormsModule,ShowEmployee,AddEmployee,UpdateEmployee],
  templateUrl: './show-employees.html',
  styleUrls: ['./show-employees.css'],
})
export class ShowEmployees implements OnInit {
  employeeService:EmployeeServices=inject(EmployeeServices);
  protected  status:WritableSignal<{loading:boolean,error?:string,success?:boolean}>=signal({loading:false,error:'',success:false});
   //employees:WritableSignal<EmployeeResponseDTO[]>=signal([]);

  public showupdate:boolean=false;
    ngOnInit(): void {
      this.status.set({loading:true,error:'',success:false});
        this.getAllEmp();
    }
    getAllEmp(){
      this.employeeService.getAll().subscribe(
        {
          next: (data) => {
            this.status.set({loading:false,success:true});
            this.employeeService.employees.set(data);
            console.log(data);
          },
          error: (err) => {
            console.log(err);
            this.status.set({loading:false,error:err.message});
          },
          complete: () => {
            console.log('GetAll completed');
          }
        }
      );
    }

}

