package com.youthmission.school;

import com.youthmission.account.CurrentUser;
import com.youthmission.domain.Account;
import com.youthmission.domain.School;
import com.youthmission.school.form.SchoolForm;
import com.youthmission.school.validator.SchoolFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final SchoolService schoolService;
    private final ModelMapper modelMapper;
    private final SchoolFormValidator schoolFormValidator;

    @InitBinder("schoolForm")
    public void schoolFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(schoolFormValidator);
    }

    @GetMapping("/new-school")
    public String newScoolForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new SchoolForm());
        return "school/form";
    }

    @PostMapping("/new-school")
    public String newStudySubmit(@CurrentUser Account account, @Valid SchoolForm schoolForm, Errors errors) {
        if (errors.hasErrors()) {
            return "school/form";
        }

        School newSchool = schoolService.createNewStudy(modelMapper.map(schoolForm, School.class), account);
        return "redirect:/school/" + URLEncoder.encode(newSchool.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/school/{path}")
    public String viewSchool(@CurrentUser Account account)
}
