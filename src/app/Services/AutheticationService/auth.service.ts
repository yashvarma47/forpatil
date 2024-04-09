import { Injectable } from '@angular/core';
import { UserService } from '../user.service';
import { User } from '../user.service';
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  public isLoggedIn: boolean = false;
 
  constructor(private userService: UserService) {}
 
  login(username: number, password: string): boolean {
    let userFound = false;
    this.userService.userId$.subscribe((userId: string) => {
      const users: User[] = JSON.parse(userId); // Assuming userId contains JSON string representing an array of users
      const user = users.find((u: { username: number; password: string; }) => u.username === username && u.password === password);
      userFound = !!user;
    });
    this.isLoggedIn = userFound;
    return this.isLoggedIn;
  }
 
  isLoggedInUser(): boolean {
    return this.isLoggedIn;
  }
}