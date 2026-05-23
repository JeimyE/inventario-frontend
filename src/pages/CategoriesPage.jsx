import { useEffect, useState } from 'react';
import { getCategories, createCategory, deleteCategory } from '../api/categories';
import PageHeader from '../components/PageHeader';
import Modal from '../components/Modal';

export default function CategoriesPage() {
  const [categories, setCategories] = useState([]);
  const [modal, setModal] = useState(false);
  const [name, setName] = useState('');
  const [error, setError] = useState('');

  const load = () => getCategories().then((r) => setCategories(r.data));

  useEffect(() => { load(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await createCategory({ nombre: name, name });
      setModal(false);
      setName('');
      load();
    } catch (err) {
      setError(err.response?.data?.message ?? 'Error creating category.');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this category?')) return;
    try { await deleteCategory(id); load(); }
    catch { alert('Could not delete category.'); }
  };

  return (
    <>
      <PageHeader
        title="Categories"
        action={<button className="btn btn-primary" onClick={() => { setModal(true); setError(''); setName(''); }}>+ New Category</button>}
      />
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>ID</th><th>Name</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {categories.length === 0 && (
              <tr><td colSpan={3} style={{ textAlign: 'center', color: '#94a3b8', padding: '2rem' }}>No categories found.</td></tr>
            )}
            {categories.map((c) => (
              <tr key={c.id}>
                <td>{c.id}</td>
                <td>{c.nombre ?? c.name}</td>
                <td>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(c.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modal && (
        <Modal title="New Category" onClose={() => setModal(false)}>
          {error && <div className="alert alert-error">{error}</div>}
          <form onSubmit={handleCreate}>
            <div className="form-group"><label>Category Name</label><input value={name} onChange={(e) => setName(e.target.value)} required /></div>
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
