package com.youthmission.school;

import com.youthmission.domain.School;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface SchoolRepository extends JpaRepository<School, Long> {

    boolean existsByPath(String path);

    @EntityGraph(value = "School.withAll", type = EntityGraph.EntityGraphType.LOAD)
    School findByPath(String path);

    @EntityGraph(value = "School.withManagers", type = EntityGraph.EntityGraphType.FETCH)
    School findSchoolWithManagersByPath(String path);

    @EntityGraph(value = "School.withMembers", type = EntityGraph.EntityGraphType.FETCH)
    School findSchoolWithMembersByPath(String path);

    School findSchoolOnlyByPath(String path);

}
