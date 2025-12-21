import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { useMutation } from '@tanstack/react-query'
import api from '../api/http'

export default function Profile() {
  const { user } = useAuth()
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    fullName: ''
  })
  const [success, setSuccess] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (user) {
      setFormData({
        username: user.username || '',
        email: user.email || '',
        fullName: user.fullName || ''
      })
    }
  }, [user])

  const updateMutation = useMutation({
    mutationFn: async () => {
      const { data } = await api.put('/api/users/profile', formData)
      return data
    },
    onSuccess: () => {
      setSuccess(true)
      setError('')
      setTimeout(() => setSuccess(false), 3000)
    },
    onError: (err) => {
      setError(err.response?.data?.message || 'Failed to update profile')
    }
  })

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    updateMutation.mutate()
  }

  return (
    <div className="max-w-md mx-auto bg-white rounded shadow p-6">
      <h1 className="text-2xl font-bold mb-4">My Profile</h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-1">Username</label>
          <input
            type="text"
            name="username"
            value={formData.username}
            disabled
            className="w-full px-3 py-2 border rounded bg-gray-100 text-gray-600 cursor-not-allowed"
          />
          <p className="text-xs text-gray-500 mt-1">Cannot change username</p>
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Email</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            className="w-full px-3 py-2 border rounded"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Full Name</label>
          <input
            type="text"
            name="fullName"
            value={formData.fullName}
            onChange={handleChange}
            className="w-full px-3 py-2 border rounded"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Role</label>
          <input
            type="text"
            value={user?.role || 'USER'}
            disabled
            className="w-full px-3 py-2 border rounded bg-gray-100 text-gray-600 cursor-not-allowed"
          />
        </div>

        {error && <div className="bg-red-100 text-red-700 p-3 rounded">{error}</div>}
        {success && <div className="bg-green-100 text-green-700 p-3 rounded">Profile updated successfully!</div>}

        <button
          type="submit"
          disabled={updateMutation.isPending}
          className="w-full bg-blue-600 text-white py-2 rounded font-medium hover:bg-blue-700 disabled:bg-gray-400"
        >
          {updateMutation.isPending ? 'Saving...' : 'Save Changes'}
        </button>
      </form>
    </div>
  )
}
