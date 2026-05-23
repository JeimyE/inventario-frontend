import api from './axios';

export const getCashiers = () => api.get('/api/cajeros');
export const getCashier = (id) => api.get(`/api/cajeros/${id}`);
export const createCashier = (data) => api.post('/api/cajeros', data);
export const updateCashier = (id, data) => api.put(`/api/cajeros/${id}`, data);
export const deleteCashier = (id) => api.delete(`/api/cajeros/${id}`);
