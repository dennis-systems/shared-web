package ru.dennis.systems.repository;


import ru.dennis.systems.entity.db.DbInjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpdateRepository  extends PaginationRepository<DbInjection> {
     @Query ("select injection from db_injection injection where name=?1 and (profile = '' or profile=?2)")
     List<DbInjection> getFirstByNameAndProfile(String name, String profile, Pageable pageable);
}
