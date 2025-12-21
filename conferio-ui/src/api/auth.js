import api from './http'

export async function login(payload) {
  try {
    console.log('API: Calling POST /api/auth/login with:', payload)
    const { data } = await api.post('/api/auth/login', payload)
    console.log('API: Login response:', data)
    return data
  } catch (error) {
    console.error('API: Login error:', error.response?.data || error.message)
    throw error
  }
}

export async function register(payload) {
  try {
    console.log('API: Calling POST /api/auth/register with:', payload)
    const { data } = await api.post('/api/auth/register', payload)
    console.log('API: Register response:', data)
    return data
  } catch (error) {
    console.error('API: Register error:', error.response?.data || error.message)
    throw error
  }
}
