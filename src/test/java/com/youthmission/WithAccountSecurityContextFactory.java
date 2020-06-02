package com.youthmission;

import com.youthmission.account.AccountService;
import com.youthmission.account.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {//우리가 방금 만든 애노테이션 타입 주기

    // 빈을 주입 받을 수 있다.
    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String email = withAccount.value();

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail(email);
        signUpForm.setName("김모세");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);

        // Authentication 만들고 SecurityuContext에 넣어주기
        //테스트에서 @WithUserDetails가 하는 일
        UserDetails principal = accountService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }

}
