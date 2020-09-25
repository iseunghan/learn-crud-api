package me.iseunghan.learncrud.Accounts;


import com.fasterxml.jackson.databind.ObjectMapper;
import me.iseunghan.learncrud.common.AccountResource;
import me.iseunghan.learncrud.common.DuplicatedException;
import me.iseunghan.learncrud.common.NotFoundException;
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

    @GetMapping(value = "/{idx}")
    public ResponseEntity getAccount(@PathVariable Integer idx) {
        Optional<Account> optionalAccount = accountService.findById(idx);

        // 존재하지 않는 Account -> NotFound
        if (optionalAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 존재하는 Account일 경우
        Account account = optionalAccount.get();
        AccountResource accountResource = new AccountResource(account);
        return ResponseEntity.ok(accountResource);
    }

    @PostMapping
    public ResponseEntity insertAccount(@RequestBody @Valid AccountDTO accountDTO, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        // TODO duplicateAccount -> exception이 터지는지 확인
//        Account accountByName = this.accountService.findAccountByName(accountDTO.getName());
//        if (accountByName != null) {
//            return ResponseEntity.badRequest().build();
//        }

        Account mapAccount = modelMapper.map(accountDTO, Account.class);
        Account insertDTO = accountService.join(mapAccount);

        AccountResource accountResource = new AccountResource(insertDTO);
        URI createUri = linkTo(AccountController.class).slash(insertDTO.getIdx()).toUri();
        return ResponseEntity.created(createUri).body(accountResource);
    }

    @PutMapping(value = "/{idx}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateAccount(@PathVariable Integer idx, @RequestBody Account account) {
        // TODO 1) 정상적으로 수정, 2) 없는 account를 수정 할 때, 3) 수정하려는 값이 비었을 때

        //TODO 비었을 경우 -> ExceptionHandler로 처리
        Account updateAccount = accountService.updateAccount(idx, account);

        AccountResource accountResource = new AccountResource(updateAccount);
        return ResponseEntity.ok(accountResource);
    }

    @DeleteMapping(value = "/{idx}")
    public ResponseEntity deleteAccount(@PathVariable Integer idx) {
        // TODO 1) 정상적으로 삭제, 2) 없는 값을 삭제 할 때
        accountService.deleteAccountbyId(idx);

        // account 가 존재할 경우.
        return ResponseEntity.ok().build();
    }

    /**
     * @ExceptionHandler
     */
    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity NotFoundExcepResponseEntity(NotFoundException e) {
        return ResponseEntity.badRequest().body(e.getInformation());
    }

    @ExceptionHandler(value = DuplicatedException.class)
    public ResponseEntity DuplicationExcepResponseEntity(DuplicatedException e) {
        return ResponseEntity.badRequest().body(e.getInformation());
    }

}
