import { apiClient, API } from '@table-order/shared';
import type { ApiResponse, TableTokenResponse } from '../types';

export async function tableLogin(
  storeCode: string,
  password: string
): Promise<TableTokenResponse> {
  const response = await apiClient.post<ApiResponse<TableTokenResponse>>(
    API.TABLE_LOGIN,
    { storeCode, password }
  );
  return response.data.data;
}
