package org.example.viaje;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class Controlador {
    private final List<String> tiposValidos = List.of("Aventura", "Relax", "Cultural");
    private final List<String> destinosValidos = List.of("Playa", "Montaña", "Ciudad");
    private final List<String> presupuestosValidos = List.of("Económico", "Medio", "Alto");
    private final List<String> actividadesValidas = List.of("Senderismo", "Museos", "Gastronomía", "Fotografía");

    @GetMapping("/viaje")
    public String inicio(HttpSession session) {
        session.removeAttribute("viaje");
        return "redirect:/viaje/paso1";
    }

    @GetMapping("/viaje/paso1")
    public String mostrarPaso1() {
        return "paso1";
    }

    @PostMapping("/viaje/paso2")
    public String paso2(@RequestParam(required = false) String nombre, @RequestParam(required = false) String edad, @RequestParam(required = false) String tipo, HttpSession session, Model modelo) {
        ViajeDatos viaje = new ViajeDatos();
        viaje.setNombre(nombre);
        viaje.setEdad(edad);
        viaje.setTipo(tipo);
        String error = validarDatosPersonales(nombre, edad, tipo);
        if (error != null) {
            modelo.addAttribute("error", error);
            cargarDatosPersonales(modelo, viaje);
            return "paso1";
        }
        // Solo se guarda en la sesión si los datos son válidos.
        viaje.setNombre(nombre.trim());
        viaje.setEdad(edad.trim());
        session.setAttribute("viaje", viaje);
        cargarDatosPersonales(modelo, viaje);
        return "paso2";
    }

    @PostMapping("/viaje/paso3")
    public String paso3(@RequestParam(required = false) List<String> destinos, @RequestParam(required = false) String presupuesto, HttpSession session, Model modelo) {
        ViajeDatos viaje = obtenerViaje(session);
        if (viaje == null) {
            return "redirect:/viaje";
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
            modelo.addAttribute("error", error);
            cargarPaso2(modelo, viaje);
            return "paso2";
        }
        cargarPaso2(modelo, viaje);
        return "paso3";
    }

    @PostMapping("/viaje/resumen")
    public String resumen(@RequestParam(required = false) List<String> actividades, @RequestParam(required = false) String comentario, HttpSession session, Model modelo) {
        ViajeDatos viaje = obtenerViaje(session);
        if (viaje == null) {
            return "redirect:/viaje";
        }

        viaje.setActividades(actividades);
        viaje.setComentario(comentario);

        String error = validarDatosPersonales(viaje.getNombre(), viaje.getEdad(), viaje.getTipo());
        if (error == null && !listaValida(viaje.getDestinos(), destinosValidos)) {
            error = "Los destinos no son válidos";
        }
        if (error == null && (viaje.getPresupuesto() == null || !presupuestosValidos.contains(viaje.getPresupuesto()))) {
            error = "Selecciona un presupuesto válido";
        }
        if (error == null && !listaValida(actividades, actividadesValidas)) {
            error = "Selecciona al menos una actividad válida";
        }
        if (error == null && comentario != null && comentario.length() > 300) {
            error = "El comentario no puede superar los 300 caracteres";
        }
        if (error != null) {
            modelo.addAttribute("error", error);
            cargarPaso3(modelo, viaje);
            return "paso3";
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
        return "resumen";
    }

    @PostMapping("/viaje/volver1")
    public String volver1(HttpSession session, Model modelo) {
        ViajeDatos viaje = obtenerViaje(session);
        if (viaje == null) {
            return "redirect:/viaje";
        }
        cargarDatosPersonales(modelo, viaje);
        return "paso1";
    }

    @PostMapping("/viaje/volver2")
    public String volver2(HttpSession session, Model modelo) {
        ViajeDatos viaje = obtenerViaje(session);
        if (viaje == null) {
            return "redirect:/viaje";
        }
        cargarPaso2(modelo, viaje);
        return "paso2";
    }

    private ViajeDatos obtenerViaje(HttpSession session) {
        return (ViajeDatos) session.getAttribute("viaje");
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