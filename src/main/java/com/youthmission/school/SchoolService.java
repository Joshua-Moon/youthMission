package com.youthmission.school;

import com.youthmission.domain.Account;
import com.youthmission.domain.School;
import com.youthmission.school.validator.SchoolDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.youthmission.school.form.SchoolForm.VALID_PATH_PATTERN;

@Service
@Transactional
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final ModelMapper modelMapper;

    public School createNewSchool(School school, Account account) {
        School newSchool = schoolRepository.save(school);
        newSchool.addManager(account);
        return newSchool;
    }

    public School getSchoolToUpdate(Account account, String path) {
        School school = this.getSchool(path);
        if (!account.isManagerOf(school)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
        return school;
    }

    public School getSchool(String path) {
        School school = this.schoolRepository.findByPath(path);
        checkIfExistingSchool(path, school);
        return school;
    }

    public void updateSchoolDescription(School school, SchoolDescriptionForm schoolDescriptionForm) {
        modelMapper.map(schoolDescriptionForm, school);
    }

    public void updateSchoolImage(School school, String image) {
        school.setImage(image);
    }

    public void enableSchoolBanner(School school) {
        school.setUseBanner(true);
    }

    public void disableSchoolBanner(School school) {
        school.setUseBanner(false);
    }

    public School getSchoolToUpdateStatus(Account account, String path) {
        School school = schoolRepository.findSchoolWithManagersByPath(path);
        checkIfExistingSchool(path, school);
        checkIfManager(account, school);
        return school;
    }

    private void checkIfManager(Account account, School school) {
        if (!account.isManagerOf(school)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    private void checkIfExistingSchool(String path, School school) {
        if (school == null) {
            throw new IllegalArgumentException(path + "에 해당하는 교회학교가 없습니다.");
        }
    }

    public void publish(School school) {
        school.publish();
    }


    public void close(School school) {
        school.close();
    }

    public void startRecruit(School school) {
        school.startRecruit();
    }

    public void stopRecruit(School school) {
        school.stopRecruit();
    }

    public boolean isValidPath(String newPath) {
        if (!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }

        return !schoolRepository.existsByPath(newPath);
    }

    public void updateSchoolPath(School school, String newPath) {
        school.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 50;
    }

    public void updateSchoolTitle(School school, String newTitle) {
        school.setTitle(newTitle);
    }

    public void remove(School school) {
        if (school.isRemovable()) {
            schoolRepository.delete(school);
        } else {
            throw new IllegalArgumentException("교회학교를 삭제할 수 없습니다.");
        }
    }

    public void addMember(School school, Account account) {
        school.addMember(account);
    }

    public void removeMember(School school, Account account) {
        school.removeMember(account);
    }

    public School getSchoolToEnroll(String path) {
        School school = schoolRepository.findSchoolOnlyByPath(path);
        checkIfExistingSchool(path, school);
        return school;
    }
}