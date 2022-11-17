package ru.project.hibernateJpa.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.project.hibernateJpa.model.Person;
import ru.project.hibernateJpa.service.PeopleService;

@Component
public class PersonValidator implements Validator {//более сложная валидация
    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        if (peopleService.getPersonByName(person.getName()).isPresent()) {
            // поле, код ошибки, сообщение ошибки
            errors.rejectValue("name", "", "Человек с таким именем уже существует");
        }

        // Проверяем, что у человека имя начинается с заглавной буквы
        // Если имя не начинается с заглавной буквы - выдаем ошибку
        if (!Character.isUpperCase(person.getName().codePointAt(0)))
            errors.rejectValue("name", "", "Имя должно начинаться с заглавной буквы");
    }
}
