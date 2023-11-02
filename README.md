[![codecov](https://codecov.io/gh/sitepark/ies-userrepository-core/graph/badge.svg?token=eRLHR95gFb)](https://codecov.io/gh/sitepark/ies-userrepository-core)
[![Known Vulnerabilities](https://snyk.io/test/github/sitepark/ies-userrepository-core/badge.svg)](https://snyk.io/test/github/sitepark/ies-userrepository-core/)
# User Repository Core Package

This is the core of the User Repository, serving as an integral part of a Clean Architecture design for user management within a Content Management System (CMS) application named IES. This project provides the fundamental building blocks and interfaces required for managing user data in the IES application.

## Components

### Entities
The entities in this project represent core objects related to user data. These objects include user profiles, login credentials, roles, and permissions. The definition and structure of these entities form the basis for data processing in the IES application.

### Use Cases
The use cases represent the business logic applied to user data, including creating user profiles, authenticating users, updating user information, and managing permissions. The use cases are implemented in the layers above this core project and utilize the entities defined here.

## Usage
To use this core project in the IES application, you should add it as a dependency and implement the interfaces and classes as per your application's requirements. This enables a clear separation of domain logic from other layers and facilitates the scalability and maintainability of your application.