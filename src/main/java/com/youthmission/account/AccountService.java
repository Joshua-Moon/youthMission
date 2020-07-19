package com.youthmission.account;

import com.youthmission.config.AppProperties;
import com.youthmission.domain.Account;
import com.youthmission.mail.EmailMessage;
import com.youthmission.mail.EmailService;
import com.youthmission.settings.form.Notifications;
import com.youthmission.settings.form.Profile;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    // 템플릿 엔진으로 HTML 본문채워 넣기
    private final TemplateEngine templateEngine;
    // local, dev, 운영 호스트 다 다르게 주기 위함
    private final AppProperties appProperties;

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
                .umionCreatedByWeb(true)
                .umionEnrollmentResultByWeb(true)
                .umionUpdatedByWeb(true)
                .build();
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
/*        EmailMessage emailMessage  = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("유미온, 회원 가입 인증")
                .message("/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email="
                        + newAccount.getEmail())
                .build();

        emailService.sendEmail(emailMessage);*/
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + newAccount.getEmailCheckToken()
                + "&email=" + newAccount.getEmail());
        context.setVariable("name", newAccount.getName());
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "유미온 서비스를 사용하려면 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(newAccount.getEmail())
                .subject("유미온, 회원 가입 인증")
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
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
        modelMapper.map(profile, account);
        accountRepository.save(account);
    }

    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotifications(Account account, Notifications notifications) {
        modelMapper.map(notifications, account);
        accountRepository.save(account);
    }

    public void sendLoginLink(Account account) {
        Context context = new Context();
        context.setVariable("link", "/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        context.setVariable("nickname", account.getName());
        context.setVariable("linkName", "유미온 로그인하기");
        context.setVariable("message", "로그인 하려면 아래 링크를 클릭하세요.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(account.getEmail())
                .subject("유미온, 로그인 링크")
                .message(message)
                .build();
        emailService.sendEmail(emailMessage);

    }

    public Account getAccount(String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new IllegalArgumentException(email + "에 해당하는 사용자가 없습니다.");
        }
        return account;
    }

}
