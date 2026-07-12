import {NgClass} from '@angular/common';
import {Component, inject, Input} from '@angular/core';
import TodoDTO from '../../../dto/TodoDTO';
import {TodoServices} from '../../services/todo-services';

@Component({
  selector: 'app-todo-item',
  imports: [NgClass],
  templateUrl: './todo-item.html',
  styleUrl: './todo-item.css',
})
export class TodoItem {
  @Input()
  todo!: TodoDTO;

  public todoService = inject(TodoServices);

}
