package org.example.viaje;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/viaje-cookies")
public class Controlador3 {
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<String> tiposValidos = List.of("Aventura", "Relax", "Cultural");
    private final List<String> destinosValidos = List.of("Playa", "Montaña", "Ciudad");
    private final List<String> presupuestosValidos = List.of("Económico", "Medio", "Alto");
    private final List<String> actividadesValidas = List.of("Senderismo", "Museos", "Gastronomía", "Fotografía");

    @GetMapping
    public String inicio(HttpServletResponse response) {
        borrarCookie(response);
        return "redirect:/viaje-cookies/paso1";
    }

    @GetMapping("/paso1")
    public String mostrarPaso1() {
        return "paso1-cookies";
    }

    @PostMapping("/paso2")
    public String paso2(@RequestParam(required = false) String nombre, @RequestParam(required = false) String edad, @RequestParam(required = false) String tipo, HttpServletResponse response, Model modelo) {
        ViajeDatos viaje = new ViajeDatos();
        viaje.setNombre(nombre);
        viaje.setEdad(edad);
        viaje.setTipo(tipo);
        String error = validarDatosPersonales(nombre, edad, tipo);
        if (error != null) {
            modelo.addAttribute("error", error);
            cargarDatosPersonales(modelo, viaje);
            return "paso1-cookies";
        }
        viaje.setNombre(nombre.trim());
        viaje.setEdad(edad.trim());
        guardarCookie(response, viaje);
        cargarDatosPersonales(modelo, viaje);
        return "paso2-cookies";
    }

    @PostMapping("/paso3")
    public String paso3(@CookieValue(name = "viaje", required = false) String valorCookie, @RequestParam(required = false) List<String> destinos, @RequestParam(required = false) String presupuesto, HttpServletResponse response, Model modelo) {
        ViajeDatos viaje = recuperarCookie(valorCookie);
        if (viaje == null) {
            return "redirect:/viaje-cookies";
        }
        viaje.setDestinos(destinos);
        viaje.setPresupuesto(presupuesto);
        String error = validarDatosPersonales(viaje.getNombre(), viaje.getEdad(), viaje.getTipo());
        if (error == null && !listaValida(destinos, destinosValidos)) {
            error = "Selecciona al menos un destino válido";
        }
        if (error == null && (presupuesto == null || !presupuestosValidos.contains(presupuesto))) {
            error = "Selecciona un presupuesto válido";
        }
        if (error != null) {
            guardarCookie(response, viaje);
            modelo.addAttribute("error", error);
            cargarPaso2(modelo, viaje);
            return "paso2-cookies";
        }
        guardarCookie(response, viaje);
        cargarPaso2(modelo, viaje);
        return "paso3-cookies";
    }

    @PostMapping("/resumen")
    public String resumen(@CookieValue(name = "viaje", required = false) String valorCookie, @RequestParam(required = false) List<String> actividades, @RequestParam(required = false) String comentario, HttpServletResponse response, Model modelo) {
        ViajeDatos viaje = recuperarCookie(valorCookie);
        if (viaje == null) {
            return "redirect:/viaje-cookies";
        }
        viaje.setActividades(actividades);
        viaje.setComentario(comentario);
        String error = validarDatosPersonales(viaje.getNombre(), viaje.getEdad(), viaje.getTipo());
        if (error == null && !listaValida(viaje.getDestinos(), destinosValidos)) {
            error = "Los destinos no son válidos";
        }
        if (error == null && (viaje.getPresupuesto() == null || !presupuestosValidos.contains(viaje.getPresupuesto()))) {
            error = "El presupuesto no es válido";
        }
        if (error == null && !listaValida(actividades, actividadesValidas)) {
            error = "Selecciona al menos una actividad válida";
        }
        if (error == null && comentario != null && comentario.length() > 300) {
            error = "El comentario no puede superar los 300 caracteres";
        }
        if (error != null) {
            guardarCookie(response, viaje);
            modelo.addAttribute("error", error);
            cargarPaso3(modelo, viaje);
            return "paso3-cookies";
        }
        List<String> respuestas = new ArrayList<>();
        respuestas.add("Nombre: " + viaje.getNombre());
        respuestas.add("Edad: " + viaje.getEdad());
        respuestas.add("Tipo de viaje: " + viaje.getTipo());
        respuestas.add("Destinos: " + String.join(", ", viaje.getDestinos()));
        respuestas.add("Presupuesto: " + viaje.getPresupuesto());
        respuestas.add("Actividades: " + String.join(", ", viaje.getActividades()));
        if (comentario == null || comentario.isBlank()) {
            respuestas.add("Comentario: Sin comentarios");
        }
        else {
            respuestas.add("Comentario: " + comentario.trim());
        }
        modelo.addAttribute("respuestas", respuestas);
        borrarCookie(response);
        return "resumen";
    }

