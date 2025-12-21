import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'
import api from '../api/http'

async function submitFeedback(sessionId, rating, comment) {
  const { data } = await api.post('/api/feedback', {
    sessionId,
    rating: parseInt(rating),
    comment
  })
  return data
}

export default function FeedbackForm() {
  const { sessionId } = useParams()
  const navigate = useNavigate()
  const [formData, setFormData] = useState({
    rating: 5,
    comment: ''
  })
  const [error, setError] = useState('')

  const submitMutation = useMutation({
    mutationFn: () => submitFeedback(sessionId, formData.rating, formData.comment),
    onSuccess: () => {
      navigate('/sessions')
    },
    onError: (err) => {
      setError(err.response?.data?.message || 'Failed to submit feedback')
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
    if (!formData.comment.trim()) {
      setError('Please provide feedback')
      return
    }
    submitMutation.mutate()
  }

  return (
    <div className="max-w-md mx-auto bg-white rounded shadow p-6">
      <button
        onClick={() => navigate(-1)}
        className="mb-4 text-blue-600 hover:underline"
      >
        ← Back
      </button>

      <h1 className="text-2xl font-bold mb-4">Leave Feedback</h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium mb-2">Rating</label>
          <div className="flex gap-2">
            {[1, 2, 3, 4, 5].map(num => (
              <button
                key={num}
                type="button"
                onClick={() => setFormData(prev => ({ ...prev, rating: num }))}
                className={`px-3 py-2 rounded ${
                  formData.rating === num
                    ? 'bg-yellow-500 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                }`}
              >
                {num}
              </button>
            ))}
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium mb-1">Comments</label>
          <textarea
            name="comment"
            value={formData.comment}
            onChange={handleChange}
            placeholder="Share your thoughts about the session..."
            rows={6}
            className="w-full px-3 py-2 border rounded"
            required
          />
        </div>

        {error && <div className="bg-red-100 text-red-700 p-3 rounded">{error}</div>}

        <button
          type="submit"
          disabled={submitMutation.isPending}
          className="w-full bg-blue-600 text-white py-2 rounded font-medium hover:bg-blue-700 disabled:bg-gray-400"
        >
          {submitMutation.isPending ? 'Submitting...' : 'Submit Feedback'}
        </button>
      </form>
    </div>
  )
}
