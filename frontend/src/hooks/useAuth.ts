import { useState, useEffect, useCallback } from 'react';
import { User, AuthState } from '../types/auth';
import { apiService } from '../services/api';

export const useAuth = () => {
  const [authState, setAuthState] = useState<AuthState>({
    user: null,
    isAuthenticated: false,
    isLoading: true,
    error: null,
  });

  const checkAuthStatus = useCallback(async () => {
    try {
      setAuthState(prev => ({ ...prev, isLoading: true, error: null }));
      
      const response = await apiService.getCurrentUser();
      
      if (response.authenticated && response.user) {
        setAuthState({
          user: response.user,
          isAuthenticated: true,
          isLoading: false,
          error: null,
        });
      } else {
        setAuthState({
          user: null,
          isAuthenticated: false,
          isLoading: false,
          error: response.error || 'Not authenticated',
        });
      }
    } catch (error: any) {
      setAuthState({
        user: null,
        isAuthenticated: false,
        isLoading: false,
        error: error.message || 'Failed to check authentication status',
      });
    }
  }, []);

  const login = useCallback(() => {
    window.location.href = '/login';
  }, []);

  const logout = useCallback(async () => {
    try {
      await apiService.logout();
      setAuthState({
        user: null,
        isAuthenticated: false,
        isLoading: false,
        error: null,
      });
      window.location.href = '/login';
    } catch (error: any) {
      setAuthState(prev => ({
        ...prev,
        error: error.message || 'Failed to logout',
      }));
    }
  }, []);

  useEffect(() => {
    checkAuthStatus();
  }, [checkAuthStatus]);

  return {
    ...authState,
    login,
    logout,
    checkAuthStatus,
  };
};
