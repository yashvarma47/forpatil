import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
 
export interface User {
  username: number;
  password: string;
  // Other properties...
}
@Injectable({
  providedIn: 'root'
})

export class UserService {
  private userIdSubject = new BehaviorSubject<string>('');
  userId$ = this.userIdSubject.asObservable();
 
  setUserId(userId: string) {
    this.userIdSubject.next(userId);
  }
}