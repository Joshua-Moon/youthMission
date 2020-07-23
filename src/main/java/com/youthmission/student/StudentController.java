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

    @GetMapping("/new-student")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model)
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

        Account teacher = accountRepository.findByEmail(studentForm.getTeacherEmail());
        studentForm.setTeacher(teacher);
//        if (teacher == null) {
//            throw new IllegalArgumentException(path + "에 해당하는 담당선생님이 없습니다.");
//        }

        Student student = studentService.createStudent(modelMapper.map(studentForm, Student.class), school, account);
        return "redirect:/school/" + school.getEncodedPath() + "/students";
    }

    @GetMapping("/student/{id}") //TODO 한 학생마다 세밀한 정보 보는 뷰
    public String getStudent(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Student student,
                             Model model) {

        model.addAttribute(account);
        model.addAttribute(student);
        model.addAttribute(schoolService.getSchool(path));

        return "students/" + student.getId();
    }

    @GetMapping("/students")
    public String viewSchoolStudents(@CurrentAccount Account account, @PathVariable String path, Model model) {
        School school = schoolService.getSchool(path);
        model.addAttribute(account);
        model.addAttribute(school);

        List<Student> students = studentRepository.findBySchoolOrderByStudentName(school);
//        List<Student> newStudents = new ArrayList<>();
////        List<Student> oldStudents = new ArrayList<>();
////        students.forEach(e -> {
////            if (e.getEndDateTime().isBefore(LocalDateTime.now())) {
////                oldStudents.add(e);
////            } else {
////                newStudents.add(e);
////            }
////        });
////
////        model.addAttribute("newStudents", newStudents);
////        model.addAttribute("oldStudents", oldStudents);
        model.addAttribute("students", students);

        return "school/students";
    }
}
