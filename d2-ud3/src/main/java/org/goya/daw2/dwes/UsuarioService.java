package org.goya.daw2.dwes;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, String> usuariosValidos = Map.of("admin", passwordEncoder.encode("muySegura"), "alumno", passwordEncoder.encode("alumno"));

    public boolean autenticar(String nombreUsuario, String passwordIntroducida) {
        if (nombreUsuario == null || passwordIntroducida == null) {
            return false;
        }
        String hashGuardado = usuariosValidos.get(nombreUsuario);
        return hashGuardado != null && passwordEncoder.matches(passwordIntroducida, hashGuardado);
    }
}