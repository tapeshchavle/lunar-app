# Lunar Event Management & Ticketing System

A comprehensive Spring Boot application for managing events, ticket sales, bookings, and payments with advanced features like QR code generation, email notifications, and real-time analytics.

## 🚀 Features

### Core Features
- **User Management**: Registration, authentication, profile management with JWT
- **Event Management**: Create, update, publish, and manage events
- **Ticket Management**: Multiple ticket types with pricing and availability
- **Booking System**: Complete booking workflow with payment integration
- **Payment Processing**: Stripe integration for secure payments
- **QR Code Generation**: Unique QR codes for ticket validation
- **Email Notifications**: Automated emails for bookings and confirmations
- **Review System**: User reviews and ratings for events
- **Admin Dashboard**: Comprehensive admin panel for system management

### Advanced Features
- **Real-time Analytics**: Event performance metrics and revenue tracking
- **Search & Filtering**: Advanced search with multiple filters
- **File Upload**: Image uploads for events and user profiles
- **Caching**: Redis caching for improved performance
- **Security**: JWT-based authentication with role-based access control
- **API Documentation**: Swagger/OpenAPI documentation
- **Monitoring**: Health checks and metrics with Actuator

## 🛠️ Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.5.5**
- **Spring Security** with JWT
- **Spring Data JPA**
- **PostgreSQL** database
- **Flyway** for database migrations
- **Maven** for dependency management

### Additional Libraries
- **Stripe** for payment processing
- **ZXing** for QR code generation
- **iText** for PDF generation
- **Thumbnailator** for image processing
- **Jackson** for JSON processing
- **Lombok** for reducing boilerplate code

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher
- Git

## 🚀 Getting Started

### Quick Setup (Recommended)
```bash
# Clone the repository
git clone <repository-url>
cd lunar-app

# Run the automated setup script
chmod +x setup.sh
./setup.sh
```

The setup script will:
- Check system requirements
- Create environment configuration
- Build the application
- Start PostgreSQL database
- Run database migrations
- Start the application

### Manual Setup

#### 1. Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher
- Docker (optional, for containerized setup)

#### 2. Database Setup
Create a PostgreSQL database:
```sql
CREATE DATABASE lunar_events;
CREATE USER lunar_user WITH PASSWORD 'lunar_password';
GRANT ALL PRIVILEGES ON DATABASE lunar_events TO lunar_user;
```

#### 3. Environment Configuration
Create a `.env` file with your credentials:
```bash
# Database Configuration
POSTGRES_DB=lunar_events
POSTGRES_USER=lunar_user
POSTGRES_PASSWORD=lunar_password

# Email Configuration
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password

# Razorpay Configuration
RAZORPAY_KEY_ID=rzp_test_your_key_id
RAZORPAY_KEY_SECRET=your_razorpay_secret
RAZORPAY_WEBHOOK_SECRET=your_webhook_secret
```

#### 4. Build and Run
```bash
# Build the application
mvn clean install

# Run database migrations
mvn flyway:migrate

# Start the application
mvn spring-boot:run
```

#### 5. Docker Setup (Alternative)
```bash
# Build and start with Docker Compose
docker-compose up -d

# Or build Docker image manually
docker build -t lunar-app .
docker run -p 8080:8080 lunar-app
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - User logout
- `GET /api/auth/me` - Get current user
- `PUT /api/auth/profile` - Update user profile
- `POST /api/auth/change-password` - Change password

### Event Endpoints
- `POST /api/events` - Create event
- `GET /api/events` - Get all events (paginated)
- `GET /api/events/{id}` - Get event by ID
- `PUT /api/events/{id}` - Update event
- `DELETE /api/events/{id}` - Delete event
- `POST /api/events/search` - Search events
- `GET /api/events/featured` - Get featured events
- `GET /api/events/upcoming` - Get upcoming events
- `POST /api/events/{id}/publish` - Publish event

### Booking Endpoints
- `POST /api/bookings` - Create booking
- `GET /api/bookings` - Get user bookings
- `GET /api/bookings/{id}` - Get booking by ID
- `PUT /api/bookings/{id}/cancel` - Cancel booking
- `GET /api/bookings/{id}/tickets` - Get booking tickets

### Payment Endpoints
- `POST /api/payments` - Process payment
- `GET /api/payments` - Get payment history
- `POST /api/payments/{id}/refund` - Process refund
- `POST /api/payments/webhook` - Stripe webhook

### Ticket Endpoints
- `GET /api/tickets` - Get user tickets
- `GET /api/tickets/{id}` - Get ticket by ID
- `POST /api/tickets/{id}/transfer` - Transfer ticket
- `POST /api/tickets/validate` - Validate QR code

## 🗄️ Database Schema

### Core Tables
- **users** - User accounts and profiles
- **events** - Event information and details
- **ticket_types** - Different types of tickets for events
- **bookings** - User bookings and reservations
- **booking_items** - Individual items in a booking
- **tickets** - Generated tickets with QR codes
- **payments** - Payment transactions and history
- **reviews** - User reviews and ratings
- **event_tags** - Tags for categorizing events

## 🔐 Security

- JWT-based authentication
- Role-based access control (USER, ORGANIZER, ADMIN, SUPER_ADMIN)
- Password encryption with BCrypt
- CORS configuration for frontend integration
- Input validation and sanitization

## 📊 Monitoring

- Spring Boot Actuator endpoints
- Health checks at `/api/health`
- Metrics at `/api/actuator/metrics`
- Prometheus metrics support

## 🧪 Testing

Run tests with:
```bash
mvn test
```

## 📝 Configuration

Key configuration properties in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/lunar_events
spring.datasource.username=lunar_user
spring.datasource.password=lunar_password

# JWT
jwt.secret=lunar-event-management-secret-key-2024-very-long-and-secure
jwt.expiration=86400000
jwt.refresh-expiration=604800000

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}

# Stripe
stripe.secret-key=${STRIPE_SECRET_KEY}
stripe.publishable-key=${STRIPE_PUBLISHABLE_KEY}
stripe.webhook-secret=${STRIPE_WEBHOOK_SECRET}
```

## 🚀 Deployment

### Docker Deployment
```bash
# Build Docker image
docker build -t lunar-event-management .

# Run with Docker Compose
docker-compose up -d
```

### Production Considerations
- Use environment variables for sensitive configuration
- Set up proper database backups
- Configure SSL/TLS certificates
- Set up monitoring and logging
- Use a reverse proxy (Nginx)
- Configure rate limiting

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

## 🔄 Version History

- **v1.0.0** - Initial release with core features
- **v1.1.0** - Added payment integration
- **v1.2.0** - Added QR code generation
- **v1.3.0** - Added email notifications
- **v1.4.0** - Added admin dashboard

---

**Lunar Event Management System** - Making event management simple and efficient! 🌙
