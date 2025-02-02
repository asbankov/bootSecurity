package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Transactional
@Service
public class UserServiceImpl implements UserDetailsService {

    //private UserDao userDao;

    private BCryptPasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public void add(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));;
        userRepository.save(user);
    }

    public User getByID(long id) {
        return userRepository.getReferenceById(id);
    }

    public void edit(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));;
        userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public void addFirst(User user, Role role) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));;
        userRepository.save(user);
        roleRepository.save(role);
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = new User();
        user.setUsername("user");
        user.setPassword("user");
        Role userRole = new Role();
        userRole.setRole("ROLE_USER");
        Set<User> users = new HashSet<> ();
        users.add(user);
        userRole.setUsers(users);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        if (userRepository.findByUsername("user") == null) {
            addFirst(user, userRole);
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        Role adminRole = new Role();
        adminRole.setRole("ROLE_ADMIN");
        users = new HashSet<> ();
        users.add(admin);
        adminRole.setUsers(users);
        roles = new HashSet<>();
        roles.add(adminRole);
        admin.setRoles(roles);
        if (userRepository.findByUsername("admin") == null) {
            addFirst(admin, adminRole);
        }

        user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
