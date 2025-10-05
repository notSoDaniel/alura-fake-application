package br.com.alura.AluraFake.infra;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.PasswordGeneration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder; // 1. Injetamos o PasswordEncoder

    public DataSeeder(UserRepository userRepository, CourseRepository courseRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder; // 2. Adicionamos ao construtor
    }

    @Override
    public void run(String... args) {
        if (!"dev".equals(activeProfile)) return;

        if (userRepository.count() == 0) {
            // --- Criação do usuário CAIO (Estudante) ---
            String caioPassword = PasswordGeneration.generatePassword();
            System.out.println(">>> Senha gerada para 'caio@alura.com.br': " + caioPassword);
            User caio = new User("Caio", "caio@alura.com.br", Role.STUDENT, passwordEncoder.encode(caioPassword));

            // --- Criação do usuário PAULO (Instrutor) ---
            String pauloPassword = PasswordGeneration.generatePassword();
            System.out.println(">>> Senha gerada para 'paulo@alura.com.br': " + pauloPassword);
            User paulo = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR, passwordEncoder.encode(pauloPassword));

            // 3. Salvamos os usuários com a senha criptografada
            userRepository.saveAll(Arrays.asList(caio, paulo));
            courseRepository.save(new Course("Java", "Aprenda Java com Alura", paulo));
        }
    }
}