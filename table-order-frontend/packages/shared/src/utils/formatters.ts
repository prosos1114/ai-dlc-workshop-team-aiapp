/**
 * 가격을 원화 형식으로 포맷팅
 * @example formatPrice(25000) => "₩25,000"
 */
export function formatPrice(price: number): string {
  return `₩${price.toLocaleString('ko-KR')}`;
}

/**
 * 날짜를 한국어 형식으로 포맷팅
 * @example formatDate("2026-04-30T12:00:00") => "2026.04.30 12:00"
 */
export function formatDate(dateString: string): string {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${year}.${month}.${day} ${hours}:${minutes}`;
}

/**
 * 주문 상태를 한국어로 변환
 */
export function formatOrderStatus(status: string): string {
  const statusMap: Record<string, string> = {
    PENDING: '대기중',
    PREPARING: '준비중',
    COMPLETED: '완료',
  };
  return statusMap[status] ?? status;
}

/**
 * 주문 상태별 색상 클래스 반환
 */
export function getStatusColor(status: string): string {
  const colorMap: Record<string, string> = {
    PENDING: 'text-yellow-600 bg-yellow-100',
    PREPARING: 'text-blue-600 bg-blue-100',
    COMPLETED: 'text-green-600 bg-green-100',
  };
  return colorMap[status] ?? 'text-gray-600 bg-gray-100';
}
