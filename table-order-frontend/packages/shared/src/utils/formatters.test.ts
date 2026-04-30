import { describe, it, expect } from 'vitest';
import { formatPrice, formatDate, formatOrderStatus, getStatusColor } from './formatters';

describe('formatPrice', () => {
  it('가격을 원화 형식으로 포맷팅한다', () => {
    expect(formatPrice(25000)).toBe('₩25,000');
  });

  it('0원을 포맷팅한다', () => {
    expect(formatPrice(0)).toBe('₩0');
  });

  it('큰 금액을 포맷팅한다', () => {
    expect(formatPrice(1000000)).toBe('₩1,000,000');
  });

  it('천 단위 미만 금액을 포맷팅한다', () => {
    expect(formatPrice(500)).toBe('₩500');
  });
});

describe('formatDate', () => {
  it('날짜를 한국어 형식으로 포맷팅한다', () => {
    const result = formatDate('2026-04-30T12:00:00');
    expect(result).toBe('2026.04.30 12:00');
  });

  it('한 자리 월/일을 0으로 패딩한다', () => {
    const result = formatDate('2026-01-05T09:05:00');
    expect(result).toBe('2026.01.05 09:05');
  });

  it('자정 시간을 올바르게 포맷팅한다', () => {
    const result = formatDate('2026-12-31T00:00:00');
    expect(result).toBe('2026.12.31 00:00');
  });
});

describe('formatOrderStatus', () => {
  it('PENDING을 대기중으로 변환한다', () => {
    expect(formatOrderStatus('PENDING')).toBe('대기중');
  });

  it('PREPARING을 준비중으로 변환한다', () => {
    expect(formatOrderStatus('PREPARING')).toBe('준비중');
  });

  it('COMPLETED를 완료로 변환한다', () => {
    expect(formatOrderStatus('COMPLETED')).toBe('완료');
  });

  it('알 수 없는 상태는 그대로 반환한다', () => {
    expect(formatOrderStatus('UNKNOWN')).toBe('UNKNOWN');
  });
});

describe('getStatusColor', () => {
  it('PENDING은 노란색 클래스를 반환한다', () => {
    expect(getStatusColor('PENDING')).toBe('text-yellow-600 bg-yellow-100');
  });

  it('PREPARING은 파란색 클래스를 반환한다', () => {
    expect(getStatusColor('PREPARING')).toBe('text-blue-600 bg-blue-100');
  });

  it('COMPLETED는 초록색 클래스를 반환한다', () => {
    expect(getStatusColor('COMPLETED')).toBe('text-green-600 bg-green-100');
  });

  it('알 수 없는 상태는 회색 클래스를 반환한다', () => {
    expect(getStatusColor('UNKNOWN')).toBe('text-gray-600 bg-gray-100');
  });
});
