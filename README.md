# Google Drive AI Agent

A comprehensive AI-powered Google Drive automation tool that integrates with Google Drive to help with querying data, creating documents, and automating business processes.

## 🚀 Features

- **Google Drive Integration**: Browse, search, and analyze your Google Drive files
- **AI Chat Interface**: Natural language queries about Drive contents using OpenAI GPT-4
- **Document Analysis**: AI-powered content analysis and summarization
- **Business Process Automation**: Template-based document generation
- **User Management**: Secure Google OAuth 2.0 authentication
- **Real-time Chat**: Interactive AI assistant for file management

## 🛠️ Tech Stack

### Backend
- **Spring Boot 3.x** with Java 17+
- **PostgreSQL 15+** for data persistence
- **Google APIs**: Drive API v3, Docs API, Sheets API
- **OpenAI API** (GPT-4) for AI capabilities
- **OAuth 2.0** for Google authentication

### Frontend
- **React 18+** with TypeScript
- **Vite** for build tooling
- **Tailwind CSS** for styling
- **React Router** for navigation
- **Axios** for API communication

## 📋 Prerequisites

- Java 17 or higher
- Node.js 18+ and npm
- PostgreSQL 15+
- Google Cloud Console project with APIs enabled
- OpenAI API key

## 🔧 Setup Instructions

### 1. Google Cloud Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable the following APIs:
   - Google Drive API
   - Google Docs API
   - Google Sheets API
4. Create OAuth 2.0 credentials:
   - Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client IDs"
   - Application type: "Web application"
   - Authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`

### 2. Database Setup

1. Install PostgreSQL 15+
2. Create a database:
   ```sql
   CREATE DATABASE driveai;
   CREATE USER driveai_user WITH PASSWORD 'driveai_password';
   GRANT ALL PRIVILEGES ON DATABASE driveai TO driveai_user;
   ```
3. Run the schema:
   ```bash
   psql -U driveai_user -d driveai -f backend/src/main/resources/schema.sql
   ```

### 3. Environment Configuration

Create a `.env` file in the backend directory:

```bash
# Database
DB_USERNAME=driveai_user
DB_PASSWORD=driveai_password

# Google OAuth
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GOOGLE_REDIRECT_URI=http://localhost:3000/auth/callback

# OpenAI
OPENAI_API_KEY=your_openai_api_key
```

### 4. Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Install dependencies and run:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

### 5. Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

   The frontend will start on `http://localhost:3000`

## 🏗️ Project Structure

```
drive-ai-agent/
├── backend/
│   ├── src/main/java/com/driveai/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── service/         # Business logic services
│   │   ├── model/           # JPA entities
│   │   ├── repository/      # Data access layer
│   │   └── dto/             # Data transfer objects
│   └── src/main/resources/
│       ├── application.yml  # Application configuration
│       └── schema.sql       # Database schema
└── frontend/
    ├── src/
    │   ├── components/      # React components
    │   ├── hooks/          # Custom React hooks
    │   ├── services/       # API services
    │   ├── types/          # TypeScript type definitions
    │   └── utils/          # Utility functions
    └── public/             # Static assets
```

## 🔑 API Endpoints

### Authentication
- `GET /api/auth/user` - Get current user info
- `GET /api/auth/status` - Check authentication status
- `POST /api/auth/logout` - Logout user

### Google Drive
- `GET /api/drive/files` - List files
- `GET /api/drive/files/{id}` - Get file details
- `GET /api/drive/files/{id}/content` - Get file content
- `GET /api/drive/search` - Search files
- `GET /api/drive/folders` - List folders

### AI Chat
- `POST /api/chat/message` - Send chat message
- `GET /api/chat/sessions` - Get chat sessions
- `GET /api/chat/sessions/{id}/messages` - Get session messages
- `POST /api/chat/sessions` - Create new session
- `DELETE /api/chat/sessions/{id}` - Delete session

## 🚀 Usage

1. **Authentication**: Visit `http://localhost:3000` and sign in with Google
2. **Browse Files**: Navigate to the Drive Files section to explore your Google Drive
3. **AI Chat**: Use the AI Chat section to ask questions about your files
4. **Document Analysis**: Click on files to view content and AI-generated summaries

## 🔒 Security Features

- OAuth 2.0 authentication with Google
- Secure token storage and refresh
- CORS configuration for frontend-backend communication
- Input validation and sanitization
- SQL injection protection with JPA

## 🧪 Development

### Running Tests
```bash
# Backend tests
cd backend
mvn test

# Frontend tests
cd frontend
npm test
```

### Building for Production
```bash
# Backend
cd backend
mvn clean package

# Frontend
cd frontend
npm run build
```

## 📝 Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `DB_USERNAME` | PostgreSQL username | Yes |
| `DB_PASSWORD` | PostgreSQL password | Yes |
| `GOOGLE_CLIENT_ID` | Google OAuth client ID | Yes |
| `GOOGLE_CLIENT_SECRET` | Google OAuth client secret | Yes |
| `OPENAI_API_KEY` | OpenAI API key | Yes |

## 🐛 Troubleshooting

### Common Issues

1. **OAuth Error**: Ensure redirect URIs match exactly in Google Cloud Console
2. **Database Connection**: Verify PostgreSQL is running and credentials are correct
3. **API Quotas**: Check Google API quotas and OpenAI API limits
4. **CORS Issues**: Ensure frontend and backend are running on correct ports

### Logs
- Backend logs: Check console output or `logs/application.log`
- Frontend logs: Check browser developer console

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Google Drive API for file management
- OpenAI for AI capabilities
- Spring Boot and React communities
- Tailwind CSS for styling

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the API documentation

---

**Note**: This is a development setup. For production deployment, additional security measures, environment configuration, and deployment strategies should be implemented.
