import { useEffect, useState } from 'react';
import { getCashiers, createCashier, updateCashier, deleteCashier } from '../api/cashiers';
import PageHeader from '../components/PageHeader';
import Modal from '../components/Modal';

const empty = { nombre: '', apellido: '', username: '', password: '', email: '' };

export default function CashiersPage() {
  const [cashiers, setCashiers] = useState([]);
  const [modal, setModal] = useState(false);
  const [form, setForm] = useState(empty);
  const [editId, setEditId] = useState(null);
  const [error, setError] = useState('');

  const load = () => getCashiers().then((r) => setCashiers(r.data));
  useEffect(() => { load(); }, []);

  const openCreate = () => { setForm(empty); setEditId(null); setError(''); setModal(true); };
  const openEdit = (c) => {
    setForm({
      nombre: c.nombre ?? c.firstName ?? c.name ?? '',
      apellido: c.apellido ?? c.lastName ?? '',
      username: c.username ?? '',
      password: '',
      email: c.email ?? '',
    });
    setEditId(c.id);
    setError('');
    setModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    const payload = { ...form };
    if (editId && !payload.password) delete payload.password;
    try {
      if (editId) await updateCashier(editId, payload);
      else await createCashier(payload);
      setModal(false);
      load();
    } catch (err) {
      setError(err.response?.data?.message ?? 'Error saving cashier.');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this cashier?')) return;
    try { await deleteCashier(id); load(); }
    catch { alert('Could not delete cashier.'); }
  };

  const field = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  return (
    <>
      <PageHeader title="Cashiers" action={<button className="btn btn-primary" onClick={openCreate}>+ New Cashier</button>} />
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Name</th><th>Username</th><th>Email</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {cashiers.length === 0 && (
              <tr><td colSpan={4} style={{ textAlign: 'center', color: '#94a3b8', padding: '2rem' }}>No cashiers found.</td></tr>
            )}
            {cashiers.map((c) => (
              <tr key={c.id}>
                <td>{`${c.nombre ?? c.firstName ?? c.name ?? ''} ${c.apellido ?? c.lastName ?? ''}`.trim()}</td>
                <td>{c.username ?? '—'}</td>
                <td>{c.email ?? '—'}</td>
                <td style={{ display: 'flex', gap: '0.5rem' }}>
                  <button className="btn btn-secondary btn-sm" onClick={() => openEdit(c)}>Edit</button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(c.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modal && (
        <Modal title={editId ? 'Edit Cashier' : 'New Cashier'} onClose={() => setModal(false)}>
          {error && <div className="alert alert-error">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="form-group"><label>First Name</label><input value={form.nombre} onChange={field('nombre')} required /></div>
            <div className="form-group"><label>Last Name</label><input value={form.apellido} onChange={field('apellido')} /></div>
            <div className="form-group"><label>Username</label><input value={form.username} onChange={field('username')} required /></div>
            <div className="form-group">
              <label>{editId ? 'New Password (leave blank to keep)' : 'Password'}</label>
              <input type="password" value={form.password} onChange={field('password')} required={!editId} />
            </div>
            <div className="form-group"><label>Email</label><input type="email" value={form.email} onChange={field('email')} /></div>
            <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end' }}>
              <button type="button" className="btn btn-secondary" onClick={() => setModal(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary">Save</button>
            </div>
          </form>
        </Modal>
      )}
    </>
  );
}
