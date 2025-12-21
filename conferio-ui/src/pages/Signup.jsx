import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { useNavigate, Link } from 'react-router-dom'
import * as authApi from '../api/auth'

export default function Signup() {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [fullName, setFullName] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(null)
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setSuccess(null)
    setLoading(true)

    if (!username || !email || !fullName || !password || !confirmPassword) {
      setError('All fields required')
      setLoading(false)
      return
    }
    if (password !== confirmPassword) {
      setError('Passwords do not match')
      setLoading(false)
      return
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters')
      setLoading(false)
      return
    }

    try {
      await authApi.register({ username, email, fullName, password })
      setSuccess('Account created! Logging in...')
      await new Promise(r => setTimeout(r, 1000))
      await login(username, password)
      navigate('/sessions')
    } catch (err) {
      const errorMsg = err.response?.data?.message || err.message || 'Registration failed'
      setError(errorMsg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-md mx-auto mt-10 bg-white p-6 rounded shadow">
      <h1 className="text-xl font-semibold mb-4">Create Account</h1>
      {error && <div className="bg-red-100 text-red-700 p-2 rounded mb-3 text-sm">{error}</div>}
      {success && <div className="bg-green-100 text-green-700 p-2 rounded mb-3 text-sm">{success}</div>}
      <form className="space-y-3" onSubmit={handleSubmit}>
        <div>
          <label className="block text-sm mb-1">Username</label>
          <input className="w-full border rounded px-3 py-2" value={username} onChange={(e) => setUsername(e.target.value)} disabled={loading} />
        </div>
        <div>
          <label className="block text-sm mb-1">Email</label>
          <input type="email" className="w-full border rounded px-3 py-2" value={email} onChange={(e) => setEmail(e.target.value)} disabled={loading} />
        </div>
        <div>
          <label className="block text-sm mb-1">Full Name</label>
          <input className="w-full border rounded px-3 py-2" value={fullName} onChange={(e) => setFullName(e.target.value)} disabled={loading} />
        </div>
        <div>
          <label className="block text-sm mb-1">Password</label>
          <input type="password" className="w-full border rounded px-3 py-2" value={password} onChange={(e) => setPassword(e.target.value)} disabled={loading} />
        </div>
        <div>
          <label className="block text-sm mb-1">Confirm Password</label>
          <input type="password" className="w-full border rounded px-3 py-2" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} disabled={loading} />
        </div>
        <button className="w-full bg-blue-600 text-white rounded py-2 hover:bg-blue-700 disabled:opacity-50" disabled={loading}>{loading ? 'Signing Up...' : 'Sign Up'}</button>
      </form>
      <p className="text-sm text-gray-600 mt-3">
        Already have an account? <Link to="/login" className="text-blue-600 hover:underline">Login</Link>
      </p>
    </div>
  )
}
