import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-componentlifecycle',
  imports: [FormsModule],
  templateUrl: './componentlifecycle.html',
  styleUrl: './componentlifecycle.css',
})
export class Componentlifecycle implements OnInit,OnChanges {
  @Input() bankName!:any;
  constructor(){
    console.log("1. inside constructor");
  }
  ngOnChanges(changes: SimpleChanges): void {
   console.log("2. ",changes);
  }
 
  ngOnInit(){
    console.log("3. inside on Init");
    console.log(" make api call, fill data");
  }
  ngDoCheck(){
  console.log("4. inside do Check");
  }
  ngAfterContentInit(){
    //<ng-content>
    console.log("5. Content Loaded");

  }
  ngAfterContentChecked(){
    console.log("6. inside ngAfterContentChecked ");
  }
  ngAfterViewInit(){
    console.log("7. ngAfterViewInit");
  }
  ngAfterViewChecked(){
    console.log("8.ngAfterViewChecked");
  }
}
