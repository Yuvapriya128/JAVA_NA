import {Component, EventEmitter, Input, Output} from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-person-component',
  imports: [CommonModule],
  templateUrl: './person-component.html',
  styleUrl: './person-component.css',
})
export class PersonComponent {

@Input()
  public p!:PersonDTO;


  @Output()
  public onRemove = new EventEmitter<number>();

  @Output()
  public onAdd = new EventEmitter<PersonDTO>();

  @Output()
  public onUpdate = new EventEmitter<PersonDTO>();

  removePerson(pid:number) {
    this.onRemove.emit(pid);
  }

  updatePerson(pid:number) {
     this.onUpdate.emit({
       id: pid,
       name: this.p.name,
       age: this.p.age,
       email: this.p.email
     });
   }

  getHeaderClass(age: number): string {
    if (age >= 50) return 'bg-danger';
    if (age >= 30) return 'bg-primary';
    return 'bg-success';
  }

  getHeaderColor(age: number): string {
    if (age >= 50) return '#dc3545';
    if (age >= 30) return '#007bff';
    return '#28a745';
  }

  getAgeBorderColor(age: number): string {
    if (age >= 50) return '#dc3545';
    if (age >= 30) return '#007bff';
    return '#28a745';
  }
}
