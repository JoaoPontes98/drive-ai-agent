export interface User {
  id: number;
  googleId: string;
  email: string;
  name: string;
  createdAt: string;
}

export interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}

export interface AuthResponse {
  authenticated: boolean;
  user?: User;
  error?: string;
}
