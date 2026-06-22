# Project Report: Library Management System

**Group Identification**
- Name: Felipe Galvão Prazeres, NUSP: 16828948
- Name: Laura Nordi Zambon, NUSP: 14655491

---

## 1. Requirements
*In this section, you list the core requirements from the assignment and any new ones we added.*

**Core Assignment Requirements:**
- **Book Management:** Add, edit, delete, and search books (Title, Author, ISBN). Track available copies.
- **Patron Management:** Add, edit, delete, and search patrons (Name, ID, Contact Info).
- **Loan Management:** Check out and return books. Enforce data integrity (no checkout if 0 copies, no deleting patron with active loans).
- **Loan Overview:** View active loans.
- **GUI Design:** Separate tabs for Books, Patrons, and Loans using Java Swing.
- **Data Persistence:** File I/O saving (`data/books.dat`, etc.).
- **Exception Handling:** Custom exceptions and clear GUI error messages.

**Additional Requirements (Above and Beyond / Optional Enhancements):**
- Role-based login (Administrator vs Librarian) to restrict who can delete records.
- Overdue fine calculation ($1.50 per day late).
- Patron borrowing history tracking.

## 2. Project Description
*Describe how the application works and the architectural decisions. Diagrams go here.*

- **Architecture:** We used the Model-View-Controller (MVC) pattern. 
  - **Models:** `Book`, `Patron`, `Loan`, `User`.
  - **View:** `LoginFrame`, `MainFrame`, `BookPanel`, `PatronPanel`, `LoanPanel` built using Java Swing.
  - **Controller/Service:** `LibraryManager` and `AuthManager` to process business logic.
- **Persistence:** We used Java Object Serialization (`ObjectOutputStream`/`ObjectInputStream`) within `FileManager` to persist object state to local `.dat` files.
- *(Insert UML Class Diagram or Use Case Diagram here)*

## 3. Comments About the Code
*Highlight good programming practices used in the code.*

- **Encapsulation:** All model properties are `private` and accessed via getters/setters.
- **Exception Handling:** We created custom exceptions (`BookAlreadyOnLoanException`, `PatronHasActiveLoansException`, `AuthenticationException`) to handle business logic violations gracefully, showing them as GUI dialogs instead of crashing.
- **Simplicity & Readability:** We avoided overly complex syntax (like complex Streams) in the GUI to ensure the logic remained clear and easy to maintain.

## 4. Test Plan
*Describe the tests to be performed.*

- **Unit Testing (Automated):** We used JUnit 5 to test core logic without relying on the GUI. 
  - Tests include verifying successful login, preventing checkout when 0 copies exist, and checking if fines are calculated correctly on late returns.
- **Manual GUI Testing:** 
  - Log in as Librarian and verify the "Delete" buttons are hidden.
  - Add a book and check if it persists after restarting the application.
  - Check out a book and verify the available copies decrease.

## 5. Test Results
*Provide the output of the automated tests.*

- **JUnit Results:** 
  - `AuthManagerTest`: All tests passed (Successful Login, Failed Login, Logout).
  - `LibraryManagerTest`: All tests passed (Add/Search Book, Checkout Success, Checkout Fails No Copies, Return Book with Fine).
- *(Optional: Include a screenshot of the green JUnit bar from IntelliJ).*

## 6. Build Procedures
*Step-by-step guide on how someone else can compile and run the project.*

1. Install Java Development Kit (JDK) 8 or higher.
2. Open the project folder `LibraryApp` in an IDE that supports Maven (like IntelliJ IDEA or Eclipse).
3. Allow the IDE to download dependencies from the `pom.xml`.
4. Run the `src/main/java/com/library/Main.java` class.
5. Log in using `admin` / `admin`.

*(Alternatively, to run via terminal with Maven installed)*
- `mvn compile`
- `mvn exec:java -Dexec.mainClass="com.library.Main"`

## 7. Problems
*List any major problems you had during development.*

- *(Example: We had to figure out how to share the `LibraryManager` state across multiple different Swing Panels without creating new instances.)*
- *(Example: Managing dates for the fine calculation required using `java.time.LocalDate` and calculating the difference in days.)*

## 8. Comments
*Any final comments you wish to add.*

- The project was successfully implemented achieving the "Above and Beyond" (5 stars) criteria by including all optional features.
