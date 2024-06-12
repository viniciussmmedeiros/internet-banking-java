package com.internetbanking.accountapi.service.branch;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "branch-service", url = "http://branch-api:8080/branches/")
public interface BranchService {

    @GetMapping("/list-all")
    List<BranchEntity> listAll();

    @GetMapping("/filter-by-institution-name/{name}/list")
    List<BranchEntity> listByInstitutionName(@PathVariable String name);
}
