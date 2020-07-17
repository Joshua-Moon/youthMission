package com.youthmission.domain;

import com.youthmission.account.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchoolTest {


    School school;
    Account account;
    UserAccount userAccount;

    @BeforeEach
    void beforeEach() {
        school = new School();
        account = new Account();
        account.setEmail("ho513@naver.com");
        account.setPassword("123");
        userAccount = new UserAccount(account);

    }

    @DisplayName("교회학교를 공개했고 인원 모집 중이고, 이미 멤버나 교회학교 관리자가 아니라면 교회학교 가입 가능")
    @Test
    void isJoinable() {
        school.setPublished(true);
        //school.setRecruiting(true);

        assertTrue(school.isJoinable(userAccount));
    }

    @DisplayName("교회학교를 공개했고 인원 모집 중이더라도, 교회학교 관리자는 교회학교 가입이 불필요하다.")
    @Test
    void isJoinable_false_for_manager() {
        school.setPublished(true);
        //school.setRecruiting(true);
        school.addManager(account);

        assertFalse(school.isJoinable(userAccount));
    }

    @DisplayName("교회학교를 공개했고 인원 모집 중이더라도, 교회학교 멤버는 교회학교 재가입이 불필요하다.")
    @Test
    void isJoinable_false_for_member() {
        school.setPublished(true);
        //school.setRecruiting(true);
        school.addMemeber(account);

        assertFalse(school.isJoinable(userAccount));
    }

//    @DisplayName("교회학교가 비공개거나 인원 모집 중이 아니면 교회학교 가입이 불가능하다.")
//    @Test
//    void isJoinable_false_for_non_recruiting_school() {
//        school.setPublished(true);
//        //school.setRecruiting(false);
//
//        assertFalse(school.isJoinable(userAccount));
//
//        school.setPublished(false);
//        //school.setRecruiting(true);
//
//        assertFalse(school.isJoinable(userAccount));
//    }

    @DisplayName("교회학교 관리자인지 확인")
    @Test
    void isManager() {
        school.addManager(account);
        assertTrue(school.isManager(userAccount));
    }

    @DisplayName("교회학교 멤버인지 확인")
    @Test
    void isMember() {
        school.addMemeber(account);
        assertTrue(school.isMember(userAccount));
    }

}