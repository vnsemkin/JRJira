package com.javarush.jira.profile.internal.web;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.login.User;
import com.javarush.jira.login.internal.UserRepository;
import com.javarush.jira.profile.internal.ProfileRepository;
import com.javarush.jira.profile.internal.model.Profile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Optional;

import static com.javarush.jira.common.util.JsonUtil.writeValue;
import static com.javarush.jira.profile.internal.web.ProfileTestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ProfileRestControllerTest extends AbstractControllerTest {
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    public void shouldReturnStatusUnAuthorisedForMissingUser() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    public void shouldReturnStatusOkForExistingUser() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                // https://jira.spring.io/browse/SPR-14472
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    public void shouldUpdateProfileDetail() throws Exception {
        Optional<User> user = userRepository.findByEmailIgnoreCase(ADMIN_MAIL);
        if(user.isPresent()){
           long userId = user.get().id();
            Profile initialProfile = profileRepository.findById(userId).orElseThrow();
            perform(MockMvcRequestBuilders.put(REST_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(writeValue(USER_PROFILE_TO)))
                    .andExpect(status().is2xxSuccessful());
            Profile actualProfile = profileRepository.getOrCreate(userId);
            // Check contacts were updated
            Assertions.assertNotEquals(initialProfile, actualProfile);
            Assertions.assertNotEquals(initialProfile.getMailNotifications(), actualProfile.getMailNotifications());
        }
    }
}