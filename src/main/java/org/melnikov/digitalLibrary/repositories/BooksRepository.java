package org.melnikov.digitalLibrary.repositories;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.melnikov.digitalLibrary.models.Book;
import org.melnikov.digitalLibrary.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.NoRepositoryBean;
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
@NoRepositoryBean
public class BooksRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public BooksRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Transactional
    public <S extends Book> S save(S book) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(book);
        return book;

    }


    @Transactional
    public <S extends Book> List<S> saveAll(Iterable<S> entities) {
        Session session = sessionFactory.openSession();
        List<S> books = new ArrayList<>((Collection<S>) entities);
        entities.forEach(session::persist);
        return books;
    }


    @Transactional(readOnly = true)
    public Optional<Book> findById(Integer id) {
        Session session = sessionFactory.openSession();
        Book book = session.get(Book.class, id);
        Hibernate.initialize(book.getClient());
        return Optional.of(book);
    }


    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        Session session = sessionFactory.openSession();
        Book book = session.get(Book.class, id);
        Hibernate.initialize(book.getClient());
        return true;
    }


    @Transactional(readOnly = true)
    public List<Book> findAll() {
        Session session = sessionFactory.openSession();
        return session.createQuery("from Book", Book.class).getResultList();

    }


    @Transactional(readOnly = true)
    public List<Book> findAllById(Iterable<Integer> ints) {

        Session session = sessionFactory.openSession();
        return session.createQuery("from Book where id in (:ints)", Book.class)
                .setParameter("ints", ints).getResultList();
    }

    @Transactional(readOnly = true)
    public Optional<Book> findByTitle(String title) {
        Session session = sessionFactory.openSession();
        return session.createQuery("from Book b where b.title = :title", Book.class)
                .setParameter("title", title)
                .stream()
                .findFirst();
    }


    @Transactional(readOnly = true)
    public long count() {
        Session session = sessionFactory.openSession();
        return session.createQuery("select count(b) from Book b", Long.class)
                .getSingleResult();

    }

    @Transactional
    public void update(int id, Book updatedBook) {
        Session session = sessionFactory.openSession();
        Book book = session.get(Book.class, id);
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setYearOfPublication(updatedBook.getYearOfPublication());

    }


    @Transactional
    public void deleteById(Integer id) {
        Session session = sessionFactory.openSession();
        Book book = session.get(Book.class, id);
        session.remove(book);

    }


    @Transactional
    public void delete(Book bookToDelete) {
        Session session = sessionFactory.openSession();
        Book book = session.get(Book.class, bookToDelete.getId());
        session.remove(book);
    }


    @Transactional
    public void deleteAllById(Iterable<? extends Integer> ids) {
        Session session = sessionFactory.openSession();
        session.createQuery("delete from Book where id in (:ids)", Book.class)
                .setParameter("ids", ids);
    }


    @Transactional
    public void deleteAll(Iterable<? extends Book> entities) {
        Session session = sessionFactory.openSession();
        for (Book book : entities) {
            session.createQuery("delete from Book b where b.id = :id", Book.class)
                    .setParameter("id", book.getId())
                    .executeUpdate();
        }
    }


    @Transactional
    public void deleteAll() {
        Session session = sessionFactory.openSession();
        session.createQuery("delete from Book").executeUpdate();
    }

    @Transactional
    public void release(int id) {
        Session session = sessionFactory.getCurrentSession();
        Book book = session.get(Book.class, id);
        book.setClient(null);
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
        Session session = sessionFactory.getCurrentSession();
        Person newOwner = session.get(Person.class, selectedPerson.getId());
        Book book = session.get(Book.class, id);
        book.setClient(newOwner);
        newOwner.getBooks().add(book);

    }
}
