package com.tutorias.domain.service;

import com.tutorias.domain.dto.CreateUserDTO;
import com.tutorias.domain.dto.EditSubjectUserDTO;
import com.tutorias.domain.model.Subject;
import com.tutorias.domain.model.SubjectUser;
import com.tutorias.domain.model.User;
import com.tutorias.domain.repository.SubjectRepository;
import com.tutorias.domain.repository.SubjectUserRepository;
import com.tutorias.domain.repository.UserRepository;
import com.tutorias.persistance.crud.*;
import com.tutorias.persistance.entity.Materia;
import com.tutorias.persistance.entity.MateriaUsuario;
import com.tutorias.persistance.entity.MateriaUsuarioPK;
import com.tutorias.persistance.entity.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UsuarioCrudRepository usuarioCrudRepository;
    @Autowired
    private RolCrudRepository rolCrudRepository;
    @Autowired
    private CarreraCrudRepository carreraCrudRepository;
    @Autowired
    private MateriaUsuarioCrudRepository materiaUsuarioCrudRepository;
    @Autowired
    private SubjectUserRepository subjectUserRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAll(){
        List<User> users = userRepository.getAll();
        users.forEach(this::calculateTotalSchedule);
        return users;
    }

    public Optional<User> getById(Integer idUser){
        Optional<User> user = userRepository.getById(idUser);
        user.ifPresent(this::calculateTotalSchedule);
        return user;
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

        List<Integer> validMateriaIds = null;
        if (body.getIdSubjects() != null && !body.getIdSubjects().isEmpty()) {
            validMateriaIds = validateAndGetValidMateriaIds(body.getIdSubjects());
        }

        userRepository.updateUser(idUser, body, encryptedPassword);

        if (validMateriaIds != null) {
            updateUserMaterias(idUser, validMateriaIds);
        }
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

    public List<User> profesoresConMateriasDelEstudiante(Integer idEstudiante) {
        List<SubjectUser> materiasEstudiante = subjectUserRepository.findByUsuarioIdUsuario(idEstudiante);

        if (materiasEstudiante.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> idsMaterias = materiasEstudiante.stream()
                .map(SubjectUser::getSubjectId)
                .collect(Collectors.toList());

        return userRepository.findProfesoresByMateriaIds(idsMaterias, 2);
    }

    private List<Integer> validateAndGetValidMateriaIds(List<Integer> materiaIds) {

        List<Integer> cleanIds = materiaIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .collect(Collectors.toList());

        if (cleanIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> existingIds = subjectRepository.getMateriaIdsExisting(cleanIds);

        List<Integer> nonExistingIds = cleanIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toList());

        if (!nonExistingIds.isEmpty()) {
            throw new RuntimeException("Las siguientes materias no existen: " + nonExistingIds);
        }

        return existingIds;
    }

    private void updateUserMaterias(int idUser, List<Integer> materiaIds) {
        List<Integer> currentMateriaIds = subjectUserRepository.getMateriaIdsByUsuarioId(idUser);

        List<Integer> newMateriaIds = materiaIds.stream()
                .filter(id -> !currentMateriaIds.contains(id))
                .collect(Collectors.toList());

        if (!newMateriaIds.isEmpty()) {
            List<MateriaUsuario> newAssignments = new ArrayList<>();

            Usuario usuario = usuarioCrudRepository.findById(idUser)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            for (Integer materiaId : newMateriaIds) {
                Materia materia = materiaCrudRepository.findById(materiaId)
                        .orElseThrow(() -> new RuntimeException("Materia no encontrada: " + materiaId));

                MateriaUsuarioPK pk = MateriaUsuarioPK.builder()
                        .idUsuario(idUser)
                        .idMateria(materiaId)
                        .build();

                MateriaUsuario materiaUsuario = new MateriaUsuario();
                materiaUsuario.setId(pk);
                materiaUsuario.setUsuario(usuario);
                materiaUsuario.setMateria(materia);

                newAssignments.add(materiaUsuario);
            }

            materiaUsuarioCrudRepository.saveAll(newAssignments);
        }
    }

    private void calculateTotalSchedule(User user) {
        if ("PROFESOR".equals(user.getRole().getName().toUpperCase())) {
            user.setTotalSchedule(userRepository.countSchedulesByIdUser(user.getUserId()));
        } else {
            user.setTotalSchedule(0);
        }
    }
}
