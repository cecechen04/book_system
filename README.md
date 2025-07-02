# Book Mall System

This project is a book mall system developed with **Vue3** and **Spring Boot 3.0**. It consists of two parts: the user side and the backend management system. The user side allows browsing books, managing shopping carts, generating orders, etc., while the backend management system enables administrators to manage book categories, book information, orders, and more.

## Project Background

With the rapid development of e-commerce, the online book market has gradually become mainstream. This system aims to provide a convenient platform for book purchasing, helping users easily find and buy books, while also providing management tools for administrators to ensure smooth platform operation.

## Project Objectives

- Users can register, log in, browse books, add them to the shopping cart, and generate orders on the frontend.
- Administrators can manage book categories, book information, and orders through the backend management system.

## Tech Stack

- **Frontend**: Vue3, Vite, Axios
- **Backend**: Spring Boot 3.0+
- **Database**: JDBC
- **Others**: RESTful API style

## Functional Modules

### User Side
- **User Registration and Login**: Users can register a new account and log in.
- **Book Browsing and Searching**: Users can search for books by category or keywords.
- **Shopping Cart Management**: Users can add books to the shopping cart, remove items, and view the cart content.
- **Order Management**: Users can generate orders from the shopping cart, view orders, simulate payment, and confirm receipt.

### Backend Management
- **Category Management**: Administrators can view, add, modify, and delete 1st and 2nd level categories.
- **Book Management**: Administrators can view books by category, add new books, edit book details, and delete books.
- **Order Management**: Administrators can query orders by status and view detailed order information.

## Installation and Running

### Backend

1. Clone the repository and navigate to the backend directory:
    ```bash
    git clone https://github.com/cecechen04/book_system.git
    cd book_system/backend
    ```

2. Build the project using Maven:
    ```bash
    mvn clean install
    ```

3. Start the backend application:
    ```bash
    mvn spring-boot:run
    ```

4. The default port is `8080`.

### Frontend

1. Navigate to the frontend directory:
    ```bash
    cd book_system/frontend
    ```

2. Install dependencies:
    ```bash
    npm install
    ```

3. Start the frontend development server:
    ```bash
    npm run dev
    ```

4. The default access URL is `http://localhost:3000`.

## Contributing

We welcome **Issues** and **Pull Requests**. We encourage everyone to participate in the development and improvement of the project.

## Contact

If you have any questions, you can reach us via the following:
- Email: [cecechen04@example.com](mailto:cecechen04@example.com)
- GitHub: [cecechen04](https://github.com/cecechen04)

## License

This project is licensed under the [MIT License](LICENSE).
