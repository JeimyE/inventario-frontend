import api from './axios';

export const getCategories = () => api.get('/api/categorias');
export const createCategory = (data) => api.post('/api/categorias', data);
export const deleteCategory = (id) => api.delete(`/api/categorias/${id}`);
