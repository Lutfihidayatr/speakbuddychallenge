# Spring Boot REST API with PostgreSQL and Audio Processing

This project is a RESTful API built with Spring Boot that includes user management and audio file processing capabilities. It's containerized using Docker and uses PostgreSQL for data persistence.

## Architecture Overview

### Tech Stack
- **Spring Boot**: Java-based framework for building web applications
- **PostgreSQL**: Relational database for data persistence
- **Docker & Docker Compose**: Containerization and orchestration
- **JavaCV/FFmpeg**: Audio processing and format conversion
- **JPA/Hibernate**: ORM for database operations

### System Architecture
The application follows a typical Spring Boot layered architecture:
- **Controller Layer**: REST endpoints for client interactions
- **Service Layer**: Business logic and audio processing
- **Repository Layer**: Data access using Spring Data JPA
- **Model Layer**: Entity definitions for database mapping

### Database Schema
- **Users**: Stores user information with fields for identification and account status
- **Phrases**: Stores audio files metadata linked to users, supporting soft delete functionality

## Key Components

### Audio Processing
Audio format conversion is handled using JavaCV, which provides Java bindings for FFmpeg. This allows converting between different audio formats while maintaining a pure Java API.

```java
// Audio conversion service using JavaCV
public boolean convertAudio(String inputFilePath, String outputFilePath) throws IOException {
    // Implementation details in JavaCVAudioConverter.java
}
```

### Docker Configuration
The application uses multi-stage Docker builds to optimize the image size and includes all necessary dependencies for audio processing.

### Database Initialization
Initial schema and sample data are provided through `init.sql` which runs when the PostgreSQL container is first created.

## Setup and Deployment

### Prerequisites
- Docker and Docker Compose installed on your system

### Running the Application
1. Clone the repository
2. Navigate to the project directory
3. Run `docker-compose up -d`

### API Endpoints
- **User Management**: 
  - `GET /api/users`: List all users
  - `GET /api/users/{id}`: Get user by ID

- **Audio Processing**:
  - `POST /audio/user/{id}/phrase`: Upload audio file for a user
  - `GET  /audio/user/{user_id}/phrase/{phrase_id}` : Additional endpoints for audio retrieval

## Development Notes

### FFmpeg Configuration
The Docker image includes FFmpeg and relevant development libraries to support JavaCV operations. Several libraries were configured to handle audio processing requirements.

### Volume Management
Two persistent volumes are configured:
- `postgres-data`: For database persistence
- `audio-uploads`: For storing uploaded and processed audio files

## Troubleshooting

### Common Issues
- **Port Conflicts**: If port 5432 is already in use, the PostgreSQL container will use port 5433 instead
- **Permission Issues**: File upload directories have 777 permissions for Docker container access

### Logs
To view application logs:
```bash
docker-compose logs -f app
```

## Future Enhancements

1. **Security Implementation**:
   - Add Spring Security for authentication and authorization
   - Implement JWT-based token authentication

2. **Service Optimization**:
   - Add caching for frequently accessed data
   - Implement asynchronous processing for audio conversion tasks
   - Add file storage like S3 to save audio files

3. **Testing Improvements**:
   - Add comprehensive unit and integration tests
   - Implement CI/CD pipeline

4. **Monitoring and Observability**:
   - Add metrics
   - Implement logging with ELK stack

5. **Feature Additions**:
   - Support for more audio formats and processing options
   - Batch audio processing functionality
