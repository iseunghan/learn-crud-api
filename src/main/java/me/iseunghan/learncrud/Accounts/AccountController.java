package me.iseunghan.learncrud.Accounts;


import com.fasterxml.jackson.databind.ObjectMapper;
import me.iseunghan.learncrud.common.AccountResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping("/accounts")
@Controller
public class AccountController {

    private final AccountService accountService;

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    private final AccountRepository accountRepository;

    public AccountController(AccountService accountService, ModelMapper modelMapper, ObjectMapper objectMapper, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.accountRepository = accountRepository;
    }


    @GetMapping
    public ResponseEntity getAccountList(Pageable pageable, PagedResourcesAssembler<Account> assembler) {
        Page<Account> page = accountService.getAccountList(pageable);
        var entityModels = assembler.toModel(page, e -> new AccountResource(e));
        return ResponseEntity.ok(entityModels);
    }

    @PostMapping
    public ResponseEntity insertAccount(@RequestBody @Valid AccountDTO accountDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        // TODO duplicateAccount -> ?

        Account mapAccount = modelMapper.map(accountDTO, Account.class);
        Account insertDTO = accountService.join(mapAccount);

        AccountResource accountResource = new AccountResource(insertDTO);
        URI createUri = linkTo(AccountController.class).slash(insertDTO.getIdx()).toUri();
        return ResponseEntity.created(createUri).body(accountResource);
    }

    @PutMapping(value = "/{idx}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateAccount(@PathVariable Integer idx, @RequestBody @Valid AccountDTO accountDTO, Errors errors) {
        // TODO 1) 정상적으로 수정, 2) 없는 account를 수정 할 때, 3) 수정하려는 값이 비었을 때
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Account> optionalAccount = accountService.findById(idx);
        if (optionalAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Account oldAccount = optionalAccount.get();
        modelMapper.map(accountDTO, oldAccount);
        accountRepository.save(oldAccount);

        AccountResource accountResource = new AccountResource(oldAccount);
        return ResponseEntity.ok(accountResource);
    }

    @DeleteMapping(value = "/{idx}")
    public ResponseEntity deleteAccount(@PathVariable Integer idx) {
        // TODO 1) 정상적으로 삭제, 2) 없는 값을 삭제 할 때
        return ResponseEntity.ok().build();
    }
}
