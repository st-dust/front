package cz.zatisigroup.service;

import cz.zatisigroup.model.User;
import cz.zatisigroup.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class GetInfoService implements InfoService {

    private final UsersRepository userRepository;

    @Autowired
    public GetInfoService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
