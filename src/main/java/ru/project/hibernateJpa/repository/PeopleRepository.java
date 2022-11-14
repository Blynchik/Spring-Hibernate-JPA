package ru.project.hibernateJpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.project.hibernateJpa.model.Person;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
}
