package com.internetbanking.branches.repository;

import com.internetbanking.branches.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    @Query(value = "SELECT b.* FROM Branch b WHERE b.data->>'NomeIf' = :nomeIf", nativeQuery = true)
    List<Branch> findAllByNomeIf(@Param("nomeIf") String name);
}
