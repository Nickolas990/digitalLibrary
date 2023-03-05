package org.melnikov.digitalLibrary.controllers;

import jakarta.validation.Valid;
import org.melnikov.digitalLibrary.models.Book;
import org.melnikov.digitalLibrary.models.Person;
import org.melnikov.digitalLibrary.repositories.BooksRepository;
import org.melnikov.digitalLibrary.repositories.PersonRepository;
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
@RequestMapping("/books")
public class BooksController {


    private final PersonRepository personRepository;
    private final BooksRepository booksRepository;

    public BooksController(PersonRepository personRepository, BooksRepository booksRepository) {
        this.personRepository = personRepository;
        this.booksRepository = booksRepository;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("books", booksRepository.findAll());
        return "books/index";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "/books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/new";
        }

        booksRepository.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        Optional<Book> bookToShow = booksRepository.findById(id);
        bookToShow.ifPresent(book -> model.addAttribute("book", book));
        Optional <Person> bookOwner = booksRepository.getOwner(id);

        if (bookOwner.isPresent()) {
            model.addAttribute("bookOwner", bookOwner.get());
        } else {
            model.addAttribute("people", personRepository.findAll());
        }
        model.addAttribute("person", new Person());

        return "books/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        Optional<Book> bookToEdit = booksRepository.findById(id);
        bookToEdit.ifPresent(book -> model.addAttribute("book", book));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult, @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "books/edit";
        }
        booksRepository.update(id, book);
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksRepository.deleteById(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        booksRepository.release(id);
        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        booksRepository.assign(id, selectedPerson);
        return "redirect:/books/" + id;
    }

}
