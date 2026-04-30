import { Routes, Route, Navigate } from 'react-router-dom';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<div>Login Page - Coming in Unit 5</div>} />
      <Route path="/register" element={<div>Register Page - Coming in Unit 5</div>} />
      <Route path="/store/register" element={<div>Store Register - Coming in Unit 5</div>} />
      <Route path="/dashboard" element={<div>Dashboard - Coming in Unit 5</div>} />
      <Route path="/tables" element={<div>Table Manage - Coming in Unit 5</div>} />
      <Route path="/menus" element={<div>Menu Manage - Coming in Unit 5</div>} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;
