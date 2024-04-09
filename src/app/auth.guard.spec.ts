import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from './Services/AutheticationService/auth.service';
import { AuthGuard } from './auth.guard';
import { of } from 'rxjs';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authService: AuthService;
  let router: Router;
  let snackBar: MatSnackBar;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        AuthGuard,
        AuthService,
        MatSnackBar
      ]
    });

    guard = TestBed.inject(AuthGuard);
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    snackBar = TestBed.inject(MatSnackBar);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true and not open snack bar if user is logged in', () => {
    const isLoggedInUserSpy = jest.spyOn(authService, 'isLoggedInUser').mockReturnValue(true);
    const snackBarOpenSpy = jest.spyOn(snackBar, 'open');
    const routerNavigateSpy = jest.spyOn(router, 'navigate');

    const canActivate = guard.canActivate();

    expect(canActivate).toBe(true);
    expect(isLoggedInUserSpy).toHaveBeenCalled();
    expect(snackBarOpenSpy).not.toHaveBeenCalled();
    expect(routerNavigateSpy).not.toHaveBeenCalled();
  });

  it('should return false, open snack bar, and navigate to home page if user is not logged in', () => {
    const isLoggedInUserSpy = jest.spyOn(authService, 'isLoggedInUser').mockReturnValue(false);
    const snackBarOpenSpy = jest.spyOn(snackBar, 'open');
    const routerNavigateSpy = jest.spyOn(router, 'navigate');

    const canActivate = guard.canActivate();

    expect(canActivate).toBe(false);
    expect(isLoggedInUserSpy).toHaveBeenCalled();
    expect(snackBarOpenSpy).toHaveBeenCalledWith('Please log in', 'OK');
    expect(routerNavigateSpy).toHaveBeenCalledWith(['']);
  });
});
