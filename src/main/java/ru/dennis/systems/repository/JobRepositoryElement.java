package ru.dennis.systems.repository;

import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface JobRepositoryElement {
    @Transactional
    Long deleteByJobId(Long job);

    long countByJobId(Long jobId);
}
