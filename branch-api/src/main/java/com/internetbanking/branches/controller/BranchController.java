package com.internetbanking.branches.controller;

import com.internetbanking.branches.model.Branch;
import com.internetbanking.branches.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @GetMapping("/list-all")
    public List<Branch> listAll() {
        return branchService.listAll();
    }

    @GetMapping("/filter-by-institution-name/{name}/list")
    public List<Branch> listByInstitutionName(@PathVariable String name) {
        return branchService.listByInstitutionName(name);
    }
}
