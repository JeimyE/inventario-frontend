import { useEffect, useState } from 'react';
import { getInvoices, createInvoice, downloadInvoicePDF } from '../api/invoices';
import { getClients } from '../api/clients';
import { getCashiers } from '../api/cashiers';
import { getProducts } from '../api/products';
import PageHeader from '../components/PageHeader';
import Modal from '../components/Modal';
import styles from './InvoicesPage.module.css';

const emptyLine = { productoId: '', cantidad: 1, precioUnitario: 0 };

export default function InvoicesPage() {
  const [invoices, setInvoices] = useState([]);
  const [clients, setClients] = useState([]);
  const [cashiers, setCashiers] = useState([]);
  const [products, setProducts] = useState([]);
  const [modal, setModal] = useState(false);
  const [form, setForm] = useState({ clienteId: '', cajeroId: '', lineas: [{ ...emptyLine }] });
  const [error, setError] = useState('');

  const load = () =>
    Promise.all([getInvoices(), getClients(), getCashiers(), getProducts()]).then(
      ([inv, cli, caj, prod]) => {
        setInvoices(inv.data);
        setClients(cli.data);
        setCashiers(caj.data);
        setProducts(prod.data);
      }
    );

  useEffect(() => { load(); }, []);

  const openCreate = () => {
    setForm({ clienteId: '', cajeroId: '', lineas: [{ ...emptyLine }] });
    setError('');
    setModal(true);
  };

  const setLine = (i, key, val) =>
    setForm((f) => {
      const lineas = [...f.lineas];
      lineas[i] = { ...lineas[i], [key]: val };
      if (key === 'productoId') {
        const prod = products.find((p) => String(p.id) === String(val));
        lineas[i].precioUnitario = prod ? (prod.precio ?? prod.price ?? 0) : 0;
      }
      return { ...f, lineas };
    });

  const addLine = () => setForm((f) => ({ ...f, lineas: [...f.lineas, { ...emptyLine }] }));
  const removeLine = (i) => setForm((f) => ({ ...f, lineas: f.lineas.filter((_, idx) => idx !== i) }));

  const total = form.lineas.reduce((acc, l) => acc + Number(l.cantidad) * Number(l.precioUnitario), 0);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    const payload = {
      clienteId: form.clienteId,
      cajeroId: form.cajeroId,
      lineas: form.lineas.map((l) => ({
        productoId: l.productoId,
        cantidad: Number(l.cantidad),
        precioUnitario: Number(l.precioUnitario),
      })),
    };
    try {
      await createInvoice(payload);
      setModal(false);
      load();
    } catch (err) {
      setError(err.response?.data?.message ?? 'Error creating invoice.');
    }
  };

  const handleDownloadPDF = async (id) => {
    try {
      const res = await downloadInvoicePDF(id);
      const url = URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }));
      const a = document.createElement('a');
      a.href = url;
      a.download = `invoice-${id}.pdf`;
      a.click();
      URL.revokeObjectURL(url);
    } catch {
      alert('PDF not available.');
    }
  };

  const fmt = (n) => `$${Number(n ?? 0).toFixed(2)}`;

  return (
    <>
      <PageHeader title="Invoices" action={<button className="btn btn-primary" onClick={openCreate}>+ New Invoice</button>} />
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>#</th><th>Date</th><th>Client</th><th>Cashier</th><th>Total</th><th>PDF</th></tr>
          </thead>
          <tbody>
            {invoices.length === 0 && (
              <tr><td colSpan={6} style={{ textAlign: 'center', color: '#94a3b8', padding: '2rem' }}>No invoices found.</td></tr>
            )}
            {invoices.map((inv) => (
              <tr key={inv.id}>
                <td>{inv.id}</td>
                <td>{inv.fecha ? new Date(inv.fecha).toLocaleDateString() : inv.date ? new Date(inv.date).toLocaleDateString() : '—'}</td>
                <td>{inv.cliente?.nombre ?? inv.client?.name ?? inv.clienteNombre ?? '—'}</td>
                <td>{inv.cajero?.nombre ?? inv.cashier?.name ?? inv.cajeroNombre ?? '—'}</td>
                <td>{fmt(inv.total ?? inv.totalAmount)}</td>
                <td>
                  <button className="btn btn-secondary btn-sm" onClick={() => handleDownloadPDF(inv.id)}>⬇ PDF</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modal && (
        <Modal title="New Invoice" onClose={() => setModal(false)}>
          {error && <div className="alert alert-error">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Client</label>
              <select value={form.clienteId} onChange={(e) => setForm((f) => ({ ...f, clienteId: e.target.value }))} required>
                <option value="">Select client</option>
                {clients.map((c) => <option key={c.id} value={c.id}>{c.nombre ?? c.firstName ?? c.name} {c.apellido ?? c.lastName ?? ''}</option>)}
              </select>
            </div>
            <div className="form-group">
              <label>Cashier</label>
              <select value={form.cajeroId} onChange={(e) => setForm((f) => ({ ...f, cajeroId: e.target.value }))} required>
                <option value="">Select cashier</option>
                {cashiers.map((c) => <option key={c.id} value={c.id}>{c.nombre ?? c.firstName ?? c.name} {c.apellido ?? c.lastName ?? ''}</option>)}
              </select>
            </div>

            <p style={{ fontWeight: 600, marginBottom: '0.5rem', fontSize: '0.875rem' }}>Product Lines</p>
            {form.lineas.map((line, i) => (
              <div key={i} className={styles.lineRow}>
                <select
                  value={line.productoId}
                  onChange={(e) => setLine(i, 'productoId', e.target.value)}
                  required
                  style={{ flex: 2 }}
                >
                  <option value="">Product</option>
                  {products.map((p) => <option key={p.id} value={p.id}>{p.nombre ?? p.name}</option>)}
                </select>
                <input
                  type="number"
                  min="1"
                  value={line.cantidad}
                  onChange={(e) => setLine(i, 'cantidad', e.target.value)}
                  placeholder="Qty"
                  required
                  style={{ flex: 1 }}
                />
                <input
                  type="number"
                  step="0.01"
                  value={line.precioUnitario}
                  onChange={(e) => setLine(i, 'precioUnitario', e.target.value)}
                  placeholder="Price"
                  required
                  style={{ flex: 1 }}
                />
                {form.lineas.length > 1 && (
                  <button type="button" className="btn btn-danger btn-sm" onClick={() => removeLine(i)}>✕</button>
                )}
              </div>
            ))}
            <button type="button" className="btn btn-secondary btn-sm" onClick={addLine} style={{ marginBottom: '1rem' }}>+ Add Line</button>

            <div className={styles.total}>Total: <strong>{fmt(total)}</strong></div>

            <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end' }}>
              <button type="button" className="btn btn-secondary" onClick={() => setModal(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary">Create Invoice</button>
            </div>
          </form>
        </Modal>
      )}
    </>
  );
}
