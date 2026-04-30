import { Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { StoreRegisterPage } from './pages/StoreRegisterPage';
import { DashboardPage } from './pages/DashboardPage';
import { TableManagePage } from './pages/TableManagePage';
import { MenuManagePage } from './pages/MenuManagePage';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/store/register" element={<StoreRegisterPage />} />
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/tables" element={<TableManagePage />} />
      <Route path="/menus" element={<MenuManagePage />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;
