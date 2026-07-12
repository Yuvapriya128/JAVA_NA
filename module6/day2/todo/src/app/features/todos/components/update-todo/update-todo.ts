import {Component, inject} from '@angular/core';
import TodoDTO from '../../../dto/TodoDTO';
import {TodoServices} from '../../services/todo-services';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-update-todo',
  imports: [FormsModule],
  templateUrl: './update-todo.html',
  styleUrls: ['./update-todo.css'],
})
export class UpdateTodo {
  public todoService:TodoServices=inject(TodoServices);
  public newTodo:TodoDTO={
    id:0,
    title:'',
    description:'',
    completed:false,
  };
  public updateTodo(selectedTodo: TodoDTO): void {

    if (
      selectedTodo.id > 0 &&
      selectedTodo.title.trim() !== '' &&
      selectedTodo.description.trim() !== ''
    ) {

      this.todoService.updateTodo(selectedTodo);

      // Clear the form
      this.newTodo = {
        id: 0,
        title: '',
        description: '',
        completed: false
      };

      // Exit update mode if you're using one
      // this.selectedTodo = null;
      // this.showUpdate = false;
    }
  }

}
