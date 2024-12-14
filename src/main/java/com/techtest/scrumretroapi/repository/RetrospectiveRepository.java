package com.techtest.scrumretroapi.repository;

import com.techtest.scrumretroapi.entity.Retrospective;
import com.techtest.scrumretroapi.entity.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RetrospectiveRepository extends JpaRepository<Retrospective, Long> {
    @Query(value = "SELECT DISTINCT r FROM Retrospective r LEFT JOIN FETCH r.feedback LEFT JOIN FETCH r.task ORDER BY r.name")
    Page<Retrospective> findAllRetrospectives(Pageable pageable);

    @Query("SELECT r FROM Retrospective r LEFT JOIN FETCH r.feedback WHERE r.date = :date")
    List<Retrospective> findRetrospectivesByDate(@Param("date") LocalDate date);

    @Query("SELECT r FROM Retrospective r WHERE r.name = :name")
    Retrospective findByName(@Param("name") String name);

    // @Query("SELECT r.task FROM Retrospective r WHERE r.name = :name")
    // List<Task> findTasksByRetrospectiveName(@Param("name") String name);
}
