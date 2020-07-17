package com.youthmission.student;

import com.youthmission.account.CurrentAccount;
import com.youthmission.domain.Account;
import com.youthmission.domain.School;
import com.youthmission.domain.Student;
import com.youthmission.school.SchoolService;
import com.youthmission.student.form.StudentForm;
import com.youthmission.student.validator.StudentValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/school/{path}")
@RequiredArgsConstructor
public class StudentController {

    private final SchoolService schoolService;
    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;
    private final StudentValidator studentValidator;

    @InitBinder("studentForm")
    public void initBinder(WebDataBinder webDataBinder) { webDataBinder.addValidators(studentValidator);}

    @GetMapping("/new-student")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        School school = schoolService.getSchoolToUpdateStatus(account, path);
        model.addAttribute(school);
        model.addAttribute(account);
        model.addAttribute(new StudentForm());
        return "student/form";
    }

    @PostMapping("/new-student")
    public String newStudentSubmit(@CurrentAccount Account account, @PathVariable String path,
                                   @Valid StudentForm studentForm, Errors errors, Model model) {
        School school = schoolService.getSchoolToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(school);
            return "student/form";
        }

        Student student = studentService.createStudent(modelMapper.map(studentForm, Student.class), school, account);
        return "redirect:/school/" + school.getEncodedPath() + "/students/" + student.getId();
    }

    @GetMapping("/student/{id}")
    public String getStudent(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id,
                             Model model) {
        model.addAttribute(account);
        model.addAttribute(studentRepository.findById(id).orElseThrow());
        model.addAttribute(schoolService.getSchool(path));
        return "student/view";
    }
}
