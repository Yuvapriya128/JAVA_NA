import {Injectable, Service, signal, WritableSignal} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NameServices {
  public names: string[] = ["yuva","siva","amudha","gokul"];
  public names$:WritableSignal<string[]> = signal(this.names);

  getNames(){
    return this.names$;
  }
  addName(name:string){
    this.names.push(name);
    this.names$.set(this.names);

  }
  updateName(oldName:string,newName:string){
    this.names = this.names.map(n => n === oldName ? newName : n);
    this.names$.set(this.names);
  }
  deleteName(name:string){
    this.names=this.names.filter(n=>n!==name);
    this.names$.set(this.names);
  }

}