    @PostMapping("/volver1")
    public String volver1(@CookieValue(name = "viaje", required = false) String valorCookie, Model modelo) {
        ViajeDatos viaje = recuperarCookie(valorCookie);
        if (viaje == null) {
            return "redirect:/viaje-cookies";
        }
        cargarDatosPersonales(modelo, viaje);
        return "paso1-cookies";
    }

    @PostMapping("/volver2")
    public String volver2(@CookieValue(name = "viaje", required = false) String valorCookie, Model modelo) {
        ViajeDatos viaje = recuperarCookie(valorCookie);
        if (viaje == null) {
            return "redirect:/viaje-cookies";
        }
        cargarPaso2(modelo, viaje);
        return "paso2-cookies";
    }

    private void guardarCookie(HttpServletResponse response, ViajeDatos viaje) {
        try {
            String json = mapper.writeValueAsString(viaje);
            String valor = Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
            Cookie cookie = new Cookie("viaje", URLEncoder.encode(valor, StandardCharsets.UTF_8));
            cookie.setPath("/viaje-cookies");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(1800);
            response.addCookie(cookie);
        }
        catch (JacksonException e) {
            throw new IllegalStateException("No se pudo guardar el cuestionario", e);
        }
    }
    private ViajeDatos recuperarCookie(String valorCookie) {
        if (valorCookie == null || valorCookie.isBlank()) {
            return null;
        }
        try {
            String valor = URLDecoder.decode(valorCookie, StandardCharsets.UTF_8);
            byte[] datos = Base64.getUrlDecoder().decode(valor);
            String json = new String(datos, StandardCharsets.UTF_8);
            return mapper.readValue(json, ViajeDatos.class);
        }
        catch (JacksonException | IllegalArgumentException e) {
            return null;
        }
    }

    private void borrarCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("viaje", "");
        cookie.setPath("/viaje-cookies");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private String validarDatosPersonales(String nombre, String edad, String tipo) {
        if (nombre == null || nombre.isBlank()) {
            return "Escribe tu nombre";
        }
        if (edad == null || !edad.trim().matches("\\d{1,3}")) {
            return "Introduce una edad válida";
        }
        int numeroEdad = Integer.parseInt(edad.trim());
        if (numeroEdad < 1 || numeroEdad > 120) {
            return "La edad debe estar entre 1 y 120";
        }
        if (tipo == null || !tiposValidos.contains(tipo)) {
            return "Selecciona un tipo de viaje válido";
        }
        return null;
    }
    private boolean listaValida(List<String> lista, List<String> valoresValidos) {
        return lista != null && !lista.isEmpty() && lista.stream().allMatch(valor -> valor != null && valoresValidos.contains(valor));
    }

    private void cargarDatosPersonales(Model modelo, ViajeDatos viaje) {
        modelo.addAttribute("nombre", viaje.getNombre());
        modelo.addAttribute("edad", viaje.getEdad());
        modelo.addAttribute("tipo", viaje.getTipo());
    }
    private void cargarPaso2(Model modelo, ViajeDatos viaje) {
        cargarDatosPersonales(modelo, viaje);
        modelo.addAttribute("destinos", viaje.getDestinos());
        modelo.addAttribute("presupuesto", viaje.getPresupuesto());
    }
    private void cargarPaso3(Model modelo, ViajeDatos viaje) {
        cargarPaso2(modelo, viaje);
        modelo.addAttribute("actividades", viaje.getActividades());
        modelo.addAttribute("comentario", viaje.getComentario());
    }
}