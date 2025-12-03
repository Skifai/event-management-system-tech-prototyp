package ch.flossrennen.eventmanagementsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Konfiguration für die Applikation.
 * <p>
 * Sicherheitseinstellungen:
 * <ul>
 * <li>Alle HTTP-Anfragen sind erlaubt (permitAll) - geeignet für Prototyp/Entwicklung</li>
 * <li>CSRF-Schutz ist deaktiviert für einfacheren Vaadin-Betrieb</li>
 * <li>BCrypt Password Encoder für sichere Passwort-Verschlüsselung</li>
 * </ul>
 * <p>
 * WICHTIG: Für Produktionsumgebungen sollte eine richtige Authentifizierung
 * und Autorisierung implementiert werden.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Konfiguriert die Security Filter Chain.
     * <p>
     * Aktuell: Alle Requests sind ohne Authentifizierung erlaubt.
     * CSRF ist deaktiviert, da Vaadin eigene CSRF-Protection mitbringt.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // Alle Anfragen erlaubt
            )
            .csrf(csrf -> csrf.disable());  // CSRF deaktiviert für Vaadin

        return http.build();
    }

    /**
     * Stellt einen BCrypt Password Encoder für sichere Passwort-Verschlüsselung bereit.
     * BCrypt ist ein adaptiver Hash-Algorithmus mit integriertem Salt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
