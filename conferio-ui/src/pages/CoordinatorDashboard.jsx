import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import api from '../api/http'

async function getSessions() {
  const { data } = await api.get('/api/sessions')
  return data
}

async function deleteSession(sessionId) {
  await api.delete(`/api/sessions/${sessionId}`)
}

async function getFeedback(sessionId) {
  const { data } = await api.get(`/api/feedback/session/${sessionId}`)
  return data
}

async function deleteFeedback(feedbackId) {
  await api.delete(`/api/feedback/${feedbackId}`)
}

export default function CoordinatorDashboard() {
  const qc = useQueryClient()
  const [selectedSession, setSelectedSession] = useState(null)
  const [showFeedback, setShowFeedback] = useState(false)

  const { data: sessions, isLoading } = useQuery({
    queryKey: ['sessions'],
    queryFn: getSessions
  })

  const { data: feedback } = useQuery({
    queryKey: ['feedback', selectedSession],
    queryFn: () => getFeedback(selectedSession),
    enabled: !!selectedSession && showFeedback
  })

  const deleteSessionMutation = useMutation({
    mutationFn: deleteSession,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['sessions'] })
      setSelectedSession(null)
    }
  })

  const deleteFeedbackMutation = useMutation({
    mutationFn: deleteFeedback,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['feedback', selectedSession] })
    }
  })

  if (isLoading) return <div className="p-4">Loading...</div>

  return (
    <div className="grid grid-cols-3 gap-4">
      {/* Sessions List */}
      <div className="col-span-2">
        <h2 className="text-xl font-bold mb-4">Manage Sessions</h2>
        <div className="space-y-2">
          {sessions?.map(s => (
            <div
              key={s.id}
              className={`bg-white border rounded p-3 cursor-pointer hover:shadow ${
                selectedSession === s.id ? 'border-blue-500 bg-blue-50' : ''
              }`}
              onClick={() => {
                setSelectedSession(s.id)
                setShowFeedback(false)
              }}
            >
              <h3 className="font-semibold">{s.title}</h3>
              <p className="text-sm text-gray-600">
                {new Date(s.sessionTime).toLocaleString()} • {s.room}
              </p>
              <p className="text-sm text-gray-600">
                Participants: {s.currentParticipants}/{s.maxParticipants}
              </p>
            </div>
          ))}
        </div>
      </div>

      {/* Session Details */}
      {selectedSession && (
        <div className="bg-white rounded shadow p-4">
          <h3 className="font-bold mb-2">Session Actions</h3>
          <div className="space-y-2">
            <button
              onClick={() => {
                setShowFeedback(true)
              }}
              className="w-full bg-blue-600 text-white px-3 py-2 rounded text-sm hover:bg-blue-700"
            >
              View Feedback
            </button>
            <button
              onClick={() => deleteSessionMutation.mutate(selectedSession)}
              disabled={deleteSessionMutation.isPending}
              className="w-full bg-red-600 text-white px-3 py-2 rounded text-sm hover:bg-red-700 disabled:bg-gray-400"
            >
              {deleteSessionMutation.isPending ? 'Deleting...' : 'Delete Session'}
            </button>
          </div>

          {/* Feedback List */}
          {showFeedback && (
            <div className="mt-4 pt-4 border-t">
              <h4 className="font-semibold mb-2">Feedback ({feedback?.length || 0})</h4>
              <div className="space-y-2 max-h-64 overflow-y-auto">
                {feedback?.map(f => (
                  <div key={f.id} className="bg-gray-50 p-2 rounded text-sm">
                    <div className="flex justify-between items-start mb-1">
                      <p className="font-medium">{f.username}</p>
                      <div className="flex gap-1">
                        <span className="text-yellow-500">★</span>
                        <span>{f.rating}/5</span>
                      </div>
                    </div>
                    <p className="text-gray-700">{f.comment}</p>
                    <button
                      onClick={() => deleteFeedbackMutation.mutate(f.id)}
                      disabled={deleteFeedbackMutation.isPending}
                      className="text-red-600 text-xs hover:underline mt-1"
                    >
                      Delete
                    </button>
                  </div>
                ))}
                {feedback?.length === 0 && (
                  <p className="text-gray-500 text-sm">No feedback yet</p>
                )}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
