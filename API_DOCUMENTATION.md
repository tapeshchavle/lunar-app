# Lunar Event Management System - API Documentation

## Overview
This document provides comprehensive API documentation for the Lunar Event Management & Ticketing System. The API is built with Spring Boot and provides endpoints for user management, event management, booking, payment processing, and ticket management.

## Base URL
```
http://localhost:8080/api
```

## Authentication
The API uses JWT (JSON Web Token) for authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### 1. Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "john@example.com",
  "password": "password123"
}
```

#### Refresh Token
```http
POST /api/auth/refresh
Content-Type: application/x-www-form-urlencoded

refreshToken=your_refresh_token
```

#### Get Current User
```http
GET /api/auth/me
Authorization: Bearer <token>
```

#### Update Profile
```http
PUT /api/auth/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "profileImageUrl": "https://example.com/image.jpg"
}
```

#### Change Password
```http
POST /api/auth/change-password
Authorization: Bearer <token>
Content-Type: application/x-www-form-urlencoded

currentPassword=old_password&newPassword=new_password
```

### 2. Event Management Endpoints

#### Create Event
```http
POST /api/events
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Tech Conference 2024",
  "description": "Annual technology conference",
  "startDate": "2024-06-15T09:00:00",
  "endDate": "2024-06-15T17:00:00",
  "venueName": "Convention Center",
  "venueAddress": "123 Main St, City, State",
  "city": "New York",
  "category": "CONFERENCE",
  "maxAttendees": 500,
  "isPublic": true
}
```

#### Get All Events
```http
GET /api/events?page=0&size=10&sort=startDate,asc
```

#### Get Event by ID
```http
GET /api/events/{id}
```

#### Search Events
```http
POST /api/events/search
Content-Type: application/json

{
  "searchTerm": "tech",
  "category": "CONFERENCE",
  "city": "New York",
  "startDate": "2024-06-01T00:00:00",
  "endDate": "2024-06-30T23:59:59"
}
```

#### Get Featured Events
```http
GET /api/events/featured
```

#### Get Upcoming Events
```http
GET /api/events/upcoming
```

#### Update Event
```http
PUT /api/events/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated Event Title",
  "description": "Updated description"
}
```

#### Publish Event
```http
POST /api/events/{id}/publish
Authorization: Bearer <token>
```

#### Delete Event
```http
DELETE /api/events/{id}
Authorization: Bearer <token>
```

### 3. Booking Management Endpoints

#### Create Booking
```http
POST /api/bookings
Authorization: Bearer <token>
Content-Type: application/json

{
  "eventId": 1,
  "tickets": [
    {
      "ticketTypeId": 1,
      "quantity": 2,
      "specialInstructions": "Vegetarian meal required"
    }
  ],
  "bookingNotes": "Special requirements",
  "specialRequirements": "Wheelchair accessible seating"
}
```

#### Get Booking by ID
```http
GET /api/bookings/{id}
Authorization: Bearer <token>
```

#### Get Booking by Reference
```http
GET /api/bookings/reference/{reference}
Authorization: Bearer <token>
```

#### Get User Bookings
```http
GET /api/bookings?page=0&size=10
Authorization: Bearer <token>
```

#### Get Event Bookings
```http
GET /api/bookings/event/{eventId}
Authorization: Bearer <token>
```

#### Confirm Booking
```http
POST /api/bookings/{id}/confirm
Authorization: Bearer <token>
```

#### Cancel Booking
```http
POST /api/bookings/{id}/cancel?reason=Change of plans
Authorization: Bearer <token>
```

#### Check-in Booking
```http
POST /api/bookings/{id}/checkin
Authorization: Bearer <token>
```

### 4. Payment Management Endpoints

#### Create Payment
```http
POST /api/payments
Authorization: Bearer <token>
Content-Type: application/json

{
  "bookingId": 1
}
```

#### Verify Payment
```http
POST /api/payments/verify
Content-Type: application/x-www-form-urlencoded

paymentId=pay_123&razorpayOrderId=order_123&razorpaySignature=signature_123
```

#### Process Refund
```http
POST /api/payments/{id}/refund
Authorization: Bearer <token>
Content-Type: application/x-www-form-urlencoded

