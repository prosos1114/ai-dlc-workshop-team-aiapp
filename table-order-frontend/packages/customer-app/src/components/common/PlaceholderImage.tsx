import { ImageOff } from 'lucide-react';

interface PlaceholderImageProps {
  className?: string;
}

export function PlaceholderImage({ className = '' }: PlaceholderImageProps) {
  return (
    <div
      className={`flex items-center justify-center bg-gray-100 ${className}`}
      data-testid="placeholder-image"
      role="img"
      aria-label="이미지 없음"
    >
      <ImageOff className="w-8 h-8 text-gray-300" />
    </div>
  );
}
