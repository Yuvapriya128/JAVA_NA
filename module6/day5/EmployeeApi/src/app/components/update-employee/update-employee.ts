import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import EmployeeRequestDTO from '../dto/EmployeeRequestDTO';
import {EmployeeServices} from '../services/employee-services';

@Component({
  selector: 'app-update-employee',
  imports: [FormsModule],
  templateUrl: './update-employee.html',
  styleUrls: ['./update-employee.css'],
})
export class UpdateEmployee implements OnInit {
  employeeService:EmployeeServices=inject(EmployeeServices);

  protected status:WritableSignal<{loading:boolean,error?:string,success?:boolean}>=signal({loading:false,error:'',success:false});

  ngOnInit(): void {
      this.status.set({loading:true});
      // setTimeout(()=>{
      //   this.updateEmployee();
      // },1000);
  }


  public newEmployee:EmployeeRequestDTO={
    id:0,
    name:'',
    salary:0
  };

  updateEmployee(){
    this.employeeService.update(this.newEmployee.id,this.newEmployee).subscribe(
      {
        next: (data) => {
          this.status.set({loading:false,success:true});
          console.log(data);
          this.employeeService.employees.set([...this.employeeService.employees(), data]);
        },
        error: (err) => {
          console.log(err);
          this.status.set({loading:false,error:err.message});
        },
        complete: () => {
          console.log('Update completed');
        }
      }
    );
  }

}

