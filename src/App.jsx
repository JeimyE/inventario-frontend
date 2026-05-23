import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import ProductsPage from './pages/ProductsPage';
import CategoriesPage from './pages/CategoriesPage';
import ClientsPage from './pages/ClientsPage';
import CashiersPage from './pages/CashiersPage';
import InvoicesPage from './pages/InvoicesPage';
import StatisticsPage from './pages/StatisticsPage';

function PrivatePage({ children }) {
  return (
    <ProtectedRoute>
      <Layout>{children}</Layout>
    </ProtectedRoute>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={<PrivatePage><DashboardPage /></PrivatePage>} />
          <Route path="/products" element={<PrivatePage><ProductsPage /></PrivatePage>} />
          <Route path="/categories" element={<PrivatePage><CategoriesPage /></PrivatePage>} />
          <Route path="/clients" element={<PrivatePage><ClientsPage /></PrivatePage>} />
          <Route path="/cashiers" element={<PrivatePage><CashiersPage /></PrivatePage>} />
          <Route path="/invoices" element={<PrivatePage><InvoicesPage /></PrivatePage>} />
          <Route path="/statistics" element={<PrivatePage><StatisticsPage /></PrivatePage>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
