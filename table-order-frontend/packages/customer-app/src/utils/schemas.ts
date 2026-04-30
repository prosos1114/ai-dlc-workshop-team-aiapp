import { z } from 'zod';

export const passwordSchema = z.object({
  password: z.string().min(4, '비밀번호는 최소 4자리입니다'),
});

export const manualSetupSchema = z.object({
  storeCode: z
    .string()
    .min(1, '매장 코드를 입력해주세요')
    .regex(/^[a-zA-Z0-9-]+$/, '영문, 숫자, 하이픈만 사용 가능합니다'),
  totalTables: z
    .number({ invalid_type_error: '숫자를 입력해주세요' })
    .int('정수를 입력해주세요')
    .min(1, '1 이상의 값을 입력해주세요'),
});

export const orderItemSchema = z.object({
  menuId: z.number().positive(),
  menuName: z.string().min(1),
  quantity: z.number().int().min(1).max(99),
  unitPrice: z.number().int().min(0),
});

export const orderCreateSchema = z.object({
  items: z.array(orderItemSchema).min(1, '주문 항목이 비어있습니다'),
});

export type PasswordFormData = z.infer<typeof passwordSchema>;
export type ManualSetupFormData = z.infer<typeof manualSetupSchema>;
