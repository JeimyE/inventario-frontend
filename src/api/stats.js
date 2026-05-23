import api from './axios';

export const getSalesByDay = () => api.get('/api/estadisticas/ventas-por-dia');
export const getTopProducts = () => api.get('/api/estadisticas/top-productos');
export const getRevenueSummary = () => api.get('/api/estadisticas/resumen');
