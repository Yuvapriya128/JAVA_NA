import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'

})
export class CountServices {
  // use $ afterwards to show it is observable
  //instead of returning value , return observable: read getCount
  private count$=new BehaviorSubject<number>(0);

  increment(){
    let curcnt=this.count$.value;
    this.count$.next(curcnt+1);
  }
  decrement(){
    let curcnt=this.count$.value;
    this.count$.next(curcnt-1);
  }
  incrementBy(value:number){
    let curcnt=this.count$.value;
    this.count$.next(curcnt+value);
  }
  decrementBy(value:number){
    let curcnt=this.count$.value;
    this.count$.next(curcnt-value);
  }
  /*add asyncpipe in imports
  * and do | async while calling in showcount*/
  getCount(){
    return this.count$.asObservable();
  }
}
