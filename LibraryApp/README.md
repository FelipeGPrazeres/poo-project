# Library Management System

This project is a simplified library management desktop application built with Java and Swing, managing Books, Patrons, and Loans.

## Features Implemented
- **Book Management:** CRUD operations and search by title/author/ISBN.
- **Patron Management:** CRUD operations, search by name/ID, and borrowing history view.
- **Loan Management:** Checkout and return books, with data integrity (e.g. no checkout if 0 copies, can't delete patron with active loans).
- **GUI:** Multi-panel interface using Swing.
- **Data Persistence:** Uses Java Object Serialization to save and load data from `data/` directory.
- **Exceptions:** Custom exceptions like `BookAlreadyOnLoanException`, `PatronHasActiveLoansException`, and `AuthenticationException`.

### Optional Enhancements Included
- Overdue fine calculation ($1.50 per day late).
- Role-based login (Administrator can delete books/patrons; Librarian can only add/edit).
- Patron borrowing history view.

## Build Procedures
This project uses Maven for dependency management (specifically JUnit 5 for tests).

### Prerequisites
- Java Development Kit (JDK) 8 or higher (Recommended: JDK 17).
- Maven installed, OR run the project via an IDE like IntelliJ IDEA or Eclipse.

### Running via IntelliJ IDEA (Recommended)
1. Open IntelliJ IDEA.
2. Select **File > Open** and choose the `LibraryApp` directory.
3. Wait for IntelliJ to load the Maven `pom.xml` and download dependencies.
4. Navigate to `src/main/java/com/library/Main.java`.
5. Click the green "Run" button next to the `main` method.
6. The Login screen will appear. 
   - **Admin Login:** Username: `admin`, Password: `admin`
   - **Librarian Login:** Username: `librarian`, Password: `lib`

### Running via Command Line (with Maven installed)
1. Open a terminal and navigate to the `LibraryApp` folder.
2. Compile the project: `mvn compile`
3. Run the application: `mvn exec:java -Dexec.mainClass="com.library.Main"`
4. To run tests: `mvn test`
