package com.tutorias.domain.service;

import com.tutorias.domain.dto.CreateUserDTO;
import com.tutorias.domain.dto.EditSubjectUserDTO;
import com.tutorias.domain.model.User;
import com.tutorias.domain.repository.UserRepository;
import com.tutorias.persistance.crud.CarreraCrudRepository;
import com.tutorias.persistance.crud.MateriaCrudRepository;
import com.tutorias.persistance.crud.MateriaUsuarioCrudRepository;
import com.tutorias.persistance.crud.RolCrudRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolCrudRepository rolCrudRepository;
    @Autowired
    private CarreraCrudRepository carreraCrudRepository;
    @Autowired
    private MateriaUsuarioCrudRepository materiaUsuarioCrudRepository;
    @Autowired
    private MateriaCrudRepository materiaCrudRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAll(){ return userRepository.getAll(); }

    public Optional<User> getById(Integer idUser){
        return userRepository.getById(idUser);
    }

    public void updateUser(int idUser, CreateUserDTO body) {
        if (!userRepository.existsByIdUser(idUser)) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (body.getRoleID() != null) {
            if (!rolCrudRepository.existsById(body.getRoleID())) {
                throw new RuntimeException("Rol no encontrado");
            }
        }

        if (body.getCareerID() != null) {
            if (!carreraCrudRepository.existsById(body.getCareerID())) {
                throw new RuntimeException("Carrera no encontrada");
            }
        }

        if (body.getUser() != null && !body.getUser().trim().isEmpty()) {
            String newUsername = body.getUser().trim();
            if (userRepository.existsByUsernameAndNotId(newUsername, idUser)) {
                throw new IllegalArgumentException("El nombre de usuario '" + newUsername + "' ya está en uso");
            }
        }

        if (body.getEmail() != null && !body.getEmail().trim().isEmpty()) {
            String newEmail = body.getEmail().trim().toLowerCase();

            if (!isValidEmail(newEmail)) {
                throw new IllegalArgumentException("El formato del email no es válido");
            }

            if (userRepository.existsByEmailAndNotId(newEmail, idUser)) {
                throw new IllegalArgumentException("El email '" + newEmail + "' ya está en uso");
            }
        }

        String encryptedPassword = null;
        if (body.getPassword() != null && !body.getPassword().trim().isEmpty()){
            String newPassword = body.getPassword().trim();

            if (newPassword.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            }

            encryptedPassword = passwordEncoder.encode(newPassword);
        }

        userRepository.updateUser(idUser, body, encryptedPassword);
    }

    public void deleteUserSubjects(EditSubjectUserDTO body) {

        if (!userRepository.existsByIdUser(body.getIdUser())) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (body.getIdSubjects() == null || body.getIdSubjects().isEmpty()) {
            throw new RuntimeException("La lista de materias no puede estar vacía");
        }

        List<Integer> materiasActuales = userRepository.getSubjectsByIdUser(body.getIdUser());

        List<Integer> materiasNoEncontradas = body.getIdSubjects().stream()
                .filter(idMateria -> !materiasActuales.contains(idMateria))
                .collect(Collectors.toList());

        if (!materiasNoEncontradas.isEmpty()) {
            throw new RuntimeException("El usuario no tiene registradas las siguientes materias: " + materiasNoEncontradas);
        }

        try {
            userRepository.deleteUserSubjects(body);

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar materias del usuario: " + e.getMessage());
        }
    }

    public void deleteUser(Integer idUser){
        if (!userRepository.existsByIdUser(idUser)){
            throw  new RuntimeException("Usuario no encontrado");
        }
        try{
            materiaUsuarioCrudRepository.deleteByIdUsuario(idUser);
            userRepository.deleteUser(idUser);
        }catch (Exception e){
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        String unipamplonaEmailRegex = "^[a-zA-Z0-9._%+-]+@unipamplona\\.edu\\.co$";
        return email.matches(unipamplonaEmailRegex);
    }
}
