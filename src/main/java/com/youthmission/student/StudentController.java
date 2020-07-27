package com.youthmission.student;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youthmission.account.AccountRepository;
import com.youthmission.account.AccountService;
import com.youthmission.account.CurrentAccount;
import com.youthmission.domain.Account;
import com.youthmission.domain.School;
import com.youthmission.domain.Student;
import com.youthmission.school.SchoolRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/school/{path}")
@RequiredArgsConstructor
public class StudentController {

    private final SchoolService schoolService;
    private final SchoolRepository schoolRepository;
    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;
    private final StudentValidator studentValidator;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    @InitBinder("studentForm")
    public void initBinder(WebDataBinder webDataBinder) { webDataBinder.addValidators(studentValidator);}

    @GetMapping("/students")
    public String viewSchoolStudents(@CurrentAccount Account account, @PathVariable String path, Model model) {
        School school = schoolService.getSchool(path);
        model.addAttribute(account);
        model.addAttribute(school);

        List<Student> students = studentRepository.findBySchoolOrderByStudentName(school);
        model.addAttribute("students", students);

        return "school/students";
    }

    @GetMapping("/new-student")
    public String newStudentForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        School school = schoolService.getSchoolToUpdateStatus(account, path);
        model.addAttribute(school);

        // 추후 내가 기존에 선택한 태그 정보를 가져와야할 때 필요할 수 있다.
//        Set<Account> tags = school.getMembers();
//        model.addAttribute("tags", tags.stream().map(Account::getName).collect(Collectors.toList()));

        List<String> allTags = accountRepository.findAll().stream().map(Account::nameEmail).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

        model.addAttribute(new StudentForm());
        return "student/form";
    }

    @PostMapping("/new-student")
    public String newStudentSubmit(@CurrentAccount Account account, @PathVariable String path,
                                   @Valid StudentForm studentForm, Errors errors, Model model) throws JsonProcessingException {
        School school = schoolService.getSchoolToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(school);
            List<String> allTags = accountRepository.findAll().stream().map(Account::nameEmail).collect(Collectors.toList());
            model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
            return "student/form";
    }
        String teacherEmail = studentService.getTeacherEmail(studentForm);
        Account teacher = accountRepository.findByEmail(teacherEmail);
        studentForm.setTeacher(teacher);

        Student student = studentService.createStudent(modelMapper.map(studentForm, Student.class), school, account);
        return "redirect:/school/" + school.getEncodedPath() + "/students";
    }

    @GetMapping("/student/{id}")
    public String getStudent(@CurrentAccount Account account, @PathVariable String path,
                             @PathVariable("id") Student student, Model model) {
        School school = schoolService.getSchoolToUpdate(account, path);
        model.addAttribute(school);
        model.addAttribute(account);
        model.addAttribute(student);

//        StudentForm studentForm = modelMapper.map(student, StudentForm.class);
//        if (student.getTeacher() != null) {
//            studentForm.setTeacherNameEmail(student.getTeacher().nameEmail());
//        }
//        model.addAttribute(studentForm);

        return "student/view";
    }

    @GetMapping("/student/{id}/edit")
    public String updateStudentForm(@CurrentAccount Account account, @PathVariable String path,
                             @PathVariable("id") Student student, Model model) throws JsonProcessingException {
        School school = schoolService.getSchoolToUpdate(account, path);
        model.addAttribute(school);
        model.addAttribute(account);
        model.addAttribute(student);
//        model.addAttribute(modelMapper.map(student, StudentForm.class));

        List<String> allTags = accountRepository.findAll().stream().map(Account::nameEmail).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

        StudentForm studentForm = modelMapper.map(student, StudentForm.class);
        if (student.getTeacher() != null) {
            studentForm.setTeacherNameEmail(student.getTeacher().nameEmail());
        }
        model.addAttribute(studentForm);

        return "student/update-form";
    }

    @PostMapping("/student/{id}/edit")
    public String updateStudentSubmit(@CurrentAccount Account account, @PathVariable String path,
                                      @PathVariable("id") Student student, @Valid StudentForm studentForm, Errors errors,
                                      Model model) throws JsonProcessingException {
        School school = schoolService.getSchoolToUpdateStatus(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(school);
            List<String> allTags = accountRepository.findAll().stream().map(Account::nameEmail).collect(Collectors.toList());
            model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));
            model.addAttribute(student);

            return "student/update-form";
        }

        String teacherEmail = studentService.getTeacherEmail(studentForm);
        Account teacher = accountRepository.findByEmail(teacherEmail);
        studentForm.setTeacher(teacher);

        studentService.updateStudent(student, studentForm);
        return "redirect:/school/" + school.getEncodedPath() + "/students";
    }

    @PostMapping("/student/{id}/delete")
    public String cancelEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Student student) {
        School school = schoolService.getSchoolToUpdateStatus(account, path);
        studentService.deleteEvent(student);
        return "redirect:/school/" + school.getEncodedPath() + "/students";
    }




}
