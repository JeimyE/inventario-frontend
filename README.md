# POS Desktop Application

A JavaFX 21 desktop client for the Inventario POS system. Connects to a Spring Boot REST API via JWT authentication.

## Features

- **Login** — Authenticates cashiers, stores JWT for all subsequent requests
- **Dashboard** — Summary cards: total products, clients, invoices, and revenue
- **Products** — CRUD table with category filter
- **Clients** — CRUD table
- **Cashiers** — CRUD table
- **Categories** — Table with create and delete
- **Invoices** — Table, new invoice dialog with dynamic product lines, PDF download
- **Statistics** — Line chart (sales by day) and bar chart (top products)

## Requirements

- Java 17 or later (Java 21 also works)
- Maven 3.8+
- The Spring Boot backend running at `http://localhost:8080`

## Running the Backend First

The desktop app depends on the Spring Boot REST API. Start it before launching this app:

```bash
cd inventario-backend
mvn spring-boot:run
```

Verify the backend is up by opening `http://localhost:8080` in your browser.

## Running the Desktop App

### Option 1 — Maven (recommended)

```bash
cd inventario-pos-desktop
mvn javafx:run
```

### Option 2 — Build then run

```bash
mvn compile
mvn javafx:run
```

## Project Structure

```
src/main/java/com/pos/
├── MainApp.java               # Application entry point
├── controllers/               # FXML controllers for each screen
│   ├── LoginController.java
│   ├── MainController.java
│   ├── DashboardController.java
│   ├── ProductsController.java
│   ├── ClientsController.java
│   ├── CashiersController.java
│   ├── CategoriesController.java
│   ├── InvoicesController.java
│   ├── InvoiceDialogController.java
│   └── StatisticsController.java
├── models/                    # Data model classes (Gson-serializable)
├── services/                  # HTTP service layer (one per resource)
└── utils/                     # HttpUtil, SessionManager, AlertUtil

src/main/resources/com/pos/
├── fxml/                      # FXML UI layouts
└── css/dark-theme.css         # Dark Catppuccin-style theme
```

## API Endpoints Used

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/auth/login | Authenticate and get JWT |
| GET/POST/PUT/DELETE | /api/productos | Product CRUD |
| GET/POST/PUT/DELETE | /api/clientes | Client CRUD |
| GET/POST/PUT/DELETE | /api/cajeros | Cashier CRUD |
| GET/POST/DELETE | /api/categorias | Category management |
| GET/POST | /api/facturas | Invoice list and create |
| GET | /api/facturas/{id}/pdf | Download invoice PDF |
| GET | /api/estadisticas/resumen | Dashboard summary |
| GET | /api/estadisticas/ventas-por-dia | Sales by day chart |
| GET | /api/estadisticas/top-productos | Top products chart |

## Troubleshooting

- **Connection refused** — The backend is not running. Start it first.
- **401 Unauthorized** — Token expired. Log out and log in again.
- **Blank charts** — The statistics endpoints need data. Create some invoices first.
- **Java version error** — This app requires Java 17+. Run `java -version` to check.
