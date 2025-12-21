import { useParams, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import api from '../api/http'

async function getUserProfile(username) {
  const { data } = await api.get(`/api/users/${username}`)
  return data
}

export default function OtherProfile() {
  const { username } = useParams()
  const navigate = useNavigate()
  const { data: user, isLoading, error } = useQuery({
    queryKey: ['user-profile', username],
    queryFn: () => getUserProfile(username)
  })

  if (isLoading) return <div className="p-4">Loading profile...</div>
  if (error) return (
    <div className="max-w-md mx-auto">
      <div className="bg-red-100 text-red-700 p-4 rounded mb-4">
        User not found
      </div>
      <button
        onClick={() => navigate(-1)}
        className="bg-gray-600 text-white px-4 py-2 rounded"
      >
        Go Back
      </button>
    </div>
  )

  return (
    <div className="max-w-md mx-auto bg-white rounded shadow p-6">
      <button
        onClick={() => navigate(-1)}
        className="mb-4 text-blue-600 hover:underline"
      >
        ← Back
      </button>

      <h1 className="text-2xl font-bold mb-4">{user?.fullName}</h1>

      <div className="space-y-3">
        <div>
          <label className="block text-sm font-medium text-gray-600">Username</label>
          <p className="text-lg">{user?.username}</p>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-600">Email</label>
          <p className="text-lg">{user?.email}</p>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-600">Role</label>
          <p className="text-lg capitalize">{user?.role?.toLowerCase()}</p>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-600">Member Since</label>
          <p className="text-lg">
            {new Date(user?.createdAt).toLocaleDateString()}
          </p>
        </div>
      </div>
    </div>
  )
}
