package com.internetbanking.accountapi.controller.pix;

import com.internetbanking.accountapi.controller.pix.request.CreateKeyRequest;
import com.internetbanking.accountapi.model.PixKey;
import com.internetbanking.accountapi.service.PixService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Pix Controller", description = "Manages pix operations including key creation, deletion and listing.")
@RestController
@RequestMapping("/pix")
public class PixController {

    @Autowired
    private PixService pixService;

    @Operation(summary = "Creates a new Pix key.", method = "POST")
    @PostMapping("/create-key")
    public void createKey(@RequestBody CreateKeyRequest request) {
        pixService.createKey(request);
    }

    @Operation(summary = "Deletes the Pix key identified by the given key ID.", method = "PATCH")
    @PatchMapping("/{accountId}/delete-key/{keyId}")
    public void deleteKey(@PathVariable UUID accountId, @PathVariable UUID keyId) {
        pixService.deleteKey(accountId, keyId);
    }

    @Operation(summary = "Lists all Pix keys for the specified account.", method = "GET")
    @GetMapping("{accountId}/list-keys")
    public List<PixKey> listKeys(@PathVariable UUID accountId) {
        return pixService.listKeys(accountId);
    }
}