refundAmount=100.00&reason=Customer request
```

#### Get Payment by ID
```http
GET /api/payments/{id}
Authorization: Bearer <token>
```

#### Get User Payments
```http
GET /api/payments?page=0&size=10
Authorization: Bearer <token>
```

#### Get Booking Payments
```http
GET /api/payments/booking/{bookingId}
Authorization: Bearer <token>
```

#### Razorpay Webhook
```http
POST /api/payments/webhook
Content-Type: application/json
X-Razorpay-Signature: webhook_signature

{
  "event": "payment.captured",
  "payload": {
    "payment": {
      "id": "pay_123",
      "order_id": "order_123",
      "status": "captured"
    }
  }
}
```

### 5. Ticket Management Endpoints

#### Get User Tickets
```http
GET /api/tickets?page=0&size=10
Authorization: Bearer <token>
```

#### Get Ticket by ID
```http
GET /api/tickets/{id}
Authorization: Bearer <token>
```

#### Get Booking Tickets
```http
GET /api/tickets/booking/{bookingId}
Authorization: Bearer <token>
```

#### Validate Ticket
```http
POST /api/tickets/validate
Content-Type: application/x-www-form-urlencoded

qrCode=LUNAR_TICKET|1|1|TKT123456
```

#### Use Ticket
```http
POST /api/tickets/{id}/use?usedBy=Event Staff
Authorization: Bearer <token>
```

#### Transfer Ticket
```http
POST /api/tickets/{id}/transfer
Authorization: Bearer <token>
Content-Type: application/x-www-form-urlencoded

newUserId=2&notes=Transfer to friend
```

#### Get Ticket QR Code
```http
GET /api/tickets/qr/{id}
Authorization: Bearer <token>
```

## Response Formats

### Success Response
```json
{
  "id": 1,
  "message": "Operation successful",
  "data": {
    // Response data
  }
}
```

### Error Response
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/events"
}
```

### Validation Error Response
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Validation failed for the provided data",
  "path": "/api/events",
  "validationErrors": {
    "title": "Title is required",
    "startDate": "Start date must be in the future"
  }
}
```

## Status Codes

- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Access denied
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource conflict
- `500 Internal Server Error` - Server error

## Pagination

Most list endpoints support pagination using query parameters:
- `page` - Page number (0-based)
- `size` - Number of items per page
- `sort` - Sort criteria (e.g., `startDate,asc`)

Example:
```
GET /api/events?page=0&size=10&sort=startDate,asc
```

## Filtering and Search

### Event Search
The event search endpoint supports multiple filters:
- `searchTerm` - Search in title, description, and city
- `category` - Filter by event category
- `city` - Filter by city
- `startDate` - Filter by start date range
- `endDate` - Filter by end date range
- `isOnline` - Filter online/offline events
- `isFeatured` - Filter featured events

### Date Formats
All dates should be in ISO 8601 format:
```
2024-06-15T09:00:00
2024-06-15T09:00:00Z
2024-06-15T09:00:00+05:30
```

## Rate Limiting
API requests are rate-limited to prevent abuse. Current limits:
- Authentication endpoints: 10 requests per minute
- Other endpoints: 100 requests per minute

## Webhooks

### Razorpay Payment Webhooks
The system supports Razorpay webhooks for payment events:
- `payment.captured` - Payment successfully captured
- `payment.failed` - Payment failed

Webhook URL: `POST /api/payments/webhook`

## Security

### JWT Token
- Access tokens expire in 24 hours
- Refresh tokens expire in 7 days
- Tokens include user ID, username, email, and role

### CORS
CORS is configured to allow requests from:
- `http://localhost:3000` (React development)
- `http://localhost:3001` (Alternative frontend)

### Role-Based Access Control
- `USER` - Can book events and manage own data
- `ORGANIZER` - Can create and manage events
- `ADMIN` - Full system access
- `SUPER_ADMIN` - System administration

## Testing

### Test Data
You can use the following test credentials:
```
Email: admin@lunar.com
Password: admin123
Role: ADMIN
```

### Postman Collection
A Postman collection is available with all API endpoints pre-configured.

## Support

For API support and questions:
- Create an issue in the repository
- Contact the development team
- Check the system logs for detailed error information

---

**Last Updated:** January 2024  
**API Version:** 1.0.0
