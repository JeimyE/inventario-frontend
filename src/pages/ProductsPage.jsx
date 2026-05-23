import { useEffect, useState } from 'react';
import { getProducts, createProduct, updateProduct, deleteProduct } from '../api/products';
import { getCategories } from '../api/categories';
import PageHeader from '../components/PageHeader';
import Modal from '../components/Modal';

const empty = { nombre: '', descripcion: '', precio: '', stock: '', categoriaId: '' };

export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [filterCat, setFilterCat] = useState('');
  const [modal, setModal] = useState(null); // null | 'create' | 'edit'
  const [form, setForm] = useState(empty);
  const [editId, setEditId] = useState(null);
  const [error, setError] = useState('');

  const load = () =>
    Promise.all([getProducts(), getCategories()]).then(([p, c]) => {
      setProducts(p.data);
      setCategories(c.data);
    });

  useEffect(() => { load(); }, []);

  const openCreate = () => { setForm(empty); setEditId(null); setError(''); setModal('form'); };
  const openEdit = (p) => {
    setForm({
      nombre: p.nombre ?? p.name ?? '',
      descripcion: p.descripcion ?? p.description ?? '',
      precio: p.precio ?? p.price ?? '',
      stock: p.stock ?? '',
      categoriaId: p.categoriaId ?? p.categoria?.id ?? p.category?.id ?? '',
    });
    setEditId(p.id);
    setError('');
    setModal('form');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (editId) await updateProduct(editId, form);
      else await createProduct(form);
      setModal(null);
      load();
    } catch (err) {
      setError(err.response?.data?.message ?? 'Error saving product.');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this product?')) return;
    try { await deleteProduct(id); load(); }
    catch { alert('Could not delete product.'); }
  };

  const field = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  const visible = filterCat
    ? products.filter((p) => String(p.categoriaId ?? p.categoria?.id ?? p.category?.id) === filterCat)
    : products;

  return (
    <>
      <PageHeader
        title="Products"
        action={
          <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
            <select className="btn btn-secondary" value={filterCat} onChange={(e) => setFilterCat(e.target.value)}>
              <option value="">All categories</option>
              {categories.map((c) => (
                <option key={c.id} value={String(c.id)}>{c.nombre ?? c.name}</option>
              ))}
            </select>
            <button className="btn btn-primary" onClick={openCreate}>+ New Product</button>
          </div>
        }
      />
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Description</th>
              <th>Price</th>
              <th>Stock</th>
              <th>Category</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {visible.length === 0 && (
              <tr><td colSpan={6} style={{ textAlign: 'center', color: '#94a3b8', padding: '2rem' }}>No products found.</td></tr>
            )}
            {visible.map((p) => (
              <tr key={p.id}>
                <td>{p.nombre ?? p.name}</td>
                <td>{p.descripcion ?? p.description ?? '—'}</td>
                <td>${Number(p.precio ?? p.price ?? 0).toFixed(2)}</td>
                <td>{p.stock}</td>
                <td>{p.categoria?.nombre ?? p.category?.name ?? '—'}</td>
                <td style={{ display: 'flex', gap: '0.5rem' }}>
                  <button className="btn btn-secondary btn-sm" onClick={() => openEdit(p)}>Edit</button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(p.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modal === 'form' && (
        <Modal title={editId ? 'Edit Product' : 'New Product'} onClose={() => setModal(null)}>
          {error && <div className="alert alert-error">{error}</div>}
          <form onSubmit={handleSubmit}>
            <div className="form-group"><label>Name</label><input value={form.nombre} onChange={field('nombre')} required /></div>
            <div className="form-group"><label>Description</label><input value={form.descripcion} onChange={field('descripcion')} /></div>
            <div className="form-group"><label>Price</label><input type="number" step="0.01" value={form.precio} onChange={field('precio')} required /></div>
            <div className="form-group"><label>Stock</label><input type="number" value={form.stock} onChange={field('stock')} required /></div>
            <div className="form-group">
              <label>Category</label>
              <select value={form.categoriaId} onChange={field('categoriaId')} required>
                <option value="">Select category</option>
                {categories.map((c) => <option key={c.id} value={c.id}>{c.nombre ?? c.name}</option>)}
              </select>
            </div>
            <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end' }}>
              <button type="button" className="btn btn-secondary" onClick={() => setModal(null)}>Cancel</button>
              <button type="submit" className="btn btn-primary">Save</button>
            </div>
          </form>
        </Modal>
      )}
    </>
  );
}
