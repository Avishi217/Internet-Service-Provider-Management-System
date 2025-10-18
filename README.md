# ISP Management System

A comprehensive Java Swing desktop application for Internet Service Provider (ISP) management, featuring a modern UI with phone-based authentication and MySQL database backend.

## 🚀 Features

### Admin Dashboard
- **Customer Management**: Add, update, delete, and view customer details with phone-based accounts
- **Plans Management**: Manage internet service plans with sequential IDs
- **Billing & Payments**: Generate bills and track payment history
- **Complaints Management**: Track and resolve customer complaints
- **Reports & Analytics**: Business insights including revenue analytics, customer statistics, billing reports, and plan popularity
- **Dashboard Metrics**: Real-time statistics with analytics cards

### Customer Portal
- **Self-Service Dashboard**: View account information and statistics
- **Profile Management**: Update personal information (name, email, phone, address)
- **Plan Management**: View current plan details
- **Billing**: View bills and payment history
- **Complaints**: Register and track complaints with ticket system
- **Usage Tracking**: Monitor service metrics

## 🏗️ Project Structure

```
ISP1/
├── backend/
│   └── src/main/java/com/isp/
│       ├── dao/              # Data Access Objects
│       │   ├── UserDAO.java
│       │   ├── CustomerDAO.java
│       │   ├── PlanDAO.java
│       │   ├── BillDAO.java
│       │   ├── PaymentDAO.java
│       │   └── ComplaintDAO.java
│       ├── model/            # Entity Models
│       │   ├── User.java
│       │   ├── Customer.java
│       │   ├── Plan.java
│       │   ├── Bill.java
│       │   ├── Payment.java
│       │   ├── Complaint.java
│       │   └── UsageLog.java
│       ├── service/          # Business Logic
│       │   ├── AuthService.java
│       │   └── DashboardAnalyticsService.java
│       └── util/             # Utilities
│           ├── DatabaseConnection.java
│           └── PasswordUtil.java
├── frontend/
│   └── src/main/java/com/isp/ui/
│       ├── components/       # Reusable UI Components
│       │   ├── ModernButton.java
│       │   ├── ModernTextField.java
│       │   ├── ModernPasswordField.java
│       │   └── DashboardCard.java
│       ├── utils/            # UI Utilities
│       │   └── UIConstants.java
│       └── views/            # Main Views
│           ├── LoginWindow.java
│           ├── AdminDashboard.java
│           └── CustomerDashboard.java
├── database/
│   ├── schema.sql                            # Database schema
│   ├── sample_data_v2_realistic.sql          # Realistic test data
│   ├── test_data_25_users_comprehensive.sql  # Complete test dataset
│   ├── quick_test_data.sql                   # Quick testing
│   ├── add_real_test_users.sql               # Auth test users
│   └── ensure_payments_table.sql             # Payment table verification
├── pom.xml                  # Maven configuration
├── setup.sh                 # Automated setup script
├── .gitignore              # Git ignore rules
├── README.md               # This file
└── QUICKSTART.md           # Quick start guide
```

## 🛠️ Technology Stack

- **Frontend**: Java Swing (Desktop Application)
- **Backend**: Java SE, JDBC
- **Database**: MySQL 8.0
- **Build Tool**: Maven 3.x
- **Authentication**: Phone-based OTP system
- **Additional Libraries**:
  - MySQL Connector/J 8.0.33 (Database connectivity)
  - BCrypt (Password hashing)

## 📋 Prerequisites

- Java Development Kit (JDK) 11 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code with Java extensions)

## 🚀 Getting Started

### Quick Setup (Recommended)

Use the automated setup script:

```bash
cd /Users/truptijain/Desktop/ISP1
chmod +x setup.sh
./setup.sh
```

The script will:
1. Check for MySQL, Maven, and Java
2. Create the `isp_management` database
3. Load the database schema
4. Build the project with Maven

### Manual Setup

#### 1. Navigate to Project Directory
```bash
cd /Users/truptijain/Desktop/ISP1
```

#### 2. Set Up the Database

Start MySQL and create the database:
```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS isp_management;"
mysql -u root -p isp_management < database/schema.sql
```

Optional: Load sample data
```bash
mysql -u root -p isp_management < database/sample_data_v2_realistic.sql
```

#### 3. Configure Database Connection

Database credentials are configured in:
`backend/src/main/resources/application.properties`

```properties
db.url=jdbc:mysql://localhost:3306/isp_management
db.username=root
db.password=root
```

#### 4. Build the Project

```bash
mvn clean package -DskipTests
```

#### 5. Run the Application

```bash
java -jar target/isp-management-system-1.0.0-jar-with-dependencies.jar
```

## 👤 Default Login Credentials

**Login Method:** Phone-based authentication

### Admin Account
- **Phone**: 9999999999
- **Password**: password123
- **Role**: Administrator

### Sample Customer Accounts
- **Phone**: 9876543210 - Amit Kumar
- **Phone**: 9876543211 - Priya Sharma  
- **Phone**: 9876543212 - Rahul Verma
- **Password** (all): password123

