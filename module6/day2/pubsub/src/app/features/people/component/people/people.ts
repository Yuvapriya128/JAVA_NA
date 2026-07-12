import { Component } from '@angular/core';
import { PersonComponent } from '../person-component/person-component';
import { AddPerson } from '../add-person/add-person';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-people',
  imports: [PersonComponent, AddPerson, CommonModule],
  templateUrl: './people.html',
  styleUrl: './people.css'
})
export class People {

  people: PersonDTO[] = [
    {
      id: 1,
      name: 'John',
      age: 25,
      email: 'john@gmail.com'
    },
    {
      id: 2,
      name: 'Alice',
      age: 30,
      email: 'alice@gmail.com'
    }
  ];

  selectedPerson: PersonDTO = {
    id: 0,
    name: '',
    age: 0,
    email: ''
  };

  remove(pid: number) {
    this.people = this.people.filter(p => p.id !== pid);
  }

  addPerson(person: PersonDTO) {
    this.people = [...this.people, person];
  }

  editPerson(person: PersonDTO) {
    this.selectedPerson = { ...person };
  }

  updatePerson(updatedPerson: PersonDTO) {

    const index = this.people.findIndex(
      p => p.id === updatedPerson.id
    );

    if (index !== -1) {
      this.people[index] = { ...updatedPerson };
    }

    this.selectedPerson = {
      id: 0,
      name: '',
      age: 0,
      email: ''
    };
  }

}
