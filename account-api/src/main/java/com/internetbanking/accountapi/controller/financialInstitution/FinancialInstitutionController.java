package com.internetbanking.accountapi.controller.financialInstitution;

import com.internetbanking.accountapi.model.FinancialInstitution;
import com.internetbanking.accountapi.service.FinancialInstitutionService;
import com.internetbanking.accountapi.service.branch.Branch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Financial Institution Controller", description = "Provides the supported financial institutions for Internet Banking.")
@RestController
@RequestMapping("/financial-institution")
public class FinancialInstitutionController {

    @Autowired
    private FinancialInstitutionService financialInstitutionService;

    @Operation(summary = "Lists all supported financial institutions.", method = "GET")
    @GetMapping("/list")
    public List<FinancialInstitution> list() {
        return financialInstitutionService.list();
    }

    @Operation(summary = "Lists all branches for the specified financial institution.", method = "GET")
    @GetMapping("{financialInstitutionId}/branch/list")
    public List<Branch> listBranchesByInstitution(@PathVariable Long financialInstitutionId) {
        return financialInstitutionService.listInstitutionBranches(financialInstitutionId);
    }
}
