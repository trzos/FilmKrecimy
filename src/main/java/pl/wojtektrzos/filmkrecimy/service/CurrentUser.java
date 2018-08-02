package pl.wojtektrzos.filmkrecimy.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CurrentUser extends User {
    private final pl.wojtektrzos.filmkrecimy.entity.User user;

    public CurrentUser(String username, String password, Collection<?
            extends GrantedAuthority> authorities,
                       pl.wojtektrzos.filmkrecimy.entity.User user) {
        super(username, password, authorities);
        this.user = user;
    }

    public pl.wojtektrzos.filmkrecimy.entity.User getUser() {
        return user;
    }
}