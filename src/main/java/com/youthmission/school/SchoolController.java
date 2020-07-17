package com.youthmission.school;

import com.youthmission.account.CurrentAccount;
import com.youthmission.domain.Account;
import com.youthmission.domain.School;
import com.youthmission.school.form.SchoolForm;
import com.youthmission.school.validator.SchoolFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolRepository schoolRepository;
    private final SchoolService schoolService;
    private final ModelMapper modelMapper;
    private final SchoolFormValidator schoolFormValidator;

    @InitBinder("schoolForm")
    public void schoolFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(schoolFormValidator);
    }

    @GetMapping("/new-school")
    public String newSchoolForm(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new SchoolForm());
        return "school/form";
    }

    @PostMapping("/new-school")
    public String newSchoolSubmit(@CurrentAccount Account account, @Valid SchoolForm schoolForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "school/form";
        }

        School newSchool = schoolService.createNewSchool(modelMapper.map(schoolForm, School.class), account);
        return "redirect:/school/" + URLEncoder.encode(newSchool.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/school/{path}")
    public String viewSchool(@CurrentAccount Account account, @PathVariable String path, Model model){
        School school = schoolService.getSchool(path);

        model.addAttribute(account);
        model.addAttribute(school);
        return "school/view";
    }

    @GetMapping("/school/{path}/members")
    public String viewSchoolMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        School school = schoolService.getSchool(path);

        model.addAttribute(account);
        model.addAttribute(school);
        return "school/members";
    }

    @GetMapping("/school/{path}/join")
    public String joinSchool(@CurrentAccount Account account, @PathVariable String path) {
        School school = schoolRepository.findSchoolWithMembersByPath(path);
        schoolService.addMember(school, account);
        return "redirect:/school/" + school.getEncodedPath() + "/members";
    }

    @GetMapping("/school/{path}/leave")
    public String leaveSchool(@CurrentAccount Account account, @PathVariable String path) {
        School school = schoolRepository.findSchoolWithMembersByPath(path);
        schoolService.removeMember(school, account);
        return "redirect:/school/" + school.getEncodedPath() + "/members";
    }
}
