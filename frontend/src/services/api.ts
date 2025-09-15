import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { User, AuthResponse } from '../types/auth';
import { DriveFileListResponse, DriveFile, DriveSearchResponse, DriveFolderResponse, FileContentResponse } from '../types/drive';
import { ChatRequest, ChatResponse, ChatSessionResponse, ChatMessagesResponse } from '../types/chat';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: '/api',
      timeout: 30000,
      withCredentials: true,
    });

    // Request interceptor
    this.api.interceptors.request.use(
      (config) => {
        // Add any auth headers here if needed
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor
    this.api.interceptors.response.use(
      (response) => {
        return response;
      },
      (error) => {
        if (error.response?.status === 401) {
          // Handle unauthorized access
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // Auth endpoints
  async getCurrentUser(): Promise<AuthResponse> {
    const response: AxiosResponse<AuthResponse> = await this.api.get('/auth/user');
    return response.data;
  }

  async getAuthStatus(): Promise<AuthResponse> {
    const response: AxiosResponse<AuthResponse> = await this.api.get('/auth/status');
    return response.data;
  }

  async logout(): Promise<{ message: string }> {
    const response: AxiosResponse<{ message: string }> = await this.api.post('/auth/logout');
    return response.data;
  }

  // Drive endpoints
  async listFiles(maxResults: number = 10, query?: string): Promise<DriveFileListResponse> {
    const params = new URLSearchParams();
    params.append('maxResults', maxResults.toString());
    if (query) params.append('query', query);

    const response: AxiosResponse<DriveFileListResponse> = await this.api.get(`/drive/files?${params}`);
    return response.data;
  }

  async getFile(fileId: string): Promise<DriveFile> {
    const response: AxiosResponse<DriveFile> = await this.api.get(`/drive/files/${fileId}`);
    return response.data;
  }

  async getFileContent(fileId: string): Promise<FileContentResponse> {
    const response: AxiosResponse<FileContentResponse> = await this.api.get(`/drive/files/${fileId}/content`);
    return response.data;
  }

  async searchFiles(query: string, maxResults: number = 10): Promise<DriveSearchResponse> {
    const params = new URLSearchParams();
    params.append('q', query);
    params.append('maxResults', maxResults.toString());

    const response: AxiosResponse<DriveSearchResponse> = await this.api.get(`/drive/search?${params}`);
    return response.data;
  }

  async listFolders(parentId?: string, maxResults: number = 10): Promise<DriveFolderResponse> {
    const params = new URLSearchParams();
    params.append('maxResults', maxResults.toString());
    if (parentId) params.append('parentId', parentId);

    const response: AxiosResponse<DriveFolderResponse> = await this.api.get(`/drive/folders?${params}`);
    return response.data;
  }

  // Chat endpoints
  async sendMessage(request: ChatRequest): Promise<ChatResponse> {
    const response: AxiosResponse<ChatResponse> = await this.api.post('/chat/message', request);
    return response.data;
  }

  async getChatSessions(): Promise<ChatSessionResponse> {
    const response: AxiosResponse<ChatSessionResponse> = await this.api.get('/chat/sessions');
    return response.data;
  }

  async getSessionMessages(sessionId: number): Promise<ChatMessagesResponse> {
    const response: AxiosResponse<ChatMessagesResponse> = await this.api.get(`/chat/sessions/${sessionId}/messages`);
    return response.data;
  }

  async createChatSession(title?: string): Promise<ChatSession> {
    const params = new URLSearchParams();
    if (title) params.append('title', title);

    const response: AxiosResponse<ChatSession> = await this.api.post(`/chat/sessions?${params}`);
    return response.data;
  }

  async deleteChatSession(sessionId: number): Promise<{ message: string }> {
    const response: AxiosResponse<{ message: string }> = await this.api.delete(`/chat/sessions/${sessionId}`);
    return response.data;
  }
}

export const apiService = new ApiService();
export default apiService;
