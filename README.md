# Online Book Store Application

## Description
The Online Book Store Application is a web application built with Spring Boot. It uses JWT for authentication and PostgreSQL as the database.

## Setup Instructions
1. Ensure you have Java 17 installed on your machine.
2. Clone the repository to your local machine.
3. Navigate to the project directory.
4. Run the command `mvn spring-boot:run` to start the application.

## Design Explanation
The application uses Spring Boot Starter for easy bootstrapping of the application. It uses Spring Security for authentication and authorization. 
JWT is used for maintaining the session. The application uses PostgreSQL as the database, and Spring Data JPA for ORM. 
Lombok is used to reduce boilerplate code. ModelMapper is used for object mapping.

## API Documentation
The application exposes several endpoints:

- `/app/**` and `/` : These are accessible to anyone and do not require authentication.
- All other endpoints require authentication.

Please note that only authenticated users can access the majority of the endpoints.

## Dependencies
The application uses the following dependencies:
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Validation
- Spring Boot Starter Web
- Spring Boot DevTools
- Spring Boot Starter Thymeleaf
- PostgreSQL
- Lombok
- ModelMapper
- jjwt-impl
- jjwt-jackson
- java-jwt
- Spring Boot Starter Test
- Spring Security Test
- Junit

## Build
The application uses the Spring Boot Maven Plugin for building the application.

## Testing the Application

You can test the application using a web browser. The application exposes the following endpoints:

-  `/`: Endpoint to display the home page
- `/app/add`: Endpoint to add a new book to the store.
- `/app/bookDetails`: Endpoint to view the details of a book.
- `/app/addBook`: Endpoint to show the page for adding a book.
- `/app/userPage`: Endpoint to show the user page.
- `/app/books`: Endpoint to show the page with a list of books.
- `/app/book/{id}`: Endpoint to show the page of a book by its ID.
- `/app/updateBook/{id}`: Endpoint to show the page for updating a book by its ID.
- `/app/removeBook/{id}`: Endpoint to remove a book by its ID.

The application also has the following endpoints for user registration and authentication:

- `/app/register`: Endpoint to show the registration form.
- `/app/login`: Endpoint to show the sign-in form.
- `/app/logout`: Endpoint to log out.

Please note that only the home, register, and login URLs are accessible by anyone. The remaining endpoints are for authenticated users.


## Application Properties

The application uses the following properties. Please replace the placeholders with your own details:

```properties
spring.application.name=Online-Book-Store

# =================================================
# - JPA / HIBERNATE
# NOT ADVISABLE FOR USE IN PRODUCTION ENVIRONMENT
# =================================================
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto= update
#spring.jpa.show-sql=true
#spring.jpa.properties.hibernate.format_sql = true
spring.jpa.database=postgresql
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# =================================================
# - Datasource (Postgres datasource Properties)
# =================================================
spring.datasource.url=jdbc:postgresql://localhost:5432/onlineBookstore-db
spring.datasource.username= <your-database-username>
spring.datasource.password= <your-database-password>

# =========================================================
# - JWT PROPERTIES
# =========================================================
lms.jwtExpirationMs=<your-jwt-expiration-time-in-ms>
lms.jwtSecretKey=<your-jwt-secret-key>
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License
MIT

## Frontend UI

![](/Users/apple/Documents/ideaProjects/Online-Book-Store/src/main/resources/images/home.png "Home page")
![](/Users/apple/Documents/ideaProjects/Online-Book-Store/src/main/resources/images/register.png "Register Page")
![](/Users/apple/Documents/ideaProjects/Online-Book-Store/src/main/resources/images/login.png "Login Page")
![](/Users/apple/Documents/ideaProjects/Online-Book-Store/src/main/resources/images/userPage.png "User Page")
![](/Users/apple/Documents/ideaProjects/Online-Book-Store/src/main/resources/images/allbooks.png "List of Books Page")
![](/Users/apple/Documents/ideaProjects/Online-Book-Store/src/main/resources/images/invalidLogin.png "Invalid Login Details")
![](/Users/apple/Documents/ideaProjects/Online-Book-Store/src/main/resources/images/addBook.png "Add Book Page")
![](/Users/apple/Documents/ideaProjects/Online-Book-Store/src/main/resources/images/invalidBookId.png "Invalid Book Id")
![When book has been successfully added to book store](/Users/apple/Documents/ideaProjects/Online-Book-Store/src/main/resources/images/bookAdded.png "Book Added")


