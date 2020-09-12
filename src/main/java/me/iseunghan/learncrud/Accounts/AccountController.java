package me.iseunghan.learncrud.Accounts;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping("/accounts")
@Controller
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping
    public ResponseEntity insertAccount(@RequestBody Account insertDto) {
        Account newAccount = accountService.join(insertDto);

        URI createUri = linkTo(AccountController.class).slash(newAccount.getIdx()).toUri();
        return ResponseEntity.created(createUri).body(newAccount);
    }
}
