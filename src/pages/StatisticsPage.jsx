import { useEffect, useState } from 'react';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend,
} from 'recharts';
import { getInvoices } from '../api/invoices';
import { getProducts } from '../api/products';
import PageHeader from '../components/PageHeader';
import styles from './StatisticsPage.module.css';

const COLORS = ['#1d4ed8', '#16a34a', '#d97706', '#9333ea', '#0891b2', '#dc2626'];

function buildSalesByDay(invoices) {
  const map = {};
  invoices.forEach((inv) => {
    const raw = inv.fecha ?? inv.date ?? inv.createdAt;
    if (!raw) return;
    const day = new Date(raw).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    map[day] = (map[day] ?? 0) + Number(inv.total ?? inv.totalAmount ?? 0);
  });
  return Object.entries(map)
    .sort((a, b) => new Date(a[0]) - new Date(b[0]))
    .map(([day, total]) => ({ day, total: +total.toFixed(2) }));
}

function buildTopProducts(invoices, products) {
  const map = {};
  invoices.forEach((inv) => {
    const lines = inv.lineas ?? inv.lines ?? inv.detalles ?? [];
    lines.forEach((l) => {
      const id = l.productoId ?? l.producto?.id ?? l.product?.id;
      if (!id) return;
      map[id] = (map[id] ?? 0) + Number(l.cantidad ?? l.quantity ?? 0);
    });
  });
  return Object.entries(map)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 6)
    .map(([id, qty]) => {
      const prod = products.find((p) => String(p.id) === String(id));
      return { name: prod?.nombre ?? prod?.name ?? `#${id}`, qty };
    });
}

export default function StatisticsPage() {
  const [salesByDay, setSalesByDay] = useState([]);
  const [topProducts, setTopProducts] = useState([]);
  const [revenue, setRevenue] = useState(0);
  const [invoiceCount, setInvoiceCount] = useState(0);
  const [avgTicket, setAvgTicket] = useState(0);

  useEffect(() => {
    Promise.allSettled([getInvoices(), getProducts()]).then(([inv, prod]) => {
      const invoices = inv.status === 'fulfilled' && Array.isArray(inv.value.data) ? inv.value.data : [];
      const products = prod.status === 'fulfilled' && Array.isArray(prod.value.data) ? prod.value.data : [];
      const total = invoices.reduce((acc, i) => acc + Number(i.total ?? i.totalAmount ?? 0), 0);
      setSalesByDay(buildSalesByDay(invoices));
      setTopProducts(buildTopProducts(invoices, products));
      setRevenue(total);
      setInvoiceCount(invoices.length);
      setAvgTicket(invoices.length ? total / invoices.length : 0);
    });
  }, []);

  const fmt = (n) => `$${Number(n).toLocaleString('en-US', { minimumFractionDigits: 2 })}`;

  return (
    <>
      <PageHeader title="Statistics" />

      <div className={styles.summaryGrid}>
        <div className="card">
          <p style={{ color: '#64748b', fontSize: '0.8rem', fontWeight: 500 }}>Total Revenue</p>
          <p style={{ fontSize: '1.75rem', fontWeight: 700, color: '#1d4ed8' }}>{fmt(revenue)}</p>
        </div>
        <div className="card">
          <p style={{ color: '#64748b', fontSize: '0.8rem', fontWeight: 500 }}>Total Invoices</p>
          <p style={{ fontSize: '1.75rem', fontWeight: 700, color: '#16a34a' }}>{invoiceCount}</p>
        </div>
        <div className="card">
          <p style={{ color: '#64748b', fontSize: '0.8rem', fontWeight: 500 }}>Avg. Ticket</p>
          <p style={{ fontSize: '1.75rem', fontWeight: 700, color: '#9333ea' }}>{fmt(avgTicket)}</p>
        </div>
      </div>

      <div className={styles.chartsGrid}>
        <div className="card">
          <h2 className={styles.chartTitle}>Sales by Day</h2>
          {salesByDay.length === 0
            ? <p className={styles.empty}>No data available.</p>
            : (
              <ResponsiveContainer width="100%" height={250}>
                <BarChart data={salesByDay}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" />
                  <XAxis dataKey="day" tick={{ fontSize: 12 }} />
                  <YAxis tick={{ fontSize: 12 }} />
                  <Tooltip formatter={(v) => fmt(v)} />
                  <Bar dataKey="total" fill="#1d4ed8" radius={[4,4,0,0]} />
                </BarChart>
              </ResponsiveContainer>
            )
          }
        </div>

        <div className="card">
          <h2 className={styles.chartTitle}>Top Products (by quantity sold)</h2>
          {topProducts.length === 0
            ? <p className={styles.empty}>No data available.</p>
            : (
              <ResponsiveContainer width="100%" height={250}>
                <PieChart>
                  <Pie data={topProducts} dataKey="qty" nameKey="name" cx="50%" cy="50%" outerRadius={90} label>
                    {topProducts.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            )
          }
        </div>
      </div>
    </>
  );
}
