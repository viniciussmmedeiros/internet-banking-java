package com.internetbanking.branches.service;

import com.internetbanking.branches.model.Branch;
import com.internetbanking.branches.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    public List<Branch> listAll() {
        return branchRepository.findAll();
    }

    public List<Branch> listByInstitutionName(String name) {
        return branchRepository.findAllByNomeIf(name);
    }
}
