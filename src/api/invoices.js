import api from './axios';

export const getInvoices = () => api.get('/api/facturas');
export const getInvoice = (id) => api.get(`/api/facturas/${id}`);
export const createInvoice = (data) => api.post('/api/facturas', data);
export const downloadInvoicePDF = (id) =>
  api.get(`/api/facturas/${id}/pdf`, { responseType: 'blob' });
