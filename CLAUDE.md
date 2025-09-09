# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Building and Testing
- **Build project**: `mvn compile`
- **Run all tests**: `mvn test`
- **Run a single test**: `mvn test -Dtest=ClassNameTest` or `mvn test -Dtest=ClassNameTest#methodName`
- **Generate test coverage report**: `mvn jacoco:report` (report generated in `target/site/jacoco/`)

### Code Quality
- **Format code**: `mvn spotless:apply`
- **Check code formatting**: `mvn spotless:check`
- **Run static analysis**: `mvn verify` (runs SpotBugs, PMD, and other quality checks)
- **Run specific static analysis tools**:
  - SpotBugs: `mvn spotbugs:check`
  - PMD: `mvn pmd:check`

### Packaging and Publishing
- **Create JAR**: `mvn package`
- **Install to local repository**: `mvn install`
- **Deploy snapshot**: `mvn deploy`

## Architecture Overview

This is a Clean Architecture implementation for user management in the IES (Content Management System) application. The codebase follows hexagonal architecture principles with clear separation between layers.

### Core Architectural Layers

1. **Domain Layer** (`domain/`)
   - **Entities**: Core business objects (`User`, `Role`, `Privilege`)
   - **Value Objects**: Immutable objects like `Identity`, `Password`, `Permission`, `UserValidity`
   - **Exceptions**: Domain-specific exceptions for business rule violations
   - **Services**: Domain services like `IdentifierResolver`

2. **Use Case Layer** (`usecase/`)
   - Business logic implementation as use case classes
   - Query infrastructure with filters, sorting, and pagination
   - Examples: `CreateUser`, `UpdateUser`, `SearchUsers`, `GetAllRoles`

3. **Port Layer** (`port/`)
   - Interfaces defining contracts for external dependencies
   - Repository interfaces: `UserRepository`, `RoleRepository`, `PrivilegeRepository`
   - Service interfaces: `AccessControl`, `PasswordHasher`, `IdGenerator`

### Key Design Patterns

- **Dependency Inversion**: Use cases depend on port interfaces, not implementations
- **Builder Pattern**: Entities use builders for construction (e.g., `User.Builder`)
- **Immutable Objects**: All entities and value objects are immutable
- **Jakarta Injection**: Uses `@Inject` for dependency injection

### Entity Relationships

- **User**: Central entity with roles, identities, and validity periods
- **Role**: Contains privileges and can be assigned to users
- **Privilege**: Atomic permission units that can be grouped into roles
- **Identity**: External identity providers (LDAP, etc.) linked to users

### Query System

The project includes a sophisticated query system with:
- **Filters**: Complex filtering with `And`, `Or`, `Not` operators
- **Sorting**: Multiple sort criteria with direction control
- **Pagination**: Offset-based and limit-based pagination
- **Results**: Structured result objects with metadata

### Code Quality Standards

- **Java 21** target with modern language features
- **Google Java Format** for consistent code style
- **SpotBugs** for bug pattern detection
- **PMD** for code quality rules
- **JaCoCo** for test coverage monitoring
- **JUnit 5** with Mockito for testing

### Testing Approach

- Unit tests for all use cases and domain logic
- Test utilities: `equalsverifier`, `to-string-verifier` for robust object testing
- Hamcrest matchers for readable assertions
- Mock-based testing with clear separation of concerns