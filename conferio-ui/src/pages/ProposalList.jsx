import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import api from '../api/http'

async function getAllProposals() {
  const { data } = await api.get('/api/proposals')
  return data
}

async function reviewProposal(proposalId, status, rejectionReason) {
  // Ensure status is uppercase and validate it's a valid option
  const validStatus = ['ACCEPTED', 'REJECTED'].includes(status?.toUpperCase()) 
    ? status.toUpperCase() 
    : 'REJECTED'
  
  const payload = {
    status: validStatus,
    rejectionReason: rejectionReason || ''
  }
  
  const { data } = await api.post(`/api/proposals/${proposalId}/review`, payload)
  return data
}

export default function ProposalList() {
  const qc = useQueryClient()
  const [reviewingId, setReviewingId] = useState(null)
  const [rejectionReason, setRejectionReason] = useState('')
  const { data: proposals, isLoading, error } = useQuery({
    queryKey: ['proposals'],
    queryFn: getAllProposals
  })

  const reviewMutation = useMutation({
    mutationFn: ({ proposalId, status }) =>
      reviewProposal(proposalId, status, rejectionReason),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['proposals'] })
      setReviewingId(null)
      setRejectionReason('')
    }
  })

  if (isLoading) return <div className="p-4">Loading proposals...</div>
  if (error) return <div className="p-4 text-red-600">Failed to load: {error.message}</div>

  const pendingProposals = proposals?.filter(p => p.status === 'PENDING') || []
  const otherProposals = proposals?.filter(p => p.status !== 'PENDING') || []

  return (
    <div className="space-y-6">
      {pendingProposals.length > 0 && (
        <div>
          <h2 className="text-xl font-bold mb-3">Pending Reviews ({pendingProposals.length})</h2>
          <div className="space-y-3">
            {pendingProposals.map(p => (
              <div key={p.id} className="bg-yellow-50 border border-yellow-200 rounded p-4">
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <h3 className="text-lg font-semibold">{p.title}</h3>
                    <p className="text-sm text-gray-600">By: {p.submitterName}</p>
                  </div>
                </div>
                <p className="text-gray-700 mb-3">{p.description}</p>
                {reviewingId === p.id ? (
                  <div className="bg-white p-3 rounded border space-y-2">
                    <p className="text-sm font-medium">Rejection reason (if rejecting):</p>
                    <textarea
                      value={rejectionReason}
                      onChange={(e) => setRejectionReason(e.target.value)}
                      placeholder="Optional: Explain why you're rejecting this proposal"
                      rows={3}
                      className="w-full px-2 py-1 border rounded text-sm"
                    />
                    <div className="flex gap-2">
                      <button
                        onClick={() => reviewMutation.mutate({
                          proposalId: p.id,
                          status: 'ACCEPTED'
                        })}
                        disabled={reviewMutation.isPending}
                        className="flex-1 bg-green-600 text-white px-3 py-1 rounded hover:bg-green-700 disabled:bg-gray-400"
                      >
                        Accept
                      </button>
                      <button
                        onClick={() => reviewMutation.mutate({
                          proposalId: p.id,
                          status: 'REJECTED'
                        })}
                        disabled={reviewMutation.isPending}
                        className="flex-1 bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700 disabled:bg-gray-400"
                      >
                        Reject
                      </button>
                      <button
                        onClick={() => {
                          setReviewingId(null)
                          setRejectionReason('')
                        }}
                        className="px-3 py-1 bg-gray-300 rounded hover:bg-gray-400"
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                ) : (
                  <button
                    onClick={() => setReviewingId(p.id)}
                    className="bg-blue-600 text-white px-3 py-1 rounded text-sm hover:bg-blue-700"
                  >
                    Review
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {otherProposals.length > 0 && (
        <div>
          <h2 className="text-xl font-bold mb-3">Reviewed Proposals</h2>
          <div className="space-y-2">
            {otherProposals.map(p => (
              <div key={p.id} className="bg-white rounded p-4 border">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="font-semibold">{p.title}</h3>
                    <p className="text-sm text-gray-600">By: {p.submitterName}</p>
                  </div>
                  <span className={`px-2 py-1 rounded text-xs font-medium ${
                    p.status === 'ACCEPTED' ? 'bg-green-100 text-green-700' :
                    'bg-red-100 text-red-700'
                  }`}>
                    {p.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {proposals?.length === 0 && (
        <p className="text-gray-500">No proposals available</p>
      )}
    </div>
  )
}
