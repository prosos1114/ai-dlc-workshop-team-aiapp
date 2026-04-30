import { describe, it, expect } from 'vitest';
import { passwordSchema, manualSetupSchema, orderCreateSchema } from '../schemas';

describe('passwordSchema', () => {
  it('should accept valid password (4+ chars)', () => {
    expect(passwordSchema.safeParse({ password: '1234' }).success).toBe(true);
    expect(passwordSchema.safeParse({ password: 'abcdef' }).success).toBe(true);
  });

  it('should reject short password', () => {
    expect(passwordSchema.safeParse({ password: '123' }).success).toBe(false);
    expect(passwordSchema.safeParse({ password: '' }).success).toBe(false);
  });
});

describe('manualSetupSchema', () => {
  it('should accept valid store code and total tables', () => {
    const result = manualSetupSchema.safeParse({ storeCode: 'cafe-abc', totalTables: 10 });
    expect(result.success).toBe(true);
  });

  it('should reject invalid store code characters', () => {
    const result = manualSetupSchema.safeParse({ storeCode: 'café abc!', totalTables: 10 });
    expect(result.success).toBe(false);
  });

  it('should reject empty store code', () => {
    const result = manualSetupSchema.safeParse({ storeCode: '', totalTables: 10 });
    expect(result.success).toBe(false);
  });

  it('should reject zero or negative total tables', () => {
    expect(manualSetupSchema.safeParse({ storeCode: 'abc', totalTables: 0 }).success).toBe(false);
    expect(manualSetupSchema.safeParse({ storeCode: 'abc', totalTables: -1 }).success).toBe(false);
  });

  it('should reject non-integer total tables', () => {
    expect(manualSetupSchema.safeParse({ storeCode: 'abc', totalTables: 1.5 }).success).toBe(false);
  });
});

describe('orderCreateSchema', () => {
  it('should accept valid order items', () => {
    const result = orderCreateSchema.safeParse({
      items: [
        { menuId: 1, menuName: '아메리카노', quantity: 2, unitPrice: 4500 },
      ],
    });
    expect(result.success).toBe(true);
  });

  it('should reject empty items array', () => {
    const result = orderCreateSchema.safeParse({ items: [] });
    expect(result.success).toBe(false);
  });

  it('should reject quantity over 99', () => {
    const result = orderCreateSchema.safeParse({
      items: [{ menuId: 1, menuName: 'test', quantity: 100, unitPrice: 1000 }],
    });
    expect(result.success).toBe(false);
  });

  it('should reject quantity of 0', () => {
    const result = orderCreateSchema.safeParse({
      items: [{ menuId: 1, menuName: 'test', quantity: 0, unitPrice: 1000 }],
    });
    expect(result.success).toBe(false);
  });

  it('should reject negative unit price', () => {
    const result = orderCreateSchema.safeParse({
      items: [{ menuId: 1, menuName: 'test', quantity: 1, unitPrice: -100 }],
    });
    expect(result.success).toBe(false);
  });
});
