package ru.dennis.systems.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Component;


@NoRepositoryBean
@Component
public interface PaginationRepository<T> extends CrudRepository<T, Long>, JpaSpecificationExecutor<T>  {
    Page<T> findAll(Pageable pageRequest);

    Page<T> findAll(Specification<T> specification, Pageable pageable);
}
