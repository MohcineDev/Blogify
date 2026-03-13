# Blog System: Full-Stack Social Blogging Platform

Built a full-stack blogging platform using Spring Boot, Angular, and PostgreSQL, developing secure REST APIs with JWT authentication and role-based access control to support post creation and user interactions.


## 🚀 Technologies Used

This project is split into two distinct parts: a Java Spring Boot backend for API and security, and an Angular frontend for the user interface.

### Backend (API Server)

| Category | Technology | Description |
| :--- | :--- | :--- |
| **Framework** | Spring Boot 3 | Used for rapid application development and building a microservice architecture. |
| **Security** | Spring Security | Handles authentication (login/registration) and authorization (role-based access control). |
| **Authentication** | JWT (JSON Web Tokens) | Stateless, token-based authentication for secure API access. |
| **Persistence** | Spring Data JPA / Hibernate | Object-Relational Mapping (ORM) for efficient database interaction. |
| **Database** | PostgreSQL | Robust, open-source relational database used for data storage. |
| **Utility** | Lombok | Reduces boilerplate code (getters, setters, constructors). |

### Frontend (Client Application)

| Category | Technology | Description |
| :--- | :--- | :--- |
| **Framework** | Angular | A platform for building efficient and scalable single-page applications (SPAs). |
| **Communication** | `HttpClient` & Interceptors | Manages HTTP requests and automatically attaches JWT tokens. |
| **Routing** | Angular Router | Handles client-side navigation and route guarding. |
| **UI/UX** | Custom CSS | Styling the application with a clean, responsive user interface, implementing pagination for better content navigation, and enhancing layout, spacing, and visual consistency across devices. |

## ⚙️ Setup and Installation

Follow these steps to get the project running locally.

### Prerequisites

* Java Development Kit (JDK) 17+
* Node.js (LTS version)
* Angular CLI (`npm install -g @angular/cli`)
* PostgreSQL Database
* An IDE (IntelliJ IDEA or VS Code recommended)

### Step 1: Database Setup

1.  Ensure your PostgreSQL server is running.
2.  Create a new database for the project (e.g., `blog_db`).

### Step 2: Backend Configuration

1.  Navigate to the `/backend` directory.
2.  Open the `application.properties` (or `application.yml`) file.
3.  Configure the database connection details:

    ```properties
    # application.properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/blog_db
    spring.datasource.username=<YOUR_DB_USERNAME>
    spring.datasource.password=<YOUR_DB_PASSWORD>

    # Hibernate will automatically create tables (set to update after first run)
    spring.jpa.hibernate.ddl-auto=update 

    # Configure your JWT secret key
    application.security.jwt.secret-key=<YOUR_VERY_LONG_SECRET_KEY>
    ```

4.  Run the Spring Boot application:
    ```bash
    ./mvnw spring-boot:run
    ```
    (Or run the main class from your IDE). The backend will start on **`http://localhost:8080`**.

### Step 3: Frontend Configuration

1.  Navigate to the `/frontend` directory.
2.  Open the environment file (`src/environments/environment.ts`).
3.  Ensure the `apiUrl` points to your running backend:

    ```typescript
    // src/environments/environment.ts
    export const environment = {
      production: false,
      apiUrl: 'http://localhost:8080/api' // Base API path
    };
    ```

4.  Install the required dependencies:
    ```bash
    npm install
    ```

5.  Start the Angular development server:
    ```bash
    ng serve -o
    ```
    The frontend will automatically open in your browser on **`http://localhost:4200`**.

## 💻 Key Architectural Features

* **Custom JWT Authentication Filter:** Handles token validation, manages the `SecurityContext`, and implements advanced error handling for **banned/deleted users** to ensure proper client-side logout/redirection.
* **Role-Based Access Control (RBAC):** Utilizes Spring Security's `@EnableMethodSecurity` and `hasAuthority("ADMIN")` for endpoint protection.
* **Data Integrity (ON DELETE CASCADE):** JPA/Hibernate is configured using `@OnDelete(action = OnDeleteAction.CASCADE)` on foreign keys to automatically clean up related data (posts, comments) when a user is deleted by an administrator.

