package com.internetbanking.accountapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internetbanking.accountapi.model.FinancialInstitution;
import com.internetbanking.accountapi.repository.FinancialInstitutionRepository;
import com.internetbanking.accountapi.service.branch.Branch;
import com.internetbanking.accountapi.service.branch.BranchEntity;
import com.internetbanking.accountapi.service.branch.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class FinancialInstitutionService {

    @Autowired
    private FinancialInstitutionRepository financialInstitutionRepository;

    @Autowired
    private BranchService branchService;

    public List<Branch> listInstitutions() {
        List<BranchEntity> branchEntities = branchService.listAll();
        List<Branch> branches = new ArrayList<>();

        for (BranchEntity entity : branchEntities) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Branch branch = mapper.readValue(entity.getData(), Branch.class);

                branches.add(branch);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return branches;
    }

    public List<Branch> listInstitutionBranches(Long financialInstitutionId) {
        FinancialInstitution institution = financialInstitutionRepository.findById(financialInstitutionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Financial Institution not found."));

        String institutionName = institution.getName();

        List<BranchEntity> branchEntities = branchService.listByInstitutionName(institutionName);
        List<Branch> branches = new ArrayList<>();

        for (BranchEntity entity : branchEntities) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Branch branch = mapper.readValue(entity.getData(), Branch.class);
                branches.add(branch);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return branches;
    }

    public List<FinancialInstitution> list() {
        return financialInstitutionRepository.findAll();
    }
}
