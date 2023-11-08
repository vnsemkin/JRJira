package com.javarush.jira.login.internal.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.javarush.jira.common.internal.config.jwt.JwtTokenUtils;
import com.javarush.jira.common.util.validation.View;
import com.javarush.jira.login.AuthUser;
import com.javarush.jira.login.User;
import com.javarush.jira.login.UserTo;
import com.javarush.jira.login.internal.UserRepository;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.javarush.jira.common.BaseHandler.createdResponse;

@Validated
@RestController
@RequestMapping(UserController.REST_URL)
@CacheConfig(cacheNames = "users")
@RequiredArgsConstructor
public class UserController extends AbstractUserController {
    public static final String REST_URL = "/api/users";
    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> createWithLocation(@Validated(View.OnCreate.class) @RequestBody UserTo userTo) {
        User created = handler.createFromTo(userTo);
        return createdResponse(REST_URL, created);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @JsonView(View.OnUpdate.class)
    public void update(@Validated(View.OnUpdate.class) @RequestBody UserTo userTo, @AuthenticationPrincipal AuthUser authUser) {
        authUser.setUser(handler.updateFromTo(userTo, authUser.id()));
    }

    @GetMapping
    public User get(@RequestHeader(value = "authorization") String token) {
        String username = jwtTokenUtils.getUsername(token);
        User user = userRepository.getExistedByEmail(username);
        Long id = user.getId();
        return handler.get(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(key = "#authUser.user.email")
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        handler.delete(authUser.id());
    }

    @PostMapping("/change_password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(key = "#authUser.user.email")
    public void changePassword(@RequestParam String oldPassword, @Size(min = 5, max = 128) @RequestParam String newPassword, @AuthenticationPrincipal AuthUser authUser) {
        changePassword0(oldPassword, newPassword, authUser.id());
    }
}
