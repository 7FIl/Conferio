import { Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Signup from './pages/Signup'
import Sessions from './pages/Sessions'
import Profile from './pages/Profile'
import OtherProfile from './pages/OtherProfile'
import MyProposals from './pages/MyProposals'
import ProposalList from './pages/ProposalList'
import FeedbackForm from './pages/FeedbackForm'
import CoordinatorDashboard from './pages/CoordinatorDashboard'
import AdminDashboard from './pages/AdminDashboard'
import Navbar from './components/Navbar'
import { useAuth } from './context/AuthContext'

function Protected({ children, roles }) {
  const { token, user } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  if (roles && !roles.includes(user?.role)) return <Navigate to="/sessions" replace />
  return children
}

export default function App() {
  return (
    <div className="min-h-screen">
      <Navbar />
      <div className="container mx-auto p-4">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          
          {/* Public protected routes */}
          <Route path="/sessions" element={<Protected><Sessions /></Protected>} />
          <Route path="/profile" element={<Protected><Profile /></Protected>} />
          <Route path="/profile/:username" element={<Protected><OtherProfile /></Protected>} />
          
          {/* User proposals */}
          <Route path="/my-proposals" element={<Protected><MyProposals /></Protected>} />
          
          {/* Coordinator routes */}
          <Route path="/proposals" element={<Protected roles={['COORDINATOR', 'ADMIN']}><ProposalList /></Protected>} />
          <Route path="/coordinator-dashboard" element={<Protected roles={['COORDINATOR']}><CoordinatorDashboard /></Protected>} />
          
          {/* Feedback */}
          <Route path="/feedback/session/:sessionId" element={<Protected><FeedbackForm /></Protected>} />
          
          {/* Admin routes */}
          <Route path="/admin-dashboard" element={<Protected roles={['ADMIN']}><AdminDashboard /></Protected>} />
          
          <Route path="*" element={<Navigate to="/sessions" replace />} />
        </Routes>
      </div>
    </div>
  )
}
