# Quick Start Guide - ISP Management System

## 📁 Project Structure

```
ISP-Management-System/
├── 📂 backend/                    # Backend (Business Logic & Data)
│   └── src/main/java/com/isp/
│       ├── 📂 dao/                # Database Access Layer
│       │   ├── UserDAO.java
│       │   ├── CustomerDAO.java
│       │   └── PlanDAO.java
│       ├── 📂 model/              # Data Models
│       │   ├── User.java
│       │   ├── Customer.java
│       │   ├── Employee.java
│       │   ├── Plan.java
│       │   ├── Bill.java
│       │   ├── Payment.java
│       │   ├── Complaint.java
│       │   └── UsageLog.java
│       ├── 📂 service/            # Business Logic
│       │   └── AuthService.java
│       └── 📂 util/               # Utilities
│           ├── DatabaseConnection.java
│           └── PasswordUtil.java
│
├── 📂 frontend/                   # Frontend (User Interface)
│   └── src/main/java/com/isp/ui/
│       ├── 📂 components/         # Reusable UI Components
│       │   ├── ModernButton.java
│       │   ├── ModernTextField.java
│       │   ├── ModernPasswordField.java
│       │   └── DashboardCard.java
│       ├── 📂 utils/              # UI Utilities
│       │   └── UIConstants.java
│       └── 📂 views/              # Main Screens
│           ├── LoginWindow.java
│           ├── AdminDashboard.java
│           └── CustomerDashboard.java
│
├── 📂 database/                   # Database Scripts
│   └── schema.sql
│
├── 📄 pom.xml                     # Maven Build Configuration
├── 📄 README.md                   # Full Documentation
├── 🔧 setup.sh                    # Setup Script (Mac/Linux)
└── 🔧 setup.bat                   # Setup Script (Windows)
```

## 🚀 Quick Setup (3 Steps)

### Step 1: Configure Database Connection

Edit `backend/src/main/java/com/isp/util/DatabaseConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/isp_management";
private static final String USERNAME = "root";
private static final String PASSWORD = "YOUR_MYSQL_PASSWORD";  // ← Change this!
```

### Step 2: Run Setup Script

**On Mac/Linux:**
```bash
cd ~/ISP-Management-System
./setup.sh
```

**On Windows:**
```cmd
cd ISP-Management-System
setup.bat
```

### Step 3: Run the Application

```bash
mvn exec:java -Dexec.mainClass="com.isp.ui.views.LoginWindow"
```

## 🔑 Login Credentials

| Role     | Username   | Password    |
|----------|------------|-------------|
| Admin    | admin      | admin123    |
| Customer | john_doe   | password123 |
| Employee | bob_wilson | password123 |

## 📝 Manual Setup (Alternative)

If the setup script doesn't work:

1. **Create Database:**
   ```bash
   mysql -u root -p < database/schema.sql
   ```

2. **Build Project:**
   ```bash
   mvn clean install
   ```

3. **Run Application:**
   ```bash
   java -jar target/isp-management-system-1.0.0-jar-with-dependencies.jar
   ```

## 🎯 What Each Folder Does

### Backend Folder
- **dao/**: Database queries and CRUD operations
- **model/**: Java classes representing database tables
- **service/**: Business logic (authentication, billing, etc.)
- **util/**: Helper classes (database connection, password hashing)

### Frontend Folder
- **components/**: Custom Swing components (buttons, text fields, cards)
- **utils/**: UI constants (colors, fonts, dimensions)
- **views/**: Main application screens (login, dashboards)

### Database Folder
- **schema.sql**: Complete database structure with sample data

## 🔧 Common Issues & Solutions

### Issue: "Cannot connect to database"
**Solution:** Update database credentials in `DatabaseConnection.java`

### Issue: "MySQL JDBC Driver not found"
**Solution:** Run `mvn clean install` to download dependencies

### Issue: "Port 3306 already in use"
**Solution:** Stop other MySQL instances or change port in connection URL

## 📊 Features by User Role

### Admin Can:
✅ Manage customers (add, edit, delete, view)  
✅ Manage employees  
✅ Create and manage internet plans  
✅ Generate bills  
✅ Track payments  
✅ Manage complaints and assign to employees  
✅ View reports and analytics  

### Customer Can:
✅ View their profile and plan details  
✅ Check data usage  
✅ View and pay bills  
✅ Register complaints  
✅ Change plans  

### Employee Can:
✅ View assigned complaints  
✅ Update complaint status  
✅ Access customer information  

## 🎨 UI Design Features

- Modern Airtel-inspired color scheme (Red & Blue)
- Card-based dashboard layout
- Sidebar navigation
- Custom styled components
- Responsive layouts
- Clean and intuitive interface

## 📦 Technologies Used

- **Frontend:** Java Swing (GUI)
- **Backend:** Java 11, JDBC
- **Database:** MySQL 8.0
- **Build:** Maven
- **PDF Generation:** iText
- **Security:** SHA-256 password hashing

## 🔄 Next Steps After Setup

1. Login as **admin** to explore the admin dashboard
2. Add new customers and employees
3. Create custom internet plans
4. Generate bills and track payments
5. Test the customer portal with existing customers
6. Explore complaint management features

## 📞 Need Help?

- Check the full README.md for detailed documentation
- Review the database schema in `database/schema.sql`
- Examine sample data to understand the data structure
- Test with provided sample users

## 🎓 Learning Resources

This project demonstrates:
- MVC architecture in Java
- JDBC database connectivity
- Java Swing GUI development
- Authentication and authorization
- CRUD operations
- Custom UI components
- SQL database design

---

**Happy Coding! 🚀**
