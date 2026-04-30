import { useState } from 'react';
import { PlaceholderImage } from './PlaceholderImage';

interface OptimizedImageProps {
  src: string | null;
  alt: string;
  className?: string;
}

export function OptimizedImage({ src, alt, className = '' }: OptimizedImageProps) {
  const [hasError, setHasError] = useState(false);
  const [isLoaded, setIsLoaded] = useState(false);

  if (!src || hasError) {
    return <PlaceholderImage className={className} />;
  }

  return (
    <div className={`relative overflow-hidden ${className}`}>
      {!isLoaded && (
        <div className="absolute inset-0 bg-gray-100 animate-pulse" />
      )}
      <img
        src={src}
        alt={alt}
        loading="lazy"
        onLoad={() => setIsLoaded(true)}
        onError={() => setHasError(true)}
        className={`w-full h-full object-cover transition-opacity duration-300 ${
          isLoaded ? 'opacity-100' : 'opacity-0'
        }`}
        data-testid="optimized-image"
      />
    </div>
  );
}
