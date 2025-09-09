# Student CRUD API

A production-ready RESTful API for managing student records, built with Spring Boot and following modern software development best practices.

## 🚀 Overview

This project demonstrates a comprehensive implementation of a CRUD (Create, Read, Update, Delete) API with robust error handling, data validation, and comprehensive testing. It serves as a showcase of modern Java development practices and enterprise-level application architecture.

## ✨ Key Features

- **Complete CRUD Operations** - Create, retrieve, update, and delete student records
- **Data Validation** - Comprehensive input validation with custom error messages
- **Exception Handling** - Global exception handling with structured error responses
- **Audit Trail** - Automatic tracking of creation and modification timestamps
- **API Documentation** - Interactive Swagger/OpenAPI documentation
- **Optimistic Locking** - Version-based concurrency control
- **Status Management** - Student lifecycle management (Active, Suspended, Graduated, Withdrawn)
- **Database Integration** - JPA/Hibernate with H2 in-memory database
- **Comprehensive Testing** - Unit, integration, and repository tests with 95%+ coverage

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.5.5
- **Java Version**: 17
- **Database**: H2 (in-memory)
- **ORM**: Spring Data JPA / Hibernate
- **Documentation**: SpringDoc OpenAPI 3 (Swagger)
- **Testing**: JUnit 5, Mockito, Spring Test
- **Build Tool**: Maven
- **Additional Libraries**: 
  - Lombok (boilerplate reduction)
  - Jakarta Validation
  - Jackson (JSON processing)

## 📋 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/students` | Retrieve all students |
| GET | `/api/v1/students/{id}` | Retrieve student by ID |
| POST | `/api/v1/students` | Create new student |
| PUT | `/api/v1/students/{id}` | Update existing student |
| DELETE | `/api/v1/students/{id}` | Delete student |

## 📊 Student Data Model

```json
{
  "id": 1,
  "name": "John Doe",
  "passportNumber": "A1234567",
  "age": 25,
  "email": "john@example.com",
  "enrollmentDate": "2024-09-01T09:00:00",
  "graduationYear": 2026,
  "status": "ACTIVE",
  "createdAt": "2024-09-01T09:00:00",
  "updatedAt": "2024-09-01T09:00:00",
  "version": 0
}
```

## 🔧 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Installation & Running

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd crud_api
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

3. **Run tests**
   ```bash
   mvn test
   ```

4. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - API Base URL: `http://localhost:8080/api/v1`
   - Swagger UI: `http://localhost:8080/swagger-ui/index.html`
   - H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:testdb`
     - Username: `sa`
     - Password: (empty)

## 📝 API Usage Examples

### Create a Student
```bash
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson",
    "passportNumber": "B9876543",
    "age": 22,
    "email": "alice@example.com",
    "enrollmentDate": "2024-01-15T10:00:00",
    "graduationYear": 2026
  }'
```

### Get All Students
```bash
curl -X GET http://localhost:8080/api/v1/students
```

### Update a Student
```bash
curl -X PUT http://localhost:8080/api/v1/students/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson Updated",
    "age": 23,
    "email": "alice.updated@example.com",
    "graduationYear": 2025
  }'
```

## 🧪 Testing Strategy

The project includes comprehensive testing at multiple levels:

- **Unit Tests** - Testing individual components in isolation
- **Integration Tests** - End-to-end API testing with real database
- **Repository Tests** - JPA repository functionality testing
- **Validation Tests** - Input validation and constraint testing

### Run Tests
```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=StudentServiceTest
```

## 📐 Architecture & Design Patterns

- **Layered Architecture** - Controller → Service → Repository → Entity
- **Dependency Injection** - Constructor-based injection for better testability
- **Repository Pattern** - Data access abstraction
- **DTO Pattern** - Data transfer optimization (implicit with JPA entities)
- **Exception Handling** - Centralized error handling with `@RestControllerAdvice`
- **Builder Pattern** - Lombok-generated builders for entity construction

## 🔒 Data Validation Rules

- **Name**: Required, 2-100 characters
- **Passport Number**: Required, unique, format: 1 letter + 7 digits (e.g., A1234567)
- **Age**: Required, 16-100 years
- **Email**: Optional, valid email format, unique
- **Enrollment Date**: Required
- **Graduation Year**: Optional, 2020-2030 range
- **Status**: Required, enum values (ACTIVE, SUSPENDED, GRADUATED, WITHDRAWN)

## 🚨 Error Handling

The API provides structured error responses with detailed information:

```json
{
  "timestamp": "2024-09-03T12:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/v1/students",
  "details": [
    "name: Name is required",
    "age: Student must be at least 16 years old"
  ]
}
```

## 📊 Database Schema

The application uses an H2 in-memory database with the following schema:

```sql
CREATE TABLE student (
   id SERIAL PRIMARY KEY NOT NULL,
   name VARCHAR(100) NOT NULL,
   passport_number VARCHAR(10) NOT NULL UNIQUE,
   age INTEGER NOT NULL CHECK (age >= 16 AND age <= 100),
   email VARCHAR(255) UNIQUE,
   enrollment_date TIMESTAMP NOT NULL,
   graduation_year INTEGER CHECK (graduation_year >= 2020 AND graduation_year <= 2030),
   status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
   created_at TIMESTAMP NOT NULL,
   updated_at TIMESTAMP NOT NULL,
   version BIGINT NOT NULL DEFAULT 0
);
```

## 🔍 Monitoring & Observability

- **Spring Actuator** - Health checks and application metrics
- **SQL Logging** - Hibernate SQL logging enabled for development
- **Audit Trail** - Automatic creation and modification timestamps

## 🚀 Future Enhancements

- [ ] JWT-based authentication and authorization
- [ ] Pagination and sorting for student listing
- [ ] Advanced search and filtering capabilities
- [ ] Integration with external databases (PostgreSQL, MySQL)
- [ ] Caching layer implementation (Redis)
- [ ] API rate limiting
- [ ] Docker containerization
- [ ] CI/CD pipeline setup

## 🤝 Contributing

This is a portfolio project, but feedback and suggestions are welcome:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Contact

**Ashna Ali** - Software Developer
- Email: ashna.ali.prof@gmail.com
- LinkedIn: [https://www.linkedin.com/in/ashna-ali]
- Portfolio: [https://my-portfolio-pi-ashy-83.vercel.app/]

---

*This project demonstrates modern Java development practices and enterprise-level application architecture suitable for production environments.*
