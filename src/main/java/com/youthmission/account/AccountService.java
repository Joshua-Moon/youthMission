package com.youthmission.account;

import com.youthmission.domain.Account;
import com.youthmission.settings.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Account processNewAccount(SignUpForm signUpForm){
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .name(signUpForm.getName())
                .church(signUpForm.getChurch())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .umionEnrollmentResultByWeb(true)
                .umionUpdatedByWeb(true)
                .build();
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("유미온, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email="
                + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);
            if (account == null){
                throw new UsernameNotFoundException(email);
            }

            return new UserAccount(account); // 프린시펄 상태인 유저어카운트 리턴
    }

    public void completeSignUp(Account account){
        account.completeSignUp();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        account.setBio(profile.getBio());
        account.setUrl(profile.getUrl());
        account.setChurch(profile.getChurch());
        account.setOccupation(profile.getOccupation());
        account.setPhoneNumber(profile.getPhoneNumber());
        account.setProfileImage(profile.getProfileImage());
        accountRepository.save(account);
    }
}
