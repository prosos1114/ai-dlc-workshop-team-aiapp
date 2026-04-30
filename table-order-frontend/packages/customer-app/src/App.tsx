import { Routes, Route, Navigate } from 'react-router-dom';

function App() {
  return (
    <Routes>
      <Route path="/setup" element={<div>Setup Page - Coming in Unit 4</div>} />
      <Route path="/menu" element={<div>Menu Page - Coming in Unit 4</div>} />
      <Route path="/cart" element={<div>Cart Page - Coming in Unit 4</div>} />
      <Route path="/order/confirm" element={<div>Order Confirm - Coming in Unit 4</div>} />
      <Route path="/orders" element={<div>Order History - Coming in Unit 4</div>} />
      <Route path="*" element={<Navigate to="/menu" replace />} />
    </Routes>
  );
}

export default App;
