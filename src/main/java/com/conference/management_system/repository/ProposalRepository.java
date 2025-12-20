package com.conference.management_system.repository;

import com.conference.management_system.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findByUserId(Long userId);
    List<Proposal> findByStatus(Proposal.ProposalStatus status);
}
