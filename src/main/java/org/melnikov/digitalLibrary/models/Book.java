package org.melnikov.digitalLibrary.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "book", schema = "public")
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title")
    @NotEmpty
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @Column(name = "author")
    @NotNull
    @NotEmpty
    @Size(min = 2, max = 100, message = "Author must be between 2 and 100 characters")
    private String author;

    @Column(name = "year_of_publication")
    @NotNull
    private Integer yearOfPublication;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person client;

    @PreRemove
    public void preRemove() {
        client.getBooks().remove(this);
    }

}