import { useState, useEffect } from 'react'
import { useMutation } from '@tanstack/react-query'
import api from '../api/http'

export default function ProposalForm({ proposalId, onSuccess, onCancel }) {
  const [formData, setFormData] = useState({
    title: '',
    description: ''
  })
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  // Load proposal data if editing
  useEffect(() => {
    if (proposalId) {
      setIsLoading(true)
      api.get(`/api/proposals/${proposalId}`)
        .then(({ data }) => {
          setFormData({
            title: data.title,
            description: data.description
          })
        })
        .catch(err => {
          setError('Failed to load proposal')
        })
        .finally(() => setIsLoading(false))
    }
  }, [proposalId])

  const submitMutation = useMutation({
    mutationFn: async () => {
      if (proposalId) {
        // Update proposal
        const { data } = await api.put(`/api/proposals/${proposalId}`, formData)
        return data
      } else {
        // Create new proposal
        const { data } = await api.post('/api/proposals', formData)
        return data
      }
    },
    onSuccess: () => {
      onSuccess?.()
    },
    onError: (err) => {
      setError(err.response?.data?.message || 'Failed to save proposal')
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
    if (!formData.title.trim()) {
      setError('Title is required')
      return
    }
    if (!formData.description.trim()) {
      setError('Description is required')
      return
    }
    submitMutation.mutate()
  }

  if (isLoading) return <div className="p-4">Loading...</div>

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium mb-1">Title</label>
        <input
          type="text"
          name="title"
          value={formData.title}
          onChange={handleChange}
          placeholder="Proposal title"
          className="w-full px-3 py-2 border rounded"
          required
        />
      </div>

      <div>
        <label className="block text-sm font-medium mb-1">Description</label>
        <textarea
          name="description"
          value={formData.description}
          onChange={handleChange}
          placeholder="Detailed proposal description"
          rows={6}
          className="w-full px-3 py-2 border rounded"
          required
        />
      </div>

      {error && <div className="bg-red-100 text-red-700 p-3 rounded">{error}</div>}

      <div className="flex gap-2">
        <button
          type="submit"
          disabled={submitMutation.isPending}
          className="flex-1 bg-blue-600 text-white py-2 rounded font-medium hover:bg-blue-700 disabled:bg-gray-400"
        >
          {submitMutation.isPending ? 'Saving...' : proposalId ? 'Update' : 'Create'}
        </button>
        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            className="px-4 py-2 bg-gray-300 text-gray-700 rounded font-medium hover:bg-gray-400"
          >
            Cancel
          </button>
        )}
      </div>
    </form>
  )
}
