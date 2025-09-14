# 🧪 Test Troubleshooting Guide

## Common Test Issues and Solutions

### 1. Database Connection Issues

**Problem**: Tests fail with database connection errors
**Solution**: We've switched to H2 in-memory database for tests

**Configuration**:
```properties
# In application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
```

### 2. Missing Dependencies

**Problem**: Tests fail due to missing test dependencies
**Solution**: Added H2 database dependency

**Added to pom.xml**:
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### 3. Environment Variables

**Problem**: Tests fail due to missing environment variables
**Solution**: Set test environment variables in GitHub Actions

**GitHub Actions Configuration**:
```yaml
env:
  SPRING_PROFILES_ACTIVE: test
  JWT_SECRET: test-secret-key-for-testing-only
  RAZORPAY_KEY_ID: rzp_test_test_key
  RAZORPAY_KEY_SECRET: test_secret
  RAZORPAY_WEBHOOK_SECRET: test_webhook_secret
  MAIL_USERNAME: test@example.com
  MAIL_PASSWORD: test-password
```

### 4. Spring Context Loading

**Problem**: Spring context fails to load during tests
**Solution**: Created simple test classes

**Test Classes Created**:
- `LunarEventManagementApplicationTests.java` - Basic context loading test
- `SimpleTest.java` - Minimal smoke test
- `AuthControllerTest.java` - Controller test example

## 🔧 How to Run Tests Locally

### 1. Run All Tests
```bash
mvn clean test -Dspring.profiles.active=test
```

### 2. Run Specific Test Class
```bash
mvn test -Dtest=SimpleTest -Dspring.profiles.active=test
```

### 3. Run Tests with Debug Output
```bash
mvn test -Dspring.profiles.active=test -X
```

## 🚀 GitHub Actions Test Flow

### What Happens in CI/CD:
1. **Checkout Code** - Gets your latest code
2. **Setup Java 17** - Installs required Java version
3. **Cache Dependencies** - Speeds up subsequent builds
4. **Run Tests** - Executes all test classes with test profile
5. **Build Application** - Creates production JAR (only if tests pass)
6. **Deploy** - Deploys to cloud platform (only if tests pass)

### Test Profile Configuration:
- **Database**: H2 in-memory (no external dependencies)
- **Security**: Test JWT secret
- **Email**: Mock configuration
- **Payment**: Test Razorpay keys
- **Logging**: Reduced verbosity

## 🐛 Debugging Test Failures

### 1. Check Test Logs
In GitHub Actions, click on the failed test job to see detailed logs.

### 2. Common Error Messages

**"Context failed to load"**
- Check if all required beans are available
- Verify test configuration is correct

**"Database connection failed"**
- Ensure H2 dependency is included
- Check test database configuration

**"Authentication failed"**
- Verify JWT secret is set
- Check security configuration

### 3. Local Debugging
```bash
# Run with detailed logging
mvn test -Dspring.profiles.active=test -Dlogging.level.com.lunar=DEBUG

# Run single test with debug
mvn test -Dtest=SimpleTest -Dspring.profiles.active=test -X
```

## ✅ Test Success Checklist

- [ ] All test classes compile without errors
- [ ] Spring context loads successfully
- [ ] Database tables are created automatically
- [ ] Security configuration works
- [ ] Controllers respond correctly
- [ ] No external dependencies required

## 🔄 If Tests Still Fail

### 1. Check GitHub Actions Logs
- Go to your repository on GitHub
- Click "Actions" tab
- Click on the failed workflow
- Check the "test" job logs

### 2. Common Fixes

**Add Missing Test Dependencies**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

**Fix Test Configuration**:
```properties
# Ensure test profile is active
spring.profiles.active=test
```

**Add Test Annotations**:
```java
@SpringBootTest
@ActiveProfiles("test")
class YourTestClass {
    // test methods
}
```

### 3. Create Minimal Test
If complex tests fail, create a simple test first:

```java
@SpringBootTest
@ActiveProfiles("test")
class MinimalTest {
    @Test
    void contextLoads() {
        // This should pass if Spring context loads
    }
}
```

## 📞 Getting Help

If tests still fail after following this guide:

1. **Check GitHub Actions logs** for specific error messages
2. **Run tests locally** to reproduce the issue
3. **Create a minimal test** to isolate the problem
4. **Check Spring Boot documentation** for testing best practices
5. **Create an issue** in your repository with error details

## 🎯 Success Indicators

When tests pass, you should see:
- ✅ All test classes execute successfully
- ✅ Spring context loads without errors
- ✅ Database operations work correctly
- ✅ GitHub Actions shows green checkmarks
- ✅ Build and deployment proceed automatically

Your tests are now configured to run reliably in GitHub Actions! 🚀
