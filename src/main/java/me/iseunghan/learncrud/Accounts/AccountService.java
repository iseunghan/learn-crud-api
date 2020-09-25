package me.iseunghan.learncrud.Accounts;

import me.iseunghan.learncrud.common.DuplicatedException;
import me.iseunghan.learncrud.common.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    public Page<Account> getAccountList(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }


    public Account findAccountByName(String name) {
        return accountRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("error"));
    }


    public Optional<Account> findById(Integer id) {
        return accountRepository.findById(id);
    }


    public Account join(Account insertDTO) {
        //TODO validateDuplicateAccount
        validateDuplicateAccount(insertDTO);
        return accountRepository.save(insertDTO);
    }

    public Account updateAccount(Integer idx, Account newAccount) {
        Optional<Account> byId = accountRepository.findById(idx);
        if (byId.isEmpty()) {
            throw new NotFoundException("존재하지 않는 id입니다.");
        }

        Account account = byId.get();
        account.setName(newAccount.getName());

        return accountRepository.save(account);
    }

    public void deleteAccount(Account deleteDTO) {
        accountRepository.delete(deleteDTO);
    }

    public void deleteAccountbyId(Integer idx) {
        Optional<Account> byId = accountRepository.findById(idx);
        if (byId.isEmpty()) {
            throw new NotFoundException("존재하지 않는 id입니다");
        }
        accountRepository.delete(byId.get());
    }

    /**
     * 중복 회원 검증 메소드
     */
    private void validateDuplicateAccount(Account account) {
        accountRepository.findByName(account.getName())
                .ifPresent(a -> {
                    throw new DuplicatedException("이미 존재하는 회원입니다.");
                });
    }

}
