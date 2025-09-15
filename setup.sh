#!/bin/bash

# Google Drive AI Agent Setup Script
echo "🚀 Setting up Google Drive AI Agent..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 18 or higher."
    exit 1
fi

# Check if PostgreSQL is installed
if ! command -v psql &> /dev/null; then
    echo "❌ PostgreSQL is not installed. Please install PostgreSQL 15 or higher."
    exit 1
fi

echo "✅ Prerequisites check passed!"

# Create environment file
echo "📝 Creating environment configuration..."
cat > backend/.env << EOF
# Database Configuration
DB_USERNAME=driveai_user
DB_PASSWORD=driveai_password

# Google OAuth Configuration
GOOGLE_CLIENT_ID=your_google_client_id_here
GOOGLE_CLIENT_SECRET=your_google_client_secret_here
GOOGLE_REDIRECT_URI=http://localhost:3000/auth/callback

# OpenAI Configuration
OPENAI_API_KEY=your_openai_api_key_here
EOF

echo "✅ Environment file created at backend/.env"
echo "⚠️  Please update the .env file with your actual API keys and credentials"

# Setup database
echo "🗄️  Setting up database..."
read -p "Enter PostgreSQL username (default: postgres): " db_user
db_user=${db_user:-postgres}

read -p "Enter PostgreSQL password: " -s db_password
echo

read -p "Enter database name (default: driveai): " db_name
db_name=${db_name:-driveai}

# Create database and user
psql -U $db_user -c "CREATE DATABASE $db_name;" 2>/dev/null || echo "Database may already exist"
psql -U $db_user -c "CREATE USER driveai_user WITH PASSWORD 'driveai_password';" 2>/dev/null || echo "User may already exist"
psql -U $db_user -c "GRANT ALL PRIVILEGES ON DATABASE $db_name TO driveai_user;" 2>/dev/null || echo "Privileges may already be granted"

# Run schema
echo "📋 Running database schema..."
psql -U driveai_user -d $db_name -f backend/src/main/resources/schema.sql

echo "✅ Database setup complete!"

# Install backend dependencies
echo "📦 Installing backend dependencies..."
cd backend
mvn clean install -q
cd ..

# Install frontend dependencies
echo "📦 Installing frontend dependencies..."
cd frontend
npm install
cd ..

echo "🎉 Setup complete!"
echo ""
echo "Next steps:"
echo "1. Update backend/.env with your actual API keys"
echo "2. Start the backend: cd backend && mvn spring-boot:run"
echo "3. Start the frontend: cd frontend && npm run dev"
echo "4. Visit http://localhost:3000 to access the application"
echo ""
echo "For detailed setup instructions, see README.md"
