package org.melnikov.digitalLibrary.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "person", schema = "public")
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(min = 2, max = 200)
    @NotNull
    @Column(name = "full_name", nullable = false, length = 200)
    @NotEmpty
    @Size(min = 10, max = 200, message = "Name must be between 10 and 200 characters")
    private String fullName;

    @Column(name = "year_of_birth")
    @Positive(message = "Year of birth must be positive")
    @Min(value = 1900, message = "Year of birth must be at least 1900")
    @Digits(integer = 4, fraction = 0, message = "Year of birth must be four digits")
    private Integer yearOfBirth;
}
