package org.goya.daw2.dwes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Controlador {
    private static final String ATRIBUTO_USUARIO = "usuarioAutenticado";

    private final UsuarioService usuarioService;
    public Controlador(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String inicio(HttpSession session, Model model) {
        return mostrarPaginaProtegida("inicio", session, model);
    }
    @GetMapping({"/pagina1", "/página1"})
    public String pagina1(HttpSession session, Model model) {
        return mostrarPaginaProtegida("página 1", session, model);
    }
    @GetMapping({"/pagina2", "/página2"})
    public String pagina2(HttpSession session, Model model) {
        return mostrarPaginaProtegida("página 2", session, model);
    }
    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        return estaAutenticado(session) ? "redirect:/" : "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario, @RequestParam String password, HttpServletRequest request, Model model) {
        HttpSession sesionActual = request.getSession(false);
        if (estaAutenticado(sesionActual)) {
            return "redirect:/";
        }
        if (usuarioService.autenticar(usuario.trim(), password)) {
            HttpSession nuevaSesion = request.getSession(true);
            nuevaSesion.setAttribute(ATRIBUTO_USUARIO, usuario.trim());
            return "redirect:/";
        }
        model.addAttribute("error", "Usuario o contraseña incorrectos.");
        model.addAttribute("usuario", usuario);
        return "login";
    }
    @PostMapping("/logout")
    public String cerrarSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    private String mostrarPaginaProtegida(String nombrePagina, HttpSession session, Model model) {
        if (!estaAutenticado(session)) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", session.getAttribute(ATRIBUTO_USUARIO));
        model.addAttribute("pagina", nombrePagina);
        return "pagina";
    }
    private boolean estaAutenticado(HttpSession session) {
        return session != null && session.getAttribute(ATRIBUTO_USUARIO) != null;
    }
}