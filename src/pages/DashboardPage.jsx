import { useEffect, useState } from 'react';
import { getProducts } from '../api/products';
import { getClients } from '../api/clients';
import { getCashiers } from '../api/cashiers';
import { getInvoices } from '../api/invoices';
import PageHeader from '../components/PageHeader';
import styles from './DashboardPage.module.css';

function StatCard({ label, value, icon, color }) {
  return (
    <div className={styles.card} style={{ borderTop: `4px solid ${color}` }}>
      <div className={styles.cardIcon} style={{ background: color + '18' }}>{icon}</div>
      <div>
        <p className={styles.cardLabel}>{label}</p>
        <p className={styles.cardValue}>{value ?? '—'}</p>
      </div>
    </div>
  );
}

export default function DashboardPage() {
  const [stats, setStats] = useState({ products: null, clients: null, cashiers: null, invoices: null, revenue: null });

  useEffect(() => {
    Promise.allSettled([getProducts(), getClients(), getCashiers(), getInvoices()]).then(
      ([p, c, ca, i]) => {
        const invoices = i.status === 'fulfilled' ? i.value.data : [];
        const revenue = Array.isArray(invoices)
          ? invoices.reduce((acc, inv) => acc + (inv.total ?? inv.totalAmount ?? 0), 0)
          : 0;
        setStats({
          products: p.status === 'fulfilled' ? p.value.data.length : '?',
          clients: c.status === 'fulfilled' ? c.value.data.length : '?',
          cashiers: ca.status === 'fulfilled' ? ca.value.data.length : '?',
          invoices: Array.isArray(invoices) ? invoices.length : '?',
          revenue,
        });
      }
    );
  }, []);

  return (
    <>
      <PageHeader title="Dashboard" />
      <div className={styles.grid}>
        <StatCard label="Total Products" value={stats.products} icon="📦" color="#1d4ed8" />
        <StatCard label="Total Clients" value={stats.clients} icon="👥" color="#16a34a" />
        <StatCard label="Cashiers" value={stats.cashiers} icon="💼" color="#d97706" />
        <StatCard label="Invoices" value={stats.invoices} icon="🧾" color="#9333ea" />
        <StatCard
          label="Total Revenue"
          value={stats.revenue !== null ? `$${Number(stats.revenue).toLocaleString('en-US', { minimumFractionDigits: 2 })}` : null}
          icon="💰"
          color="#0891b2"
        />
      </div>
    </>
  );
}
