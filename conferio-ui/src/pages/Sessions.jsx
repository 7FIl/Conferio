import { useEffect, useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getSessions, registerToSession, getMyRegistrations } from '../api/sessions'
import { useAuth } from '../context/AuthContext'

export default function Sessions() {
  const { user } = useAuth()
  const qc = useQueryClient()
  const [registeredSessions, setRegisteredSessions] = useState(new Set())
  const [sessionErrors, setSessionErrors] = useState({})

  const { data: sessions, isLoading, error } = useQuery({
    queryKey: ['sessions'],
    queryFn: getSessions
  })

  // Fetch user's registered sessions
  const { data: myRegistrations } = useQuery({
    queryKey: ['my-registrations'],
    queryFn: getMyRegistrations,
    enabled: !!user
  })

  // Update registered sessions set when registrations load
  useEffect(() => {
    if (myRegistrations) {
      setRegisteredSessions(new Set(myRegistrations.map(r => r.sessionId)))
    }
  }, [myRegistrations])

  const registerMutation = useMutation({
    mutationFn: ({sessionId, ...rest}) => registerToSession(sessionId),
    onSuccess: (data) => {
      setSessionErrors({})
      qc.invalidateQueries({ queryKey: ['sessions'] })
      qc.invalidateQueries({ queryKey: ['my-registrations'] })
      setRegisteredSessions(prev => new Set([...prev, data.sessionId]))
    },
    onError: (error, variables) => {
      const sessionId = variables.sessionId
      const errorMsg = error.response?.data?.message || error.message || 'Failed to register'
      setSessionErrors(prev => ({ ...prev, [sessionId]: errorMsg }))
    }
  })

  if (isLoading) return <div className="p-4">Loading sessions...</div>
  if (error) return <div className="p-4 text-red-600">Failed to load: {error.message}</div>

  return (
    <div className="grid gap-4 md:grid-cols-2">
      {sessions?.map(s => {
        const isRegistered = registeredSessions.has(s.id)
        const isFull = s.currentParticipants >= s.maxParticipants
        const canRegister = !isRegistered && !isFull && user

        return (
          <div key={s.id} className="bg-white rounded shadow p-4">
            <h2 className="font-semibold text-lg">{s.title}</h2>
            <p className="text-sm text-gray-600">Room: {s.room}</p>
            <p className="text-sm text-gray-600">
              Participants: {s.currentParticipants}/{s.maxParticipants}
            </p>
            <p className="text-sm text-gray-600">
              Time: {new Date(s.sessionTime).toLocaleString()}
            </p>

            <div className="mt-3 flex gap-2 flex-wrap">
              {isRegistered ? (
                <span className="bg-green-100 text-green-700 px-3 py-1 rounded text-sm font-medium">
                  ✓ Registered
                </span>
              ) : isFull ? (
                <span className="bg-gray-200 text-gray-600 px-3 py-1 rounded text-sm font-medium">
                  Session Full
                </span>
              ) : (
                <button
                  className={`px-3 py-1 rounded text-white font-medium transition ${
                    canRegister
                      ? 'bg-blue-600 hover:bg-blue-700 cursor-pointer'
                      : 'bg-gray-400 cursor-not-allowed'
                  }`}
                  onClick={() => canRegister && registerMutation.mutate({ sessionId: s.id })}
                  disabled={!canRegister || registerMutation.isPending}
                >
                  {registerMutation.isPending ? 'Registering...' : 'Register'}
                </button>
              )}

              {sessionErrors[s.id] && (
                <span className="text-red-600 text-sm w-full">
                  Error: {sessionErrors[s.id]}
                </span>
              )}
            </div>
          </div>
        )
      })}
    </div>
  )
}

