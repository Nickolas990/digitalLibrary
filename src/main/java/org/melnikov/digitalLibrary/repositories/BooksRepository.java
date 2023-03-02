package org.melnikov.digitalLibrary.repositories;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.melnikov.digitalLibrary.models.Book;
import org.melnikov.digitalLibrary.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Component;
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
    public <S extends Book> S save(S book) {

        try (Session session = sessionFactory.openSession()) {
           session.beginTransaction();
           session.persist(book);
           session.getTransaction().commit();
           return book;
       }
    }

    @Override
    public <S extends Book> List<S> saveAll(Iterable<S> entities) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            entities.forEach(session::persist);
            session.getTransaction().commit();
        }
        return (List<S>) entities;
    }

    @Override
    public Optional<Book> findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Book book = session.get(Book.class, id);
            Hibernate.initialize(book.getClient());
            return Optional.of(book);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Book book = session.get(Book.class, id);
            Hibernate.initialize(book.getClient());
            return true;
        }
    }

    @Override
    public List<Book> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Book", Book.class).getResultList();
        }
    }

    @Override
    public List<Book> findAllById(Iterable<Integer> ints) {

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Book where id in (:ints)", Book.class)
                    .setParameter("ints", ints).getResultList();
        }
    }

    public Optional<Book> findByTitle(String title) {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("from Book b where b.title = :title", Book.class)
                    .setParameter("title", title)
                    .stream()
                    .findFirst();
        }
    }

    @Override
    public long count() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select count(b) from Book b", Long.class)
                    .getSingleResult();
        }
    }

    public void update(int id, Book updatedBook) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Book book = session.get(Book.class, id);

            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setYearOfPublication(updatedBook.getYearOfPublication());

            session.merge(book);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Book book = session.get(Book.class, id);
            session.delete(book);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Book bookToDelete) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Book book = session.get(Book.class, bookToDelete.getId());
            session.remove(book);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> ids) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from Book where id in (:ids)", Book.class)
                    .setParameter("ids", ids);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteAll(Iterable<? extends Book> entities) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            for (Book book : entities) {
                session.createQuery("delete from Book b where b.id = :id", Book.class)
                        .setParameter("id", book.getId())
                        .executeUpdate();
                session.getTransaction().commit();
            }
        }
    }

    @Override
    public void deleteAll() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from Book").executeUpdate();
            session.getTransaction().commit();
        }
    }

    public void release(int id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Book book = session.get(Book.class, id);
            book.setClient(null);
            session.merge(book);
            session.getTransaction().commit();
        }
    }

    public Optional<Person> getOwner(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            Book book = session.get(Book.class, id);
            Person owner = book.getClient();
            return Optional.ofNullable(owner);
        }
    }

    public void assign(int id, Person selectedPerson) {

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Person newOwner = session.get(Person.class, selectedPerson.getId());
            Book book = session.get(Book.class, id);
            book.setClient(newOwner);
            newOwner.getBooks().add(book);
            session.merge(book);
            session.getTransaction().commit();
        }
    }
}
