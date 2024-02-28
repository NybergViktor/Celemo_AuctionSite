package sidkbk.celemo.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sidkbk.celemo.dto.user.*;
import sidkbk.celemo.exceptions.EntityNotFoundException;
import sidkbk.celemo.models.EGender;
import sidkbk.celemo.models.ERole;
import sidkbk.celemo.models.Role;
import sidkbk.celemo.models.User;
import sidkbk.celemo.repositories.RoleRepository;
import sidkbk.celemo.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;


    // create/add/post user account
    public User createUser(CreateUserDTO createUserDTO){
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setPassword(createUserDTO.getPassword());
        user.setDateOfBirth(createUserDTO.getDateOfBirth());
        user.setEmail(createUserDTO.getEmail());
        user.setFirstName(createUserDTO.getFirstName());
        user.setLastName(createUserDTO.getLastName());
        user.setAdress_city(createUserDTO.getAdress_city());
        user.setAdress_street(createUserDTO.getAdress_street());
        user.setAdress_postalCode(createUserDTO.getAdress_postalCode());
        user.setGender(createUserDTO.getGender());
        //checks if  password is longer than 8 chars and contains atleast one upperCase
        user.isPasswordCorrect(user);

        //checks that gender isn't null
        if (user.getGender() == null){
            throw new RuntimeException("ERROR: no gender");
        } else if (user.getGender().equals("MALE")){ //string to enum
            user.setGender(EGender.MALE);
        } else if (user.getGender().equals("FEMALE")){//string to enum
            user.setGender(EGender.FEMALE);
        }

        Set<Role> roles = new HashSet<>();
        Set<String> strRoles = createUserDTO.getUsersRoles();
        if (strRoles.isEmpty()){
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: role is not found"));
            roles.add(userRole);
            user.setRoles(roles);
        }else {
            strRoles.forEach(role -> {
                switch (role) {
                case "ADMIN" -> {
                    Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Admin Role couldn't be found"));
                    roles.add(adminRole);
                    user.setRoles(roles);
                }
                case "USER" -> {
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role couldn't be found"));
                    roles.add(userRole);
                    user.setRoles(roles);
                }
                }
            });

        }

        return userRepository.save(user);
    }

    // get/find all user accounts
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    //find user variable with filter. For example : grade
    public String getUserFilter(FindUserIdandFilterDTO findUserIdandFilterDTO){ //userId and filter, filter can be grade, username, firstName, lastName
        User user = userRepository.findById(findUserIdandFilterDTO.getUserId()).get();
        return  user.getFilter(findUserIdandFilterDTO.getFilter());

    }

    // get/find user account using id
    public Optional<User> getUserById(FindUserIdDTO findUserIdDTO){
        return userRepository.findById(findUserIdDTO.getUserId());
    }

    // PUT/update user account. checks that new value isn't empty before adding. If something is empty then it will throw EntityNotFoundException
    public User updateUser(UpdateUserDTO updateUserDTO){
        Set<Role> roles = new HashSet<>();
        Set<String> strRoles = updateUserDTO.getUsersRoles();
        return userRepository.findById(updateUserDTO.getUserId())
        .map(existingUser -> {
            if(updateUserDTO.getUsername()!=null){
                existingUser.setUsername(updateUserDTO.getUsername());
            }if(updateUserDTO.getPassword()!=null){
                existingUser.setPassword(updateUserDTO.getPassword());
            }if(updateUserDTO.getDateOfBirth()!=null){
                existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
            }if(updateUserDTO.getEmail()!=null){
                existingUser.setEmail(updateUserDTO.getEmail());
            }if(updateUserDTO.getFirstName()!=null){
                existingUser.setFirstName(updateUserDTO.getFirstName());
            }if(updateUserDTO.getLastName()!=null){
                existingUser.setLastName(updateUserDTO.getLastName());
            }if(updateUserDTO.getAdress_street()!=null){
                existingUser.setAdress_street(updateUserDTO.getAdress_street());
            }if(updateUserDTO.getAdress_city()!=null){
                existingUser.setAdress_city(updateUserDTO.getAdress_city());
            }if(updateUserDTO.getAdress_postalCode()!=null){
                existingUser.setAdress_postalCode(updateUserDTO.getAdress_postalCode());
            }if(updateUserDTO.getBalance()!=0.0){
                existingUser.setBalance(updateUserDTO.getBalance());
            }
            if (updateUserDTO.getGender() != null) {
                existingUser.setGender(updateUserDTO.getGender());
            }
            if(updateUserDTO.getPhoto()!=null){
                existingUser.setPhoto(updateUserDTO.getPhoto());
            }if (strRoles.isEmpty()){
                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: role is not found"));
                roles.add(userRole);
                existingUser.setRoles(roles);
            }else {
                strRoles.forEach(role -> {
                    switch (role) {
                        case "ADMIN" -> {
                            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Admin Role couldn't be found"));
                            roles.add(adminRole);
                            existingUser.setRoles(roles);
                        }
                        case "USER" -> {
                            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role couldn't be found"));
                            roles.add(userRole);
                            existingUser.setRoles(roles);
                        }
                    }
                });

            }
            return userRepository.save(existingUser);
        })
                .orElseThrow(() -> new EntityNotFoundException("User with id:" + updateUserDTO.getUserId() + " was not found!"));
    }
/*
username
password
dateOfBirth
email
firstName
lastName
adress_street
adress_postalCode
adress_city
 */


    // delete user account
    public ResponseEntity<String> deleteUser(DeleteUserDTO deleteUserDTO){
        userRepository.findById(deleteUserDTO.getUserId())
                .orElseThrow(()-> new RuntimeException("User does not exist"));
        userRepository.deleteById(deleteUserDTO.getUserId());
        return ResponseEntity.ok("User deleted");
    }
}
