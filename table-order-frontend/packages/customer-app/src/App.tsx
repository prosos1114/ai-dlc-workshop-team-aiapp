import { lazy, Suspense } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { ErrorBoundary } from './components/common/ErrorBoundary';
import { LoadingSpinner } from './components/common/LoadingSpinner';
import { Toast } from './components/common/Toast';
import { AuthGuard } from './components/layout/AuthGuard';
import { AppLayout } from './components/layout/AppLayout';
import MenuPage from './pages/MenuPage';

const SetupPage = lazy(() => import('./pages/SetupPage'));
const OrderConfirmPage = lazy(() => import('./pages/OrderConfirmPage'));
const OrderSuccessPage = lazy(() => import('./pages/OrderSuccessPage'));
const OrderHistoryPage = lazy(() => import('./pages/OrderHistoryPage'));

function App() {
  return (
    <ErrorBoundary>
      <Suspense fallback={<div className="flex items-center justify-center h-screen"><LoadingSpinner size="lg" /></div>}>
        <Routes>
          <Route path="/setup" element={<SetupPage />} />
          <Route
            element={
              <AuthGuard>
                <AppLayout />
              </AuthGuard>
            }
          >
            <Route path="/menu" element={<MenuPage />} />
            <Route path="/order-confirm" element={<OrderConfirmPage />} />
            <Route path="/order-success" element={<OrderSuccessPage />} />
            <Route path="/order-history" element={<OrderHistoryPage />} />
          </Route>
          <Route path="*" element={<Navigate to="/menu" replace />} />
        </Routes>
      </Suspense>
      <Toast />
    </ErrorBoundary>
  );
}

export default App;
