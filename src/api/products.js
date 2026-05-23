import api from './axios';

export const getProducts = () => api.get('/api/productos');
export const getProduct = (id) => api.get(`/api/productos/${id}`);
export const createProduct = (data) => api.post('/api/productos', data);
export const updateProduct = (id, data) => api.put(`/api/productos/${id}`, data);
export const deleteProduct = (id) => api.delete(`/api/productos/${id}`);
