package org.melnikov.digitalLibrary.util;

import org.melnikov.digitalLibrary.models.Person;
import org.melnikov.digitalLibrary.repositories.PersonRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.function.Predicate;

/**
 * @author Nikolay Melnikov
 */
@Component
public class PersonValidator implements Validator {

    private final PersonRepository personRepository;

    public PersonValidator(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        Predicate<Class> isPerson = clazz::equals;
        return isPerson.test(Person.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        if (personRepository.findByName(person.getFullName()).isPresent()) {
            errors.rejectValue("fullName", "error.person.exists", "Person with this name already exists");
        }

    }
}
