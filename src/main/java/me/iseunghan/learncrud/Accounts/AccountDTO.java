package me.iseunghan.learncrud.Accounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Data
public class AccountDTO {
    @NotEmpty
    private String name;
}
