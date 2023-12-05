package ru.intel.credits.tests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Slice<Student> findAllByFirstName(String firstName, Pageable page);

    Page<Student> findAllByLastName(String lastName, Pageable page);
}