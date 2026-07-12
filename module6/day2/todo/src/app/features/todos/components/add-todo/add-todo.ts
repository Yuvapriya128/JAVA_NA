import {Component, inject} from '@angular/core';
import TodoDTO from '../../../dto/TodoDTO';
import {TodoServices} from '../../services/todo-services';
import {TodosComponent} from '../todos-component/todos-component';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-add-todo',
  imports: [FormsModule],
  templateUrl: './add-todo.html',
  styleUrl: './add-todo.css',
})
/*
* 1.inject service
* 2.create newtodo-object
* 3.add newtodo to the service
* 4.clear newtodo data
*/
export class AddTodo {
  public todoService:TodoServices=inject(TodoServices);
  public newTodo:TodoDTO = {
    id:0,
    title:'',
    description:'',
    completed:false
  };
  addTodo(newTodo:TodoDTO){
    if(newTodo.id>0 && newTodo.title!=null && newTodo.description!=null){
      this.todoService.addTodo(newTodo);
    }
    this.newTodo={
      id:0,
      title:'',
      description:'',
      completed:false
    }
  }

}
