package me.iseunghan.learncrud.Accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Test
    public void 중복회원() {

        // Given
        Account account = new Account();
        account.setIdx(1);
        account.setName("spring");

        Account account1 = new Account();
        account.setIdx(2);
        account.setName("spring");



        accountService.join(account);

        try {
            accountService.join(account1);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }
    }

    @Test
    public void 예측테스트() {
        // Expected
        Account account = new Account();
        account.setName("name");
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(Matchers.is("이미 존재하는 회원입니다."));




        accountService.join(account);

        // When

    }
}
