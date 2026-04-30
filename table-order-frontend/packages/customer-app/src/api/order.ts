import { apiClient, API } from '@table-order/shared';
import type { ApiResponse, Order, OrderCreateRequest } from '../types';

export async function createOrder(
  storeId: number,
  tableId: number,
  request: OrderCreateRequest
): Promise<Order> {
  const response = await apiClient.post<ApiResponse<Order>>(
    API.TABLE_ORDERS(storeId, tableId),
    request
  );
  return response.data.data;
}

export async function fetchOrders(storeId: number, tableId: number): Promise<Order[]> {
  const response = await apiClient.get<ApiResponse<Order[]>>(
    API.TABLE_ORDERS(storeId, tableId)
  );
  return response.data.data;
}
