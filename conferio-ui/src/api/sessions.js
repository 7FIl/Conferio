import api from './http'

export async function getSessions() {
  const { data } = await api.get('/api/sessions')
  return data
}

export async function registerToSession(sessionId) {
  const { data } = await api.post(`/api/registrations/session/${sessionId}`)
  return data
}
export async function getMyRegistrations() {
  const { data } = await api.get('/api/registrations/my')
  return data
}