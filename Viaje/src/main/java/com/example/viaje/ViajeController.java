package com.example.viaje;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ViajeController {
    private final List<String> tiposValidos = List.of("Aventura", "Relax", "Cultural");
    private final List<String> destinosValidos = List.of("Playa", "Montaña", "Ciudad");
    private final List<String> presupuestosValidos = List.of("Económico", "Medio", "Alto");
    private final List<String> actividadesValidas = List.of("Senderismo", "Museos", "Gastronomía", "Fotografía");

    @GetMapping("/viaje")
    public String inicio() {
        return "paso1";
    }

    @PostMapping("/viaje/paso2")
    public String paso2(@RequestParam(required = false) String nombre, @RequestParam(required = false) String edad, @RequestParam(required = false) String tipo, Model model) {

        String error = validarPersonales(nombre, edad, tipo);

        if (error != null) {
            model.addAttribute("error", error);
            cargarPersonales(model, nombre, edad, tipo);
            return "paso1";
        }

        cargarPersonales(model, nombre.trim(), edad.trim(), tipo);
        return "paso2";
    }

    @PostMapping("/viaje/paso3")
    public String paso3(@RequestParam(required = false) String nombre, @RequestParam(required = false) String edad, @RequestParam(required = false) String tipo, @RequestParam(required = false) List<String> destinos, @RequestParam(required = false) String presupuesto, Model model) {

        String error = validarPersonales(nombre, edad, tipo);

        if (error == null && !listaValida(destinos, destinosValidos)) {
            error = "Selecciona al menos un destino válido.";
        }

        if (error == null && !presupuestosValidos.contains(presupuesto)) {
            error = "Selecciona un presupuesto válido.";
        }

        if (error != null) {
            model.addAttribute("error", error);
            cargarPaso2(model, nombre, edad, tipo, destinos, presupuesto);
            return "paso2";
        }

        cargarPaso2(model, nombre.trim(), edad.trim(), tipo, destinos, presupuesto);

        return "paso3";
    }

    @PostMapping("/viaje/resumen")
    public String resumen(@RequestParam(required = false) String nombre, @RequestParam(required = false) String edad, @RequestParam(required = false) String tipo, @RequestParam(required = false) List<String> destinos, @RequestParam(required = false) String presupuesto, @RequestParam(required = false) List<String> actividades, @RequestParam(required = false) String comentario, Model model) {

        String error = validarPersonales(nombre, edad, tipo);

        if (error == null && !listaValida(destinos, destinosValidos)) {
            error = "Los destinos no son válidos. Vuelve atrás y corrígelos.";
        }

        if (error == null && !presupuestosValidos.contains(presupuesto)) {
            error = "El presupuesto no es válido. Vuelve atrás y corrígelo.";
        }

        if (error == null && !listaValida(actividades, actividadesValidas)) {
            error = "Selecciona al menos una actividad válida.";
        }

        if (error == null && comentario != null && comentario.length() > 300) {
            error = "El comentario no puede superar los 300 caracteres.";
        }

        if (error != null) {
            model.addAttribute("error", error);
            cargarPaso3(model, nombre, edad, tipo, destinos, presupuesto, actividades, comentario);
            return "paso3";
        }

        List<String> respuestas = new ArrayList<>();

        respuestas.add("Nombre: " + nombre.trim());
        respuestas.add("Edad: " + Integer.parseInt(edad.trim()));
        respuestas.add("Tipo de viaje: " + tipo);
        respuestas.add("Destinos: " + String.join(", ", destinos));
        respuestas.add("Presupuesto: " + presupuesto);
        respuestas.add("Actividades: " + String.join(", ", actividades));

        if (comentario == null || comentario.isBlank()) {
            respuestas.add("Comentario: Sin comentarios");
        } else {
            respuestas.add("Comentario: " + comentario.trim());
        }

        model.addAttribute("respuestas", respuestas);

        return "resumen";
    }

    @PostMapping("/viaje/volver1")
    public String volver1(@RequestParam(required = false) String nombre, @RequestParam(required = false) String edad, @RequestParam(required = false) String tipo, Model model) {

        cargarPersonales(model, nombre, edad, tipo);
        return "paso1";
    }

    @PostMapping("/viaje/volver2")
    public String volver2(@RequestParam(required = false) String nombre, @RequestParam(required = false) String edad, @RequestParam(required = false) String tipo, @RequestParam(required = false) List<String> destinos, @RequestParam(required = false) String presupuesto, Model model) {

        cargarPaso2(model, nombre, edad, tipo, destinos, presupuesto);
        return "paso2";
    }

    private String validarPersonales(String nombre, String edad, String tipo) {

        if (nombre == null || nombre.isBlank()) {
            return "Escribe tu nombre.";
        }

        if (edad == null || !edad.trim().matches("\\d{1,3}")) {
            return "Introduce una edad numérica válida.";
        }

        int numeroEdad = Integer.parseInt(edad.trim());

        if (numeroEdad < 1 || numeroEdad > 120) {
            return "La edad debe estar entre 1 y 120.";
        }

        if (tipo == null || !tiposValidos.contains(tipo)) {
            return "Selecciona un tipo de viaje válido.";
        }

        return null;
    }

    private boolean listaValida(List<String> valores, List<String> opcionesValidas) {

        return valores != null && !valores.isEmpty() && valores.stream().allMatch(valor -> valor != null && opcionesValidas.contains(valor));
    }

    private void cargarPersonales(Model model, String nombre, String edad, String tipo) {

        model.addAttribute("nombre", nombre);
        model.addAttribute("edad", edad);
        model.addAttribute("tipo", tipo);
    }

    private void cargarPaso2(Model model, String nombre, String edad, String tipo, List<String> destinos, String presupuesto) {

        cargarPersonales(model, nombre, edad, tipo);
        model.addAttribute("destinos", destinos);
        model.addAttribute("presupuesto", presupuesto);
    }

    private void cargarPaso3(Model model, String nombre, String edad, String tipo, List<String> destinos, String presupuesto, List<String> actividades, String comentario) {

        cargarPaso2(model, nombre, edad, tipo, destinos, presupuesto);
        model.addAttribute("actividades", actividades);
        model.addAttribute("comentario", comentario);
    }
}
