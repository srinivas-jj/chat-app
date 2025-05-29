# Chat Application Backend

A real-time chat application built with Spring Boot, PostgreSQL, WebSocket (STOMP), and JWT authentication.

## Features

- 🔐 JWT-based authentication with email verification
- 💬 Real-time messaging using WebSocket (STOMP)
- 👥 One-to-one private chat
- 🏠 Group chat rooms
- 🔍 Message search functionality
- 📱 Online/offline user status
- ✅ Message persistence in PostgreSQL
- 🚀 RESTful API endpoints

## Technology Stack

- **Backend**: Spring Boot 3.2.0
- **Database**: PostgreSQL
- **Authentication**: JWT with Spring Security
- **Real-time Communication**: WebSocket (STOMP)
- **Email**: Spring Mail
- **Validation**: Bean Validation (Hibernate Validator)

## Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- An email account for SMTP (Gmail recommended)

## Setup Instructions

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE chatdb;
CREATE USER postgres WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE chatdb TO postgres;
```

### 2. Application Configuration

Update `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/chatdb
    username: postgres
    password: your_password
  
  mail:
    username: your-email@gmail.com
    password: your-app-password
```

### 3. Email Configuration (Gmail)

1. Enable 2-factor authentication on your Gmail account
2. Generate an App Password: Gmail → Manage Account → Security → App Passwords
3. Use the generated password in the application.yml

### 4. JWT Secret

Set a secure JWT secret (32+ characters):

```yaml
jwt:
  secret: your-very-secure-secret-key-here-32-chars-minimum
```

### 5. Build and Run

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | User login |
| GET | `/api/auth/verify-email?token=` | Verify email |
| POST | `/api/auth/refresh` | Refresh JWT token |

### Chat Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/chat/rooms` | Create group room |
| POST | `/api/chat/rooms/private?userId=` | Create/get private room |
| GET | `/api/chat/rooms` | Get user's rooms |
| GET | `/api/chat/rooms/{id}/messages` | Get room messages |
| GET | `/api/chat/rooms/{id}/messages/search?query=` | Search messages |
| POST | `/api/chat/rooms/{id}/members?userId=` | Add member to room |
| DELETE | `/api/chat/rooms/{id}/members/{userId}` | Remove member |
| GET | `/api/chat/users/search?query=` | Search users |
| GET | `/api/chat/users/online` | Get online users |

## WebSocket Endpoints

### Connection
```
ws://localhost:8080/ws
```

### Subscribe to Topics
```javascript
// For room messages
/topic/room/{roomId}

// For typing indicators
/topic/room/{roomId}/typing

// For personal error messages
/user/queue/errors
```

### Send Messages
```javascript
// Send chat message
/app/chat.sendMessage

// Join room
/app/chat.addUser

// Leave room
/app/chat.leaveUser

// Typing indicator
/app/chat.typing
```

## Request/Response Examples

### User Registration
```bash
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "John Doe"
}
```

### User Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

# Response
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "id": 1,
  "email": "user@example.com",
  "fullName": "John Doe"
}
```

### Create Group Room
```bash
POST /api/chat/rooms
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Development Team",
  "description": "Team discussion room",
  "type": "GROUP",
  "memberIds": [2, 3, 4]
}
```

### WebSocket Message Format
```javascript
{
  "content": "Hello everyone!",
  "type": "TEXT",
  "senderId": 1,
  "senderName": "John Doe",
  "roomId": 1
}
```

## Frontend Integration Example

### Connecting to WebSocket
```javascript
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect(
  { Authorization: `Bearer ${token}` },
  (frame) => {
    console.log('Connected:', frame);
    
    // Subscribe to room messages
    stompClient.subscribe(`/topic/room/${roomId}`, (message) => {
      const chatMessage = JSON.parse(message.body);
      displayMessage(chatMessage);
    });
  }
);

// Send message
const sendMessage = (content, roomId, senderId, senderName) => {
  stompClient.send('/app/chat.sendMessage', {}, JSON.stringify({
    content,
    type: 'TEXT',
    roomId,
    senderId,
    senderName
  }));
};
```

## Database Schema

### Tables Created Automatically
- `users` - User information and authentication
- `rooms` - Chat rooms (private/group)
- `room_members` - Room membership mapping
- `messages` - Chat messages

## Security Features

- JWT token-based authentication
- Password encryption using BCrypt
- Email verification for new accounts
- CORS configuration for frontend integration
- Request validation using Bean Validation

## Development

### Running in Development Mode
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package -Pprod
java -jar target/chat-backend-0.0.1-SNAPSHOT.jar
```

## Environment Variables

Set these environment variables for production:

```bash
export JWT_SECRET=your-production-secret-key
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
export DB_URL=jdbc:postgresql://localhost:5432/chatdb
export DB_USERNAME=postgres
export DB_PASSWORD=your-db-password
```

## Common Issues

1. **Email not sending**: Check Gmail app password and 2FA settings
2. **Database connection**: Verify PostgreSQL is running and credentials are correct
3. **JWT errors**: Ensure JWT secret is at least 32 characters
4. **WebSocket connection**: Check CORS settings and frontend URL

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License. 