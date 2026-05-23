import api from './axios';

export const getClients = () => api.get('/api/clientes');
export const getClient = (id) => api.get(`/api/clientes/${id}`);
export const createClient = (data) => api.post('/api/clientes', data);
export const updateClient = (id, data) => api.put(`/api/clientes/${id}`, data);
export const deleteClient = (id) => api.delete(`/api/clientes/${id}`);
