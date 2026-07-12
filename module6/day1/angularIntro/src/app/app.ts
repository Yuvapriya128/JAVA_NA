import {Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {FormsModule} from '@angular/forms';
import EmployeeDTO from '../dto/EmployeeDTO';
import { HttpClient } from '@angular/common/http';

// ngModel comes from FormsModule
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './app.html'
})
// to connect with database: onInit, httpClient
export class App implements OnInit {

    // protected names:string[]=["yuva"];
    // modify people to WritableSignal
    //protected names:WritableSignal<string[]> = signal([]);

/*
  getAll() {
    this.httpClient
      .get<EmployeeDTO[]>("http://localhost:8080/api/employees")
      .subscribe({
        next: (data: EmployeeDTO[]) => {
          console.log(data);

          this.people.set(data.map((item: EmployeeDTO) => {
            return item.name;
          }));
        },
        error: (err:string) => {
          console.error(err);
        }
      });
  }*/

  httpClient: HttpClient = inject(HttpClient);

  ngOnInit(): void {
    this.getAllEmployees();

  }
  protected people:WritableSignal<EmployeeDTO[]> = signal([]);

 getAllEmployees(){
   this.httpClient
     .get<EmployeeDTO[]>("http://localhost:8080/api/employees")
     .subscribe({
       next:(response: EmployeeDTO[])=>{
         console.log(response);
         this.people.set(response);

       },error:(err:string)=>{console.log(err)}
       }
     )
 }
 protected newEmployee={
   id:0,
   name:"",
   salary:0,

 }
 addEmployee(){
   if(this.newEmployee.id!=0 && this.newEmployee.name!="" && this.newEmployee.salary!=0)
   this.httpClient
     .post("http://localhost:8080/api/employees", this.newEmployee)
     .subscribe({
       next: (response) => {
         console.log(response);
         this.getAllEmployees(); // Refresh the list after adding a new employee
       },
       error: (err) => {
         console.error(err);
       }
     });
   this.newEmployee.id=0;
   this.newEmployee.name="";
   this.newEmployee.salary=0;
 }
 deleteEmployee(employee: EmployeeDTO){
   this.httpClient
     .delete(`http://localhost:8080/api/employees/${employee.id}`)
     .subscribe({
       next: (response) => {
         console.log(response);
         this.getAllEmployees(); // Refresh the list after deleting an employee
       },
       error: (err) => {
         console.error(err);
       }
     });

 }
updateEmployee(employee: EmployeeDTO){
   this.httpClient
     .put(`http://localhost:8080/api/employees/${employee.id}`,
       this.newEmployee)
  .subscribe({
    next: (response) => {
      console.log(response);
      this.getAllEmployees();
    },
    error: (err) => {
      console.error(err);
    }
  });
  this.newEmployee.id=0;
  this.newEmployee.name="";
  this.newEmployee.salary=0;
}









  protected  title = 'firstDemo';
  protected person={
    fname: "John",
    lname: "Doe"
  }
  protected newName = '';

  protected newPerson={
    name:'',
    age:0,
    city:''
  }
  protected peopleDetails=[
    {name:"Yuvapriya", age: 25, city: "Chennai" },
    {name:"Siva", age: 30, city: "Bangalore" },
    {name:"Amudha", age: 28, city: "Hyderabad" },
    {name:"Gokul", age: 27, city: "Mumbai" }
  ]
  changeTitle(){
    this.title = 'Demo';
  }

  removePerson(person: {name: string, age: number, city: string}){
    this.peopleDetails = this.peopleDetails.filter(p => p !== person);
  }
  // shift ,unshift can be used
  addPerson(){
    if(this.newPerson.name.trim().length > 0 && this.newPerson.age > 0 && this.newPerson.city.trim().length > 0){
      this.peopleDetails.push({...this.newPerson});
      this.newPerson = {name:'', age:0, city:''};
    }
  }
 updatePerson(index:number){
   if(this.newPerson.name.trim().length > 0 && this.newPerson.age > 0 && this.newPerson.city.trim().length > 0) {
     this.peopleDetails[index] = {...this.newPerson};
     this.newPerson = {name:'', age:0, city:''};
   }
   }

  /*removeName(name: string){
    this.people.set(this.people.filter(person => person !== name));
  }
  //  push, unshift: can be used
  addName(){
    if(this.newName.trim().length > 0)
    this.people.push(this.newName);
    this.newName = '';
  }
  updateName(index:number){
    //alert(index);
    if(this.newName.trim().length > 0){
      this.people[index] = this.newName;
      this.newName = '';
    }
  }*/
}
