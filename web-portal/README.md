# Web Portal

A modern web application with React frontend and Spring Boot backend, featuring user authentication with email verification.

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 12+
- Maven 3.6+

### Backend Setup
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

### Database Setup

#### Automated Setup (Windows)
Double-click `setup.bat` in the root directory, or run:
```cmd
setup.bat
```


#### Manual Setup
1. Install PostgreSQL 12+
2. Create PostgreSQL database: `webportal`
3. Update credentials in `backend/src/main/resources/application.properties` if you changed the default password
4. Liquibase will automatically create tables on first run

## 📚 Documentation

- **[Architecture](docs/architecture/)** - System architecture and design
- **[Setup Guides](docs/setup/)** - Installation and configuration
- **[Features](docs/features/)** - Feature documentation
- **[Cleanup Summary](docs/CLEANUP_SUMMARY.md)** - Project cleanup history

## 🏗️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.5.7
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA
- **Migrations**: Liquibase
- **Email**: Spring Mail (SMTP)

### Frontend
- **Framework**: React 19
- **Build Tool**: Vite 7
- **Routing**: React Router 7
- **Styling**: Vanilla CSS

## ✨ Features

- ✅ User Registration with Email Verification
- ✅ Login/Logout
- ✅ Password Validation
- ✅ Email Verification System
- ✅ PostgreSQL Database
- ✅ Liquibase Database Migrations
- ✅ CORS Configuration
- ✅ Responsive UI

## 🔐 Security Features

- Email verification required before login
- Password strength validation (8+ chars, uppercase, lowercase, number, special char)
- CORS protection
- Input validation (client and server-side)

## 📁 Project Structure

```
web-portal/
├── backend/              # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
├── frontend/             # React frontend
│   ├── src/
│   │   ├── assets/
│   │   ├── App.jsx
│   │   ├── Login.jsx
│   │   ├── Signup.jsx
│   │   └── Verify.jsx
│   └── package.json
└── docs/                 # Documentation
    ├── architecture/
    ├── setup/
    └── features/
```

## 🌐 Endpoints

### Backend (Port 8081)
- `POST /api/signup` - User registration
- `GET /api/verify?token=...` - Email verification
- `POST /api/login` - User login
- `GET /api/hello` - Test endpoint

### Frontend (Port 5173)
- `/` - Login page
- `/signup` - Registration page
- `/verify` - Email verification page

## 🛠️ Development

### Running Tests
```bash
# Backend
cd backend
./mvnw test

# Frontend
cd frontend
npm test
```

### Building for Production
```bash
# Backend
cd backend
./mvnw clean package

# Frontend
cd frontend
npm run build
```

## 📝 Configuration

Key configuration files:
- `backend/src/main/resources/application.properties` - Backend configuration
- `frontend/vite.config.js` - Frontend build configuration
- `backend/src/main/resources/db/changelog/` - Database migrations

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Add tests if applicable
4. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- Development Team

## 🔗 Related Documentation

- [Liquibase Guide](docs/setup/LIQUIBASE_GUIDE.md)
- [Signup Implementation](docs/features/SIGNUP_IMPLEMENTATION.md)
- [Architecture Diagram](docs/architecture/architecture.md)
