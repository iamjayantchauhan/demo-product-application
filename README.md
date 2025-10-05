# Product Manager - Spring Boot + HTMX Application

A modern web application built with Spring Boot and HTMX that provides a dynamic product management interface with real-time updates and external API integration.

## 🚀 Features

### Core Functionality

- **Dynamic Product Management**: Complete CRUD operations without page reloads
- **Real-time UI Updates**: HTMX-powered interface for seamless user experience
- **External API Integration**: Automated product fetching from external sources
- **Modern UI**: Clean, responsive design using Web Awesome components
- **Database Persistence**: PostgreSQL with JSONB support for complex data structures

### Technical Highlights

- **Spring Boot 3.3.4** with Kotlin
- **HTMX 1.9.6** for dynamic interactions
- **PostgreSQL** database with Flyway migrations
- **Thymeleaf** templating with modular fragments
- **JdbcClient** for modern database operations
- **Scheduled Tasks** for automated data synchronization
- **JSON Deserialization** with Jackson for API integration

## 🛠️ Tech Stack

| Component        | Technology  | Version |
| ---------------- | ----------- | ------- |
| **Backend**      | Spring Boot | 3.3.4   |
| **Language**     | Kotlin      | Latest  |
| **Database**     | PostgreSQL  | 17.6+   |
| **Migration**    | Flyway      | Latest  |
| **Frontend**     | HTMX        | 1.9.6   |
| **Templating**   | Thymeleaf   | Latest  |
| **UI Framework** | Web Awesome | Latest  |
| **Build Tool**   | Gradle      | 8.14.3  |
| **JVM**          | Java        | 17      |

## 📋 Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher
- **PostgreSQL 17.6** or higher
- **Git** for version control
- **Docker** (optional, for containerized PostgreSQL)

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd DemoForGreg
```

### 2. Database Setup

#### Option A: Using Docker Compose (Recommended)

```bash
docker-compose up -d
```

#### Option B: Manual PostgreSQL Setup

1. Install PostgreSQL 17.6+
2. Create database:

```sql
CREATE DATABASE demo_for_greg;
CREATE USER demo_user WITH PASSWORD 'demo_password';
GRANT ALL PRIVILEGES ON DATABASE demo_for_greg TO demo_user;
```

### 3. Configure Application

Update `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/demo_for_greg
spring.datasource.username=demo_user
spring.datasource.password=demo_password
```

### 4. Run the Application

```bash
./gradlew bootRun
```

### 5. Access the Application

Open your browser and navigate to: **http://localhost:8080**

## 🎯 Usage Guide

### Product Management Interface

1. **Load Products**

   - Click the "Load Products" button to display all products from the database
   - Products are fetched automatically from external API every hour

2. **Add New Product**

   - Fill in the form fields:
     - Title (required)
     - Price (required, decimal format)
     - Image URL (optional)
     - Description (optional)
   - Click "Add Product" - the new product appears instantly in the table

3. **Edit Existing Products**

   - Click the "Edit" button on any product row
   - Modify the values in the inline form
   - Click "Save" - changes update immediately

4. **Delete Products**
   - Click the "Delete" button on any product row
   - Product is removed instantly from the table

All operations happen **without page reloads** thanks to HTMX integration!

## 🏗️ Architecture

### Project Structure

```
src/
├── main/
│   ├── kotlin/org/example/demo/
│   │   ├── controller/          # REST endpoints
│   │   ├── model/              # Data models
│   │   ├── repository/         # Database access layer
│   │   ├── service/            # Business logic
│   │   └── DemoForGregApplication.kt
│   └── resources/
│       ├── db/migration/       # Flyway SQL migrations
│       ├── templates/          # Thymeleaf templates
│       │   └── fragments/      # Reusable template fragments
│       └── application.properties
└── test/                       # Test files
```

### Key Components

#### Controllers

- **ProductController**: Handles all product-related HTTP requests
- HTMX-compatible endpoints returning template fragments

#### Services

- **ProductService**: Core business logic for product operations
- **ProductFetchService**: Scheduled service for external API integration

#### Repository Layer

- **ProductRepository**: Database operations using JdbcClient
- Modern Spring Data access patterns

#### Database Schema

- **products** table with JSONB support for flexible data storage
- Flyway migrations for version-controlled schema changes

## 🔄 External API Integration

The application automatically fetches product data from `https://famme.no/products.json`:

