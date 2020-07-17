package com.youthmission.school;

import com.youthmission.account.CurrentAccount;
import com.youthmission.domain.Account;
import com.youthmission.domain.School;
import com.youthmission.school.validator.SchoolDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/school/{path}/settings")
@RequiredArgsConstructor
public class SchoolSettingController {
    
    private final SchoolService schoolService;
    private final ModelMapper modelMapper;

    @GetMapping("/description")
    public String viewSchoolSetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        School school = schoolService.getSchoolToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(school);
        model.addAttribute(modelMapper.map(school, SchoolDescriptionForm.class));
        return "school/settings/description";
    }

    @PostMapping("/description")
    public String updateSchoolInfo(@CurrentAccount Account account, @PathVariable String path,
                                  @Valid SchoolDescriptionForm schoolDescriptionForm, Errors errors,
                                  Model model, RedirectAttributes attributes) {
        School school = schoolService.getSchoolToUpdate(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(school);
            return "school/settings/description";
        }

        schoolService.updateSchoolDescription(school, schoolDescriptionForm);
        attributes.addFlashAttribute("message", "교회학교 소개를 수정했습니다.");
        return "redirect:/school/" + getPath(path) + "/settings/description";
    }

    private String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @GetMapping("/banner")
    public String schoolImageForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        School school = schoolService.getSchoolToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(school);
        return "school/settings/banner";
    }

    @PostMapping("/banner")
    public String schoolImageSubmit(@CurrentAccount Account account, @PathVariable String path,
                                   String image, RedirectAttributes attributes) {
        School school = schoolService.getSchoolToUpdate(account, path);
        schoolService.updateSchoolImage(school, image);
        attributes.addFlashAttribute("message", "교회학교 이미지를 수정했습니다.");
        return "redirect:/school/" + getPath(path) + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableSchoolBanner(@CurrentAccount Account account, @PathVariable String path) {
        School school = schoolService.getSchoolToUpdate(account, path);
        schoolService.enableSchoolBanner(school);
        return "redirect:/school/" + getPath(path) + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableSchoolBanner(@CurrentAccount Account account, @PathVariable String path) {
        School school = schoolService.getSchoolToUpdate(account, path);
        schoolService.disableSchoolBanner(school);
        return "redirect:/school/" + getPath(path) + "/settings/banner";
    }

    @GetMapping("/school")
    public String schoolSettingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        School school = schoolService.getSchoolToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(school);
        return "school/settings/school";
    }

    @PostMapping("/school/publish")
    public String publishSchool(@CurrentAccount Account account, @PathVariable String path,
                               RedirectAttributes attributes) {
        School school = schoolService.getSchoolToUpdateStatus(account, path);
        schoolService.publish(school);
        attributes.addFlashAttribute("message", "교회학교를 공개했습니다.");
        return "redirect:/school/" + school.getEncodedPath() + "/settings/school";
    }

    @PostMapping("/school/close")
    public String closeSchool(@CurrentAccount Account account, @PathVariable String path,
                             RedirectAttributes attributes) {
        School school = schoolService.getSchoolToUpdateStatus(account, path);
        schoolService.close(school);
        attributes.addFlashAttribute("message", "교회학교를 종료했습니다.");
        return "redirect:/school/" + school.getEncodedPath() + "/settings/school";
    }

    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                               RedirectAttributes attributes) {
        School school = schoolService.getSchoolToUpdateStatus(account, path);
        if (!school.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/school/" + school.getEncodedPath() + "/settings/school";
        }

        schoolService.startRecruit(school);
        attributes.addFlashAttribute("message", "인원 모집을 시작합니다.");
        return "redirect:/school/" + school.getEncodedPath() + "/settings/school";
    }

    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                              RedirectAttributes attributes) {
        School school = schoolService.getSchoolToUpdate(account, path);
        if (!school.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/school/" + school.getEncodedPath() + "/settings/school";
        }

        schoolService.stopRecruit(school);
        attributes.addFlashAttribute("message", "인원 모집을 종료합니다.");
        return "redirect:/school/" + school.getEncodedPath() + "/settings/school";
    }



    @PostMapping("/school/path")
    public String updateSchoolPath(@CurrentAccount Account account, @PathVariable String path, String newPath,
                                  Model model, RedirectAttributes attributes) {
        School school = schoolService.getSchoolToUpdateStatus(account, path);
        if (!schoolService.isValidPath(newPath)) {
            model.addAttribute(account);
            model.addAttribute(school);
            model.addAttribute("schoolPathError", "해당 교회학교 경로는 사용할 수 없습니다. 다른 값을 입력하세요.");
            return "school/settings/school";
        }

        schoolService.updateSchoolPath(school, newPath);
        attributes.addFlashAttribute("message", "교회학교 경로를 수정했습니다.");
        return "redirect:/school/" + school.getEncodedPath() + "/settings/school";
    }

    @PostMapping("/school/title")
    public String updateSchoolTitle(@CurrentAccount Account account, @PathVariable String path, String newTitle,
                                   Model model, RedirectAttributes attributes) {
        School school = schoolService.getSchoolToUpdateStatus(account, path);
        if (!schoolService.isValidTitle(newTitle)) {
            model.addAttribute(account);
            model.addAttribute(school);
            model.addAttribute("schoolTitleError", "교회학교 이름을 다시 입력하세요.");
            return "school/settings/school";
        }

        schoolService.updateSchoolTitle(school, newTitle);
        attributes.addFlashAttribute("message", "교회학교 이름을 수정했습니다.");
        return "redirect:/school/" + school.getEncodedPath() + "/settings/school";
    }

    @PostMapping("/school/remove")
    public String removeSchool(@CurrentAccount Account account, @PathVariable String path, Model model) {
        School school = schoolService.getSchoolToUpdateStatus(account, path);
        schoolService.remove(school);
        return "redirect:/";
    }
}
