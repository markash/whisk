package org.acme.kogito.service;

import javax.enterprise.inject.Default;

import org.acme.kogito.model.Person;

@Default
public class PersonServiceImpl {

    public Person write(Person person) {

        System.err.print("Haha:" + person);
    
        return person;
    }
}