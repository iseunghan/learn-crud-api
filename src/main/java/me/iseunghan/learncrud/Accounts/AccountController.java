package me.iseunghan.learncrud.Accounts;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping("/accounts")
@Controller
public class AccountController {

    private final AccountService accountService;

    private final AccountRepository accountRepository;


    public AccountController(AccountService accountService, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }


//    @GetMapping
//    public ResponseEntity getAccountList(Pageable pageable) {
//        Page<Account> accountList = accountService.getAccountList(pageable);
//        return ResponseEntity.ok(accountList);
//    }

    @GetMapping
    public ResponseEntity getAccount() {
        List<Account> accountList = accountRepository.findAll();
        return ResponseEntity.ok(accountList);
    }

    @PostMapping
    public ResponseEntity insertAccount(@RequestBody Account insertDto) {
        Account newAccount = accountService.join(insertDto);

        URI createUri = linkTo(AccountController.class).slash(newAccount.getIdx()).toUri();
        return ResponseEntity.created(createUri).body(newAccount);
    }
}
