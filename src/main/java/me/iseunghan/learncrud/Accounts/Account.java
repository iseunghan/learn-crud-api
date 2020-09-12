package me.iseunghan.learncrud.Accounts;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Entity
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    private String name;

}
