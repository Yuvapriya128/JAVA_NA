import {Component, inject} from '@angular/core';
import {TodoServices} from '../../services/todo-services';
import TodoDTO from '../../../dto/TodoDTO';
import {TodoItem} from '../todo-item/todo-item';
import {AddTodo} from '../add-todo/add-todo';
import {UpdateTodo} from '../update-todo/update-todo';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-todos-component',
  imports: [FormsModule,TodoItem,AddTodo,UpdateTodo],
  templateUrl: './todos-component.html',
  styleUrls: ['./todos-component.css'],
})
export class TodosComponent {
  public todoService = inject(TodoServices);
  showUpdate = false;

}
