import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import api from '../api/http'

async function getAllUsers() {
  const { data } = await api.get('/api/users')
  return data
}

async function updateUserRole(userId, role) {
  const { data } = await api.put(`/api/users/${userId}/role`, { role })
  return data
}

async function deleteUser(userId) {
  await api.delete(`/api/users/${userId}`)
}

export default function AdminDashboard() {
  const [filterRole, setFilterRole] = useState('ALL')
  const [editingId, setEditingId] = useState(null)
  const [newRole, setNewRole] = useState({})
  const qc = useQueryClient()
  
  const { data: users, isLoading, error } = useQuery({
    queryKey: ['admin-users'],
    queryFn: getAllUsers
  })

  const updateRoleMutation = useMutation({
    mutationFn: ({ userId, role }) => updateUserRole(userId, role),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['admin-users'] })
      setEditingId(null)
      setNewRole({})
    },
    onError: (error) => {
      alert('Failed to update role: ' + (error.response?.data?.message || error.message))
    }
  })

  const deleteUserMutation = useMutation({
    mutationFn: deleteUser,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['admin-users'] })
    },
    onError: (error) => {
      alert('Failed to delete user: ' + (error.response?.data?.message || error.message))
    }
  })

  const filteredUsers = filterRole === 'ALL' 
    ? users 
    : users?.filter(u => u.role === filterRole)

  if (isLoading) return <div className="p-4">Loading users...</div>
  if (error) return <div className="p-4 text-red-600">Failed to load users: {error.message}</div>

  return (
    <div className="p-6">
      <h2 className="text-3xl font-bold mb-6">User Management</h2>

      <div className="mb-6 bg-white p-4 rounded shadow">
        <label className="flex items-center gap-2">
          <span className="font-medium">Filter by Role:</span>
          <select 
            value={filterRole} 
            onChange={(e) => setFilterRole(e.target.value)}
            className="p-2 border rounded"
          >
            <option value="ALL">All Users</option>
            <option value="ADMIN">Admin</option>
            <option value="COORDINATOR">Coordinator</option>
            <option value="USER">User</option>
          </select>
        </label>
      </div>

      <div className="grid grid-cols-3 gap-4 mb-6">
        <div className="bg-blue-50 p-4 rounded shadow">
          <p className="text-gray-600 text-sm">Total Users</p>
          <p className="text-3xl font-bold text-blue-600">{users?.length || 0}</p>
        </div>
        <div className="bg-purple-50 p-4 rounded shadow">
          <p className="text-gray-600 text-sm">Admins</p>
          <p className="text-3xl font-bold text-purple-600">
            {users?.filter(u => u.role === 'ADMIN').length || 0}
          </p>
        </div>
        <div className="bg-green-50 p-4 rounded shadow">
          <p className="text-gray-600 text-sm">Coordinators</p>
          <p className="text-3xl font-bold text-green-600">
            {users?.filter(u => u.role === 'COORDINATOR').length || 0}
          </p>
        </div>
      </div>

      <div className="bg-white rounded shadow overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-200 border-b">
            <tr>
              <th className="p-4 text-left">Username</th>
              <th className="p-4 text-left">Email</th>
              <th className="p-4 text-left">Full Name</th>
              <th className="p-4 text-left">Current Role</th>
              <th className="p-4 text-left">Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredUsers && filteredUsers.length > 0 ? (
              filteredUsers.map(u => (
                <tr key={u.id} className="border-t hover:bg-gray-50">
                  <td className="p-4 font-medium">{u.username}</td>
                  <td className="p-4 text-sm">{u.email}</td>
                  <td className="p-4 text-sm">{u.fullName || '-'}</td>
                  <td className="p-4">
                    <span className={`px-3 py-1 rounded-full text-sm font-medium ${
                      u.role === 'ADMIN' ? 'bg-red-100 text-red-700' :
                      u.role === 'COORDINATOR' ? 'bg-blue-100 text-blue-700' :
                      'bg-gray-100 text-gray-700'
                    }`}>
                      {u.role}
                    </span>
                  </td>
                  <td className="p-4">
                    <div className="flex gap-2 items-center flex-wrap">
                      <select
                        value={newRole[u.id] || u.role}
                        onChange={(e) => setNewRole(prev => ({ ...prev, [u.id]: e.target.value }))}
                        className="text-sm border rounded px-2 py-1"
                      >
                        <option value="USER">User</option>
                        <option value="COORDINATOR">Coordinator</option>
                        <option value="ADMIN">Admin</option>
                      </select>
                      {newRole[u.id] && newRole[u.id] !== u.role && (
                        <button
                          onClick={() => updateRoleMutation.mutate({
                            userId: u.id,
                            role: newRole[u.id]
                          })}
                          disabled={updateRoleMutation.isPending}
                          className="text-sm bg-blue-600 text-white px-3 py-1 rounded hover:bg-blue-700 disabled:bg-gray-400"
                        >
                          Update
                        </button>
                      )}
                      <button
                        onClick={() => {
                          if (confirm(`Are you sure you want to delete ${u.username}?`)) {
                            deleteUserMutation.mutate(u.id)
                          }
                        }}
                        disabled={deleteUserMutation.isPending}
                        className="text-sm bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700 disabled:bg-gray-400"
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="5" className="p-4 text-center text-gray-500">
                  No users found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
