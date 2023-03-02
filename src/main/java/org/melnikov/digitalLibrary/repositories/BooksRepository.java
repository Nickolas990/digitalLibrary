package org.melnikov.digitalLibrary.repositories;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.melnikov.digitalLibrary.models.Book;
import org.melnikov.digitalLibrary.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Nikolay Melnikov
 */
@Component
public class BooksRepository implements ListCrudRepository<Book, Integer> {

    private final SessionFactory sessionFactory;

    @Autowired
    public BooksRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public <S extends Book> S save(S book) {
        try (Session session = sessionFactory.openSession()) {
           session.persist(book);
           return book;
       }
    }

    @Override
    @Transactional
    public <S extends Book> List<S> saveAll(Iterable<S> entities) {
        try(Session session = sessionFactory.openSession()) {
            List<S> books = new ArrayList<>((Collection<S>) entities);
            entities.forEach(session::persist);
            return books;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Book book = session.get(Book.class, id);
            Hibernate.initialize(book.getClient());
            return Optional.of(book);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Book book = session.get(Book.class, id);
            Hibernate.initialize(book.getClient());
            return true;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Book", Book.class).getResultList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAllById(Iterable<Integer> ints) {

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Book where id in (:ints)", Book.class)
                    .setParameter("ints", ints).getResultList();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Book> findByTitle(String title) {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("from Book b where b.title = :title", Book.class)
                    .setParameter("title", title)
                    .stream()
                    .findFirst();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select count(b) from Book b", Long.class)
                    .getSingleResult();
        }
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        try (Session session = sessionFactory.openSession()) {
            Book book = session.get(Book.class, id);
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setYearOfPublication(updatedBook.getYearOfPublication());
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Book book = session.get(Book.class, id);
            session.remove(book);
        }
    }

    @Override
    @Transactional
    public void delete(Book bookToDelete) {
        try (Session session = sessionFactory.openSession()) {
            Book book = session.get(Book.class, bookToDelete.getId());
            session.remove(book);
        }
    }

    @Override
    @Transactional
    public void deleteAllById(Iterable<? extends Integer> ids) {
        try (Session session = sessionFactory.openSession()) {
            session.createQuery("delete from Book where id in (:ids)", Book.class)
                    .setParameter("ids", ids);
        }
    }

    @Override
    @Transactional
    public void deleteAll(Iterable<? extends Book> entities) {
        try(Session session = sessionFactory.openSession()) {
            for (Book book : entities) {
                session.createQuery("delete from Book b where b.id = :id", Book.class)
                        .setParameter("id", book.getId())
                        .executeUpdate();
            }
        }
    }

    @Override
    @Transactional
    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.createQuery("delete from Book").executeUpdate();
        }
    }

    @Transactional
    public void release(int id) {
        try (Session session = sessionFactory.openSession()) {
            Book book = session.get(Book.class, id);
            book.setClient(null);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Person> getOwner(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Book book = session.get(Book.class, id);
            Person owner = book.getClient();
            return Optional.ofNullable(owner);
        }
    }

    @Transactional
    public void assign(int id, Person selectedPerson) {

        try (Session session = sessionFactory.openSession()) {
            Person newOwner = session.get(Person.class, selectedPerson.getId());
            Book book = session.get(Book.class, id);
            book.setClient(newOwner);
            newOwner.getBooks().add(book);
        }
    }
}