**Note**: The system uses phone numbers for login with OTP-based authentication.

## 🗄️ Database Schema

The application uses the following main tables:

- **users**: Phone-based authentication and user management
- **customers**: Customer profiles with plan assignments
- **plans**: Internet service plans (17 plans: voice, unlimited, data top-ups)
- **bills**: Billing records with payment status tracking
- **payments**: Payment transaction history
- **complaints**: Customer complaint tickets with status tracking
- **usage_logs**: Data usage monitoring (future implementation)

**Current Database Stats:**
- 27 Users (1 admin, 26 customers)
- 24 Customer profiles
- 17 Internet plans
- 20+ Complaints tracked

## 🎨 UI Design

The application features a modern, vibrant interface:

- **Color Scheme**: Vibrant gradient colors (purple, blue, orange, green)
- **Modern Components**: Custom-styled buttons with hover effects, text fields, and animated cards
- **Responsive Layout**: Sidebar navigation with dynamic content panels
- **Card-based Dashboard**: Clean metrics presentation with icons
- **Reports & Analytics**: 
  - Revenue Analytics (6 metrics with icons)
  - Customer Analytics (3 categories)
  - Billing Statistics
  - Complaint Analytics  
  - Plan Popularity Charts

## 📦 Building for Production

To create a standalone executable JAR with all dependencies:

```bash
mvn clean package
```

The JAR file will be created in the `target/` directory:
- `isp-management-system-1.0.0-jar-with-dependencies.jar`

## 🔧 Configuration

### Database Configuration
Update database connection settings in:
- `backend/src/main/resources/application.properties`

```properties
db.url=jdbc:mysql://localhost:3306/isp_management
db.username=root
db.password=root
```

### UI Customization
Modify UI constants and colors in:
- `frontend/src/main/java/com/isp/ui/utils/UIConstants.java`

### Build Configuration
Maven configuration in:
- `pom.xml` (Java 11, MySQL Connector 8.0.33, BCrypt)

## 📝 Recent Updates & Features

### ✅ Implemented
- ✅ Phone-based authentication system
- ✅ Customer account creation with phone validation
- ✅ Manual complaint submission and tracking
- ✅ Sequential plan IDs (1-17)
- ✅ Enhanced Reports & Analytics visibility
- ✅ Revenue Analytics with 6 key metrics
- ✅ Removed Employee management features
- ✅ Simplified Profile management (removed password change)
- ✅ DashboardAnalyticsService for business intelligence
- ✅ Project optimization (760KB, 72 files)

### 🚀 Future Enhancements
- [ ] Live Dashboard with auto-refresh (SwingWorker)
- [ ] Animated StatCard components
- [ ] Count-up number animations
- [ ] Email notifications for bills and complaints
- [ ] SMS integration for OTP
- [ ] Advanced data visualization charts
- [ ] Payment gateway integration
- [ ] Automated bill generation scheduler
- [ ] Customer usage analytics dashboard

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Development

### Project Setup in IDE (VS Code / IntelliJ)

1. Open project folder: `/Users/truptijain/Desktop/ISP1`
2. Import as Maven project
3. Set JDK 11 or higher
4. Update Maven dependencies: `mvn clean install`
5. Configure database in `application.properties`
6. Run `LoginWindow.java` main class

### Code Structure

- **Models**: POJOs representing database entities (User, Customer, Plan, Bill, Payment, Complaint)
- **DAOs**: Database operations using JDBC PreparedStatements
- **Services**: Business logic (AuthService, DashboardAnalyticsService)
- **Utils**: Helper classes (DatabaseConnection, PasswordUtil)
- **UI Components**: Reusable Swing components (ModernButton, DashboardCard)
- **Views**: Main application screens (LoginWindow, AdminDashboard, CustomerDashboard)

### Build Commands

```bash
# Clean build
mvn clean package -DskipTests

# Run application
java -jar target/isp-management-system-1.0.0-jar-with-dependencies.jar

# Run with Maven
mvn exec:java -Dexec.mainClass="com.isp.ui.views.LoginWindow"
```

### Database Commands

```bash
# Access MySQL
mysql -u root -proot isp_management

# Load sample data
mysql -u root -proot isp_management < database/sample_data_v2_realistic.sql

# Check database status
mysql -u root -proot isp_management -e "SHOW TABLES;"
```

## 📞 Support

For issues or questions:
1. Check `QUICKSTART.md` for quick setup instructions
2. Review database connection in `application.properties`
3. Ensure MySQL is running: `mysql -u root -proot -e "SELECT 1"`
4. Rebuild project: `mvn clean package -DskipTests`

## 📊 Project Stats

- **Total Size**: 760 KB (optimized)
- **Source Files**: 72 files
- **Java Classes**: 40+
- **Database Tables**: 7 tables
- **Test Data**: 6 SQL files available
- **Build Time**: ~30 seconds

---

**Built with ❤️ for ISP Management**  
*Last Updated: October 15, 2025*
