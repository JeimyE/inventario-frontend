import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import styles from './Sidebar.module.css';

const links = [
  { to: '/', label: 'Dashboard', icon: '🏠' },
  { to: '/products', label: 'Products', icon: '📦' },
  { to: '/categories', label: 'Categories', icon: '🏷️' },
  { to: '/clients', label: 'Clients', icon: '👥' },
  { to: '/cashiers', label: 'Cashiers', icon: '💼' },
  { to: '/invoices', label: 'Invoices', icon: '🧾' },
  { to: '/statistics', label: 'Statistics', icon: '📊' },
];

export default function Sidebar() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <aside className={styles.sidebar}>
      <div className={styles.logo}>
        <span>POS System</span>
      </div>
      <nav className={styles.nav}>
        {links.map(({ to, label, icon }) => (
          <NavLink
            key={to}
            to={to}
            end={to === '/'}
            className={({ isActive }) =>
              `${styles.link} ${isActive ? styles.active : ''}`
            }
          >
            <span className={styles.icon}>{icon}</span>
            <span>{label}</span>
          </NavLink>
        ))}
      </nav>
      <button className={styles.logout} onClick={handleLogout}>
        🚪 Logout
      </button>
    </aside>
  );
}
