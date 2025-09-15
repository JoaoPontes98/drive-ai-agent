export interface DriveFile {
  id: string;
  name: string;
  mimeType: string;
  size?: number;
  modifiedTime?: string;
  contentSummary?: string;
  webViewLink?: string;
  downloadLink?: string;
  isFolder: boolean;
  parentId?: string;
}

export interface DriveFileListResponse {
  files: DriveFile[];
  count: number;
  query: string;
}

export interface DriveSearchResponse {
  files: DriveFile[];
  count: number;
  query: string;
}

export interface DriveFolderResponse {
  folders: DriveFile[];
  count: number;
  parentId?: string;
}

export interface FileContentResponse {
  fileId: string;
  content: string;
}

export interface DriveFileType {
  type: 'Google Doc' | 'Google Sheet' | 'PDF' | 'Text File' | 'Folder' | 'File';
  icon: string;
  color: string;
}
