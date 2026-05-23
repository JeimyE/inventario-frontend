import { useEffect, useState } from 'react';
import { getClients, createClient, updateClient, deleteClient } from '../api/clients';
import PageHeader from '../components/PageHeader';
import Modal from '../components/Modal';

const empty = { nombre: '', apellido: '', email: '', telefono: '', cedula: '' };

export default function ClientsPage() {
  const [clients, setClients] = useState([]);
  const [modal, setModal] = useState(false);
  const [form, setForm] = useState(empty);
  const [editId, setEditId] = useState(null);
  const [error, setError] = useState('');

  const load = () => getClients().then((r) => setClients(r.data));
  useEffect(() => { load(); }, []);

  const openCreate = () => { setForm(empty); setEditId(null); setError(''); setModal(true); };
  const openEdit = (c) => {
    setForm({
      nombre: c.nombre ?? c.firstName ?? c.name ?? '',
      apellido: c.apellido ?? c.lastName ?? '',
      email: c.email ?? '',
      telefono: c.telefono ?? c.phone ?? '',
      cedula: c.cedula ?? c.dni ?? '',
    });
    setEditId(c.id);
    setError('');
    setModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (editId) await updateClient(editId, form);
      else await createClient(form);
      setModal(false);
      load();
    } catch (err) {
      setError(err.response?.data?.message ?? 'Error saving client.');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this client?')) return;
    try { await deleteClient(id); load(); }
    catch { alert('Could not delete client.'); }
  };

  const field = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  return (
    <>
      <PageHeader title="Clients" action={<button className="btn btn-primary" onClick={openCreate}>+ New Client</button>} />
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Name</th><th>Email</th><th>Phone</th><th>ID / Cedula</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {clients.length === 0 && (
              <tr><td colSpan={5} style={{ textAlign: 'center', color: '#94a3b8', padding: '2rem' }}>No clients found.</td></tr>
            )}
            {clients.map((c) => (
              <tr key={c.id}>
                <td>{`${c.nombre ?? c.firstName ?? c.name ?? ''} ${c.apellido ?? c.lastName ?? ''}`.trim()}</td>
                <td>{c.email ?? '—'}</td>
                <td>{c.telefono ?? c.phone ?? '—'}</td>
                <td>{c.cedula ?? c.dni ?? '—'}</td>
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
        <Modal title={editId ? 'Edit Client' : 'New Client'} onClose={() => setModal(false)}>
          {error && <div className="alert alert-error">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="form-group"><label>First Name</label><input value={form.nombre} onChange={field('nombre')} required /></div>
            <div className="form-group"><label>Last Name</label><input value={form.apellido} onChange={field('apellido')} /></div>
            <div className="form-group"><label>Email</label><input type="email" value={form.email} onChange={field('email')} /></div>
            <div className="form-group"><label>Phone</label><input value={form.telefono} onChange={field('telefono')} /></div>
            <div className="form-group"><label>Cedula / ID</label><input value={form.cedula} onChange={field('cedula')} /></div>
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
