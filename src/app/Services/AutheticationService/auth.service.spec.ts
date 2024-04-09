import { AuthService } from './auth.service';
import { UserService, User } from '../user.service';
import { of, Observable } from 'rxjs';

describe('AuthService', () => {
  let authService: AuthService;
  let userService: UserService;

  beforeEach(() => {
    const userServiceMock: Partial<UserService> = { // Partial type to mock only the necessary parts
      userId$: jest.fn() as unknown as Observable<string> // Mocking userId$ observable
    };

    userService = userServiceMock as UserService;

    authService = new AuthService(userService);
  });

  it('should be created', () => {
    expect(authService).toBeTruthy();
  });

  describe('login', () => {
    it('should return true if user is found', () => {
      const mockUser: User[] = [{ username: 123, password: 'password' }];
      userService.userId$ = of(JSON.stringify(mockUser)) as Observable<string>; // Mocking userId$ observable behavior
      const isLoggedIn = authService.login(123, 'password');
      expect(isLoggedIn).toBe(true);
    });

    it('should return false if user is not found', () => {
      const mockUser: User[] = [{ username: 123, password: 'password' }];
      userService.userId$ = of(JSON.stringify(mockUser)) as Observable<string>; // Mocking userId$ observable behavior
      const isLoggedIn = authService.login(456, 'password');
      expect(isLoggedIn).toBe(false);
    });
  });

  describe('isLoggedInUser', () => {
    it('should return the value of isLoggedIn', () => {
      authService.isLoggedIn = true;
      const isLoggedIn = authService.isLoggedInUser();
      expect(isLoggedIn).toBe(true);
    });
  });
});
