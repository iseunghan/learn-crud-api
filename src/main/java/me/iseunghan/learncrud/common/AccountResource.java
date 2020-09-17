package me.iseunghan.learncrud.common;

import me.iseunghan.learncrud.Accounts.Account;
import me.iseunghan.learncrud.Accounts.AccountController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

//ver. 1.0.1 이후에 ResourceSupport -> RepresentationModel 로 변경됨.
public class AccountResource extends EntityModel<Account> {

    //그냥 링크로 보내게 되면 Event로 감싼 형태가 나오게 된다. -> "event" { {"id":1, "name" .... 이런식으로
    //@JsonUnwrapped를 사용하면 성공.

    //안에 unwrap이 들어있음.
    public AccountResource(Account content, Link... links) {
        super(content, links);
        add(linkTo(AccountController.class).slash(content.getIdx()).withSelfRel()); //self ReLation 생성
    }

}