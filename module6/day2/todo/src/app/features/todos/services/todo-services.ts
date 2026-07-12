import {Injectable, Service} from '@angular/core';
import TodoDTO from '../../dto/TodoDTO';


@Injectable({
  providedIn: 'root'
})
export class TodoServices {
  public todos: TodoDTO[] = [
    {
      id: 1,
      title: 'Learn Angular',
      description: 'Learn basics and advanced concepts',
      completed: false
    },
    {
      id: 2,
      title: 'Build Todo App',
      description: 'Create a CRUD application using Angular',
      completed: false
    },
    {
      id: 3,
      title: 'Practice TypeScript',
      description: 'Work on interfaces, classes, and generics',
      completed: true
    },
    {
      id: 4,
      title: 'Learn RxJS',
      description: 'Understand Observables and operators',
      completed: false
    },
    {
      id: 5,
      title: 'Read Angular Documentation',
      description: 'Explore routing, forms, and services',
      completed: true
    },
    {
      id: 6,
      title: 'Complete Project',
      description: 'Finalize UI and connect APIs',
      completed: false
    },
    {
      id: 7,
      title: 'Write Unit Tests',
      description: 'Add test cases for components and services',
      completed: false
    },
    {
      id: 8,
      title: 'Deploy Application',
      description: 'Host Angular app on Netlify or Azure',
      completed: false
    }
  ];

  public getTodos(): TodoDTO[] {
    return this.todos;
  }
  public addTodo(todo:TodoDTO):TodoDTO{
    this.todos.push(todo);
    return todo;
  }
  public updateTodo(updatedTodo:TodoDTO):void{
    const index=this.todos.findIndex(todo => todo.id === updatedTodo.id);
    if (index !== -1) {
      this.todos[index] = updatedTodo;
    }
  }
  public deleteTodo(id:number):void{
    this.todos = this.todos.filter(todo => todo.id !== id);
  }

}
