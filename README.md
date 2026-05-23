# POS System — React Frontend

A complete Point of Sale frontend built with React 18 + Vite, connecting to a Spring Boot REST API.

## Features

| Page | Capabilities |
|---|---|
| Login | JWT authentication |
| Dashboard | Summary cards (products, clients, cashiers, invoices, revenue) |
| Products | List, create, edit, delete with category filter |
| Categories | List and create categories |
| Clients | List, create, edit, delete |
| Cashiers | List, create, edit, delete |
| Invoices | List all, create with dynamic product lines, download PDF |
| Statistics | Sales by day (bar chart), top products (pie chart), revenue summary |

## Requirements

- Node.js 18+
- Spring Boot API running at `http://localhost:8080`

## Installation

```bash
git clone <your-repo-url>
cd inventario-frontend
npm install
```

## Running the app

```bash
npm run dev
```

Open [http://localhost:5173](http://localhost:5173) in your browser.

## Connecting to the backend

The API base URL is configured in `src/api/axios.js`:

```js
const api = axios.create({
  baseURL: 'http://localhost:8080',
});
```

Change the `baseURL` value if your Spring Boot server runs on a different host or port.

### Expected API endpoints

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/login` | Login — returns `{ token }` |
| GET/POST/PUT/DELETE | `/api/productos` | Products CRUD |
| GET/POST/DELETE | `/api/categorias` | Categories |
| GET/POST/PUT/DELETE | `/api/clientes` | Clients CRUD |
| GET/POST/PUT/DELETE | `/api/cajeros` | Cashiers CRUD |
| GET/POST | `/api/facturas` | Invoices |
| GET | `/api/facturas/{id}/pdf` | Invoice PDF (binary) |

> The statistics page builds charts from the raw `/api/facturas` and `/api/productos` data, so no separate statistics endpoints are required.

## Authentication

After login, the JWT is stored in `localStorage` under the key `token`. Every subsequent request includes `Authorization: Bearer <token>` via an Axios interceptor. A 401 from the API clears the token and redirects to `/login`.

## Project structure

```
src/
  api/          # Axios instance + per-resource API helpers
  components/   # Shared UI: Layout, Sidebar, Modal, PageHeader
  context/      # AuthContext (JWT state)
  pages/        # One file per page/route
```

## Build for production

```bash
npm run build
```

Output is placed in `dist/`.
