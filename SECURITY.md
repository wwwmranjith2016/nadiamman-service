# Security Guide

## Environment Variables Setup

### For Local Development

1. **Create a `.env` file** (this file is ignored by git):
   ```bash
   # Copy the example file
   cp .env.example .env
   ```

2. **Edit `.env` file** with your actual database credentials:
   ```bash
   SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/your-db-name
   SPRING_DATASOURCE_USERNAME=your-username
   SPRING_DATASOURCE_PASSWORD=your-secure-password
   ```

3. **Never commit `.env` file** to version control (already in .gitignore)

### For Cloud Deployment (Render)

1. **Set environment variables in Render dashboard**:
   - Go to your service in Render dashboard
   - Navigate to Environment tab
   - Add these variables:
     ```
     SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/your-db-name
     SPRING_DATASOURCE_USERNAME=your-username
     SPRING_DATASOURCE_PASSWORD=your-secure-password
     SPRING_PROFILES_ACTIVE=production
     ```

2. **Or use Render Database** (recommended):
   - The `render.yaml` file includes database configuration
   - Render will automatically inject database credentials
   - No manual environment variable setup needed

## Security Best Practices

### 1. **Never Hardcode Credentials**
- ✅ Use environment variables
- ✅ Use secure credential storage
- ❌ Don't put passwords in code or config files

### 2. **Database Security**
- Use strong, unique passwords
- Enable SSL connections for production
- Restrict database access by IP if possible
- Regularly rotate credentials

### 3. **Application Security**
- Run application as non-root user (already implemented)
- Use HTTPS in production
- Enable CORS properly
- Validate all inputs
- Use Spring Security for authentication

### 4. **Environment Security**
- Keep dependencies updated
- Use secrets management for production
- Monitor for vulnerabilities
- Regular security audits

## Production Checklist

- [ ] Remove all hardcoded credentials
- [ ] Use environment variables for all sensitive data
- [ ] Enable HTTPS
- [ ] Configure proper CORS settings
- [ ] Set up monitoring and logging
- [ ] Enable database SSL
- [ ] Use strong database passwords
- [ ] Enable application security headers
- [ ] Set up backup strategy
- [ ] Configure rate limiting

## Credential Rotation

If you need to rotate your database credentials:

1. **Update database password**
2. **Update environment variables** in your deployment platform
3. **Restart the application**
4. **Verify the connection works**

## Monitoring

The application includes health check endpoints for monitoring:
- `/actuator/health` - Application health
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application information

## Troubleshooting

### Common Issues

1. **Database connection fails**
   - Check environment variables are set correctly
   - Verify database is accessible from deployment environment
   - Check firewall/security group settings

2. **Application won't start**
   - Verify all required environment variables are set
   - Check application logs for specific errors
   - Ensure database is reachable

3. **Credentials exposed**
   - Check git history for accidentally committed credentials
   - Rotate compromised credentials immediately
   - Review access logs

## Contact

For security issues or questions, please contact the development team.