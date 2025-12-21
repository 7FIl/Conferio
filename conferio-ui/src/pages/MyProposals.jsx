import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import api from '../api/http'
import ProposalForm from '../components/ProposalForm'

async function getMyProposals() {
  const { data } = await api.get('/api/proposals/my')
  return data
}

async function deleteProposal(proposalId) {
  await api.delete(`/api/proposals/${proposalId}`)
}

export default function MyProposals() {
  const qc = useQueryClient()
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const { data: proposals, isLoading, error } = useQuery({
    queryKey: ['my-proposals'],
    queryFn: getMyProposals
  })

  const deleteMutation = useMutation({
    mutationFn: deleteProposal,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['my-proposals'] })
    }
  })

  const handleSuccess = () => {
    qc.invalidateQueries({ queryKey: ['my-proposals'] })
    setShowForm(false)
    setEditingId(null)
  }

  if (isLoading) return <div className="p-4">Loading proposals...</div>
  if (error) return <div className="p-4 text-red-600">Failed to load: {error.message}</div>

  return (
    <div className="space-y-4">
      {!showForm && (
        <button
          onClick={() => setShowForm(true)}
          className="bg-green-600 text-white px-4 py-2 rounded font-medium hover:bg-green-700"
        >
          + New Proposal
        </button>
      )}

      {showForm && (
        <div className="bg-white rounded shadow p-6">
          <h2 className="text-xl font-bold mb-4">
            {editingId ? 'Edit Proposal' : 'Create New Proposal'}
          </h2>
          <ProposalForm
            proposalId={editingId}
            onSuccess={handleSuccess}
            onCancel={() => {
              setShowForm(false)
              setEditingId(null)
            }}
          />
        </div>
      )}

      <div className="space-y-3">
        {proposals?.length === 0 ? (
          <p className="text-gray-500">No proposals yet. Create your first one!</p>
        ) : (
          proposals?.map(p => (
            <div key={p.id} className="bg-white rounded shadow p-4">
              <div className="flex justify-between items-start mb-2">
                <h3 className="text-lg font-semibold">{p.title}</h3>
                <span className={`px-2 py-1 rounded text-xs font-medium ${
                  p.status === 'ACCEPTED' ? 'bg-green-100 text-green-700' :
                  p.status === 'REJECTED' ? 'bg-red-100 text-red-700' :
                  'bg-yellow-100 text-yellow-700'
                }`}>
                  {p.status}
                </span>
              </div>
              <p className="text-gray-600 text-sm mb-3">{p.description}</p>
              {p.status === 'REJECTED' && p.rejectionReason && (
                <div className="bg-red-50 border border-red-200 p-2 rounded text-sm text-red-700 mb-3">
                  <strong>Reason:</strong> {p.rejectionReason}
                </div>
              )}
              <div className="flex gap-2">
                {p.status === 'PENDING' && (
                  <>
                    <button
                      onClick={() => {
                        setEditingId(p.id)
                        setShowForm(true)
                      }}
                      className="bg-blue-600 text-white px-3 py-1 rounded text-sm hover:bg-blue-700"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => deleteMutation.mutate(p.id)}
                      disabled={deleteMutation.isPending}
                      className="bg-red-600 text-white px-3 py-1 rounded text-sm hover:bg-red-700 disabled:bg-gray-400"
                    >
                      Delete
                    </button>
                  </>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}
