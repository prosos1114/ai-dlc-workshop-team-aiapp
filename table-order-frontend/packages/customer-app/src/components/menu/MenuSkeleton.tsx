export function MenuSkeleton() {
  return (
    <div className="grid grid-cols-3 gap-4 p-4" data-testid="menu-skeleton">
      {Array.from({ length: 6 }).map((_, i) => (
        <div key={i} className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden animate-pulse">
          <div className="h-32 bg-gray-200" />
          <div className="p-3 space-y-2">
            <div className="h-4 bg-gray-200 rounded w-3/4" />
            <div className="h-4 bg-gray-200 rounded w-1/2" />
          </div>
        </div>
      ))}
    </div>
  );
}
