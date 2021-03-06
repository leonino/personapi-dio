package one.digitalinovation.personapi.service;

import one.digitalinovation.personapi.dto.request.PersonDTO;
import one.digitalinovation.personapi.dto.response.MessageResponseDTO;
import one.digitalinovation.personapi.entity.Person;
import one.digitalinovation.personapi.exception.PersonNotFoundException;
import one.digitalinovation.personapi.mapper.PersonMapper;
import one.digitalinovation.personapi.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final PersonMapper personMapper = PersonMapper.INSTANCE;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public MessageResponseDTO createPerson(PersonDTO personDTO) {
        Person personToSave = PersonMapper.INSTANCE.toModel(personDTO);

        Person savePerson = personRepository.save(personToSave);
        return createdMessageResponse(savePerson.getId(), "Created person with ID ");
    }

    public List<PersonDTO> listAll() {
        List<Person> allPerson = personRepository.findAll();
        return allPerson.stream()
                .map(personMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PersonDTO findById(Long id) throws PersonNotFoundException {
        Person person = verifyExists(id);
        return personMapper.toDTO(person);
    }

    public void deleteById(Long id) throws PersonNotFoundException {
        Person person = verifyExists(id);
        personRepository.delete(person);
    }

    public MessageResponseDTO updateById(Long id, PersonDTO personDTO) throws PersonNotFoundException {
        verifyExists(id);
        Person personToUpdate = PersonMapper.INSTANCE.toModel(personDTO);
        Person updatePerson = personRepository.save(personToUpdate);
        return createdMessageResponse(updatePerson.getId(), "Updated person with ID ");
    }

    private MessageResponseDTO createdMessageResponse(Long id, String message) {
        return MessageResponseDTO
                .builder()
                .message(message + id)
                .build();
    }

    private Person verifyExists(Long id) throws PersonNotFoundException {
        return personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
    }

}