- **Scheduled Execution**: Runs every hour using `@Scheduled`
- **JSON Processing**: Handles complex nested JSON structures
- **Data Mapping**: Maps external API format to internal data model
- **Conflict Resolution**: Uses UPSERT to handle existing products
- **Error Handling**: Robust error handling with logging

### API Data Flow

1. Scheduled job triggers every hour
2. WebClient fetches JSON data from external API
3. Jackson deserializes JSON to Kotlin data classes
4. ProductService processes and saves to database
5. Database triggers update timestamps automatically

## 🎨 Frontend Architecture

### HTMX Integration

- **hx-get**: Load content dynamically
- **hx-post**: Submit forms via AJAX
- **hx-target**: Update specific page sections
- **hx-swap**: Control how content is updated

### Template Structure

- **index.html**: Main page layout
- **fragments/products-table.html**: Product table component
- **fragments/edit-product.html**: Inline edit form component

### Web Awesome Styling

- Modern design system with CSS custom properties
- Responsive layout components
- Consistent visual hierarchy

## 🗄️ Database Schema

### Products Table

```sql
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    external_id BIGINT UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    image_url TEXT,
    description TEXT,
    variants JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
```

### Features

- **JSONB Column**: Stores complex product variants efficiently
- **Unique Constraints**: Prevents duplicate external products
- **Timestamps**: Automatic tracking of record changes
- **Indexes**: Optimized for common query patterns

## 🚀 Deployment

### Production Configuration

1. Update database configuration for production
2. Configure external API endpoints
3. Set appropriate logging levels
4. Enable production profiles

### Docker Deployment

```bash
# Build application
./gradlew build

# Run with Docker Compose
docker-compose -f docker-compose.prod.yml up -d
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
```

### Manual Testing Checklist

- [ ] Load products from database
- [ ] Add new product via form
- [ ] Edit existing product inline
- [ ] Delete product from table
- [ ] Verify HTMX real-time updates
- [ ] Check external API data fetching
- [ ] Validate database persistence

## 🔧 Development

### Adding New Features

1. Create feature branch: `git checkout -b feature/new-feature`
2. Implement changes following existing patterns
3. Update tests and documentation
4. Submit pull request

### Code Style

- Kotlin coding conventions
- Spring Boot best practices
- HTMX progressive enhancement principles
- Clean architecture patterns

## 🐛 Troubleshooting

### Common Issues

**Database Connection Issues**

- Verify PostgreSQL is running
- Check connection credentials
- Ensure database exists

**External API Issues**

- Check network connectivity
- Verify API endpoint accessibility
- Review JSON deserialization logs

**HTMX Not Working**

- Ensure HTMX library is loaded
- Check browser developer tools for JavaScript errors
- Verify HTMX attributes are correctly set

## 📝 API Documentation

### Endpoints

| Method | Endpoint                  | Description           | Response      |
| ------ | ------------------------- | --------------------- | ------------- |
| GET    | `/`                       | Main application page | HTML          |
| GET    | `/api/products`           | Load all products     | HTML fragment |
| POST   | `/api/products`           | Create new product    | HTML fragment |
| GET    | `/api/products/{id}/edit` | Get edit form         | HTML fragment |
| POST   | `/api/products/{id}`      | Update product        | HTML fragment |
| DELETE | `/api/products/{id}`      | Delete product        | HTTP 200      |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- **Spring Boot Team** for the excellent framework
- **HTMX Community** for the innovative approach to web interactivity
- **Web Awesome** for the beautiful UI components
- **PostgreSQL** for robust database capabilities

---

**Built with ❤️ using Spring Boot + HTMX**

For questions or support, please open an issue in the repository.
