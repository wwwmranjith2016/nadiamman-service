# BillFlow - Billing Management Backend

A Spring Boot backend application for managing billing, invoices, products, suppliers, and clients. Now configured for MySQL database and ready for cloud deployment.

## Features

- **Invoice Management**: Create, update, and manage invoices
- **Product Catalog**: Manage product inventory and pricing
- **Supplier Management**: Track supplier information and warranties
- **Client Management**: Maintain client database
- **PDF Processing**: Generate and process PDF invoices
- **Reporting**: Generate various business reports
- **RESTful API**: Complete REST API for frontend integration

## Technology Stack

- **Backend**: Spring Boot 3.5.6
- **Java**: Version 17
- **Database**: MySQL
- **Build Tool**: Maven
- **Containerization**: Docker
- **Cloud Platform**: Render (recommended)

## Database Configuration

The application is configured to use MySQL with environment variables for security. **Important: Never commit database credentials to version control.**

### Environment Variables

Required environment variables:
- `SPRING_DATASOURCE_URL`: MySQL connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username  
- `SPRING_DATASOURCE_PASSWORD`: Database password

### Database Connection Examples

**Aiven Cloud MySQL:**
```
SPRING_DATASOURCE_URL=jdbc:mysql://your-mysql-host:port/your-database?ssl-mode=REQUIRED&useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-secure-password
```

**AWS RDS MySQL:**
```
SPRING_DATASOURCE_URL=jdbc:mysql://your-rds-endpoint:3306/your-database?useSSL=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
```

**Google Cloud SQL MySQL:**
```
SPRING_DATASOURCE_URL=jdbc:mysql://your-instance-ip:3306/your-database?useSSL=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
```

**Local MySQL:**
```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/billflow?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your-password
```

## Local Development

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker (optional)

### Running Locally

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd nadiamman-bill-service
   ```

2. **Run with Maven**
   ```bash
   mvn spring-boot:run
   ```

3. **Run with Docker**
   ```bash
   docker-compose up --build
   ```

4. **Access the application**
   - API Base URL: http://localhost:8080
   - Health Check: http://localhost:8080/actuator/health

## Cloud Deployment

### Deploy to Render (Recommended)

1. **Connect your repository to Render**
   - Go to [render.com](https://render.com)
   - Connect your GitHub/GitLab repository
   - Render will automatically detect the `render.yaml` file

2. **Configure environment variables** (optional)
   - The application uses environment variables for database configuration
   - Default connection to your Supabase PostgreSQL is pre-configured
   - For custom database, set these environment variables:
     - `SPRING_DATASOURCE_URL`
     - `SPRING_DATASOURCE_USERNAME`
     - `SPRING_DATASOURCE_PASSWORD`

3. **Deploy**
   - Render will automatically build and deploy your application
   - The deployment will use the Dockerfile for containerization
   - Health checks are configured at `/actuator/health`

### Manual Docker Deployment

1. **Build the Docker image**
   ```bash
   docker build -t billflow-backend .
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_DATASOURCE_URL=your-database-url \
     -e SPRING_DATASOURCE_USERNAME=your-username \
     -e SPRING_DATASOURCE_PASSWORD=your-password \
     billflow-backend
   ```

## API Endpoints

### Main Controllers
- **Invoice Controller**: `/api/invoices`
- **Product Controller**: `/api/products`
- **Supplier Controller**: `/api/suppliers`
- **Client Controller**: `/api/clients`
- **Quote Controller**: `/api/quotes`
- **Report Controller**: `/api/reports`
- **Warranty Controller**: `/api/warranties`

### Health Check
- **GET** `/actuator/health` - Application health status

## Configuration Profiles

The application supports three Spring profiles:

- **default**: Development profile (local Maven runs)
  - SQL logging enabled for debugging
  - Port 8080
  - Basic configuration

- **production**: Production profile (Render cloud deployment)
  - SQL logging disabled for performance
  - Optimized logging levels
  - Actuator endpoints enabled
  - Database connection optimizations

- **docker**: Docker-specific profile (docker-compose runs)
  - Production-like settings
  - Optimized for containerized environment
  - Connection pool settings
  - Health checks enabled

### Profile Activation:

- **Local Development**: No profile needed (uses default)
- **Render Deployment**: `SPRING_PROFILES_ACTIVE=production` (set in render.yaml)
- **Docker Compose**: `SPRING_PROFILES_ACTIVE=docker` (set in docker-compose.yml)

## Database Migrations

The application uses Hibernate auto-DDL with `update` strategy. On first run, it will automatically create the necessary tables.

## Security Features

- CORS configuration for frontend integration
- Input validation using Spring Boot Validation
- Non-root Docker user for security
- Environment variable-based configuration

## Monitoring

- Actuator endpoints enabled for monitoring
- Health checks configured for container orchestration
- Logging configured for production environments

## Troubleshooting

### Common Issues

1. **Database Connection Issues**
   
   **Error**: `java.net.SocketException: Network unreachable` or `SQLException: Access denied for user`
   
   **Solutions**:
   - **Verify Database Status**: Ensure your MySQL database is running and accessible
   - **Check Network Connectivity**: Verify the database host is reachable from your deployment environment
   - **Database Service Issues**: Some cloud providers may pause databases after inactivity
   - **Firewall/Security Groups**: Ensure database allows connections from your deployment IP range
   - **Connection String**: Verify the JDBC URL format is correct for your database provider
   - **Credentials**: Double-check username and password are correct
   - **SSL Configuration**: For cloud MySQL (like Aiven), ensure SSL is properly configured
   
   **For Aiven Cloud MySQL Users**:
   - Check if your database is paused due to inactivity
   - Verify your database is in an active project
   - Ensure SSL is enabled in connection string: `?ssl-mode=REQUIRED`
   - Check connection limits and user permissions
   
   **For Cloud Deployment**:
   - Set environment variables in your deployment platform
   - Ensure network connectivity between deployment and database
   - Check deployment platform logs for detailed error messages

2. **Port Issues**
   - Ensure port 8080 is available (or configure via PORT environment variable)
   - For Render deployment, the PORT environment variable is automatically set

3. **Memory Issues**
   - Java heap size can be configured via JAVA_OPTS environment variable
   - Default: -Xmx512m -Xms256m
   - Increase memory if experiencing OutOfMemoryError

4. **Build/Deployment Issues**
   - Ensure Maven build completes successfully: `mvn clean package`
   - Check Java version compatibility (requires Java 17+)
   - Verify all dependencies are available

### Database Connection Testing

To test database connectivity locally:

```bash
# Test connection with mysql client (if available)
mysql -h your-mysql-host -P your-port -u your-username -p

# Or use the health endpoint after deployment
curl http://localhost:8080/actuator/health
```

### Environment Variable Setup

**For Local Development (.env file):**
```bash
# Copy from .env.example and update values
cp .env.example .env
# Edit .env with your database credentials
```

**For Render Deployment:**
1. Go to your Render service dashboard
2. Navigate to Environment tab
3. Add the required environment variables:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME` 
   - `SPRING_DATASOURCE_PASSWORD`
   - `SPRING_PROFILES_ACTIVE=production`

### Health Check Endpoints

- Application: http://localhost:8080/actuator/health
- Docker health check: Automatic via HEALTHCHECK instruction in Dockerfile

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License.
