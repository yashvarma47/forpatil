import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginComponent } from 'src/app/Login-Fields/login/login.component';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  constructor(private router: Router) {}

  redirectTo() {
    // Replace 'another-component' with the route path of the component you want to navigate to
    this.router.navigate(['']);
  }

}
