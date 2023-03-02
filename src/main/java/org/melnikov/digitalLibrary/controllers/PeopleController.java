package org.melnikov.digitalLibrary.controllers;

import jakarta.validation.Valid;
import org.melnikov.digitalLibrary.models.Person;
import org.melnikov.digitalLibrary.repositories.PersonRepository;
import org.melnikov.digitalLibrary.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author Nikolay Melnikov
 */
@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PersonRepository personRepository;

    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PersonRepository personRepository, PersonValidator personValidator) {
        this.personRepository = personRepository;
        this.personValidator = personValidator;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", personRepository.findAll());
        return "people/index";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "/people/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            return "people/new";
        }

        personRepository.save(person);
        return "redirect:/people";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        Optional<Person> personToShow = personRepository.findById(id);
        personToShow.ifPresent(person -> model.addAttribute("person", person));
        personToShow.ifPresent(person -> model.addAttribute("books", person.getBooks()));
        return "people/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        Optional<Person> personToEdit = personRepository.findById(id);
        personToEdit.ifPresent(person -> model.addAttribute("person", person));
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult, @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "people/edit";
        }
        personRepository.update(id, person);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        personRepository.deleteById(id);
        return "redirect:/people";
    }
}
