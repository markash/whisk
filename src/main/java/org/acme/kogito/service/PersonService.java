package org.acme.kogito.service;

import javax.enterprise.context.ApplicationScoped;

import org.acme.kogito.model.Person;

@ApplicationScoped
public class PersonService {

    public Person write(Person person) {

        System.err.print("Haha:" + person);
    
        return person;
    }
}