import { CalendarDays, LogOut, UserCircle, Menu } from 'lucide-react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useState } from 'react'

export default function Navbar() {
  const { user, logout } = useAuth()
  const [showMenu, setShowMenu] = useState(false)

  const menuItems = [
    { label: 'Sessions', path: '/sessions', roles: ['USER', 'COORDINATOR', 'ADMIN'] },
    { label: 'My Proposals', path: '/my-proposals', roles: ['USER', 'COORDINATOR', 'ADMIN'] },
    { label: 'Profile', path: '/profile', roles: ['USER', 'COORDINATOR', 'ADMIN'] },
    { label: 'Proposals Review', path: '/proposals', roles: ['COORDINATOR', 'ADMIN'] },
    { label: 'Coordinator Dashboard', path: '/coordinator-dashboard', roles: ['COORDINATOR'] },
    { label: 'Admin Dashboard', path: '/admin-dashboard', roles: ['ADMIN'] },
  ]

  const visibleMenuItems = user
    ? menuItems.filter(item => item.roles.includes(user.role))
    : []

  return (
    <nav className="bg-white border-b shadow-sm">
      <div className="container mx-auto px-4 py-3">
        <div className="flex items-center justify-between mb-3">
          <Link to="/sessions" className="flex items-center gap-2 font-semibold">
            <CalendarDays className="w-5 h-5" /> Conferio
          </Link>
          <div className="flex items-center gap-4">
            {user ? (
              <>
                <span className="flex items-center gap-1 text-sm text-gray-700">
                  <UserCircle className="w-5 h-5" /> {user.username} ({user.role})
                </span>
                <button
                  onClick={logout}
                  className="inline-flex items-center gap-1 bg-gray-100 hover:bg-gray-200 px-3 py-1 rounded text-sm"
                >
                  <LogOut className="w-4 h-4" /> Logout
                </button>
              </>
            ) : (
              <Link to="/login" className="text-sm">Login</Link>
            )}
          </div>
        </div>

        {/* Menu Items */}
        {user && visibleMenuItems.length > 0 && (
          <div className="flex flex-wrap gap-2">
            {visibleMenuItems.map(item => (
              <Link
                key={item.path}
                to={item.path}
                className="px-3 py-1 text-sm rounded hover:bg-blue-100 hover:text-blue-700 transition"
              >
                {item.label}
              </Link>
            ))}
          </div>
        )}
      </div>
    </nav>
  )
}

