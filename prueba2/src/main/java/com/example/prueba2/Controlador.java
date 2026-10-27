package com.example.prueba2;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Controlador {
    @GetMapping("/saludo")
    public String saludo(@RequestParam(defaultValue = "Mundo") String nombre, Model modelo) {
        modelo.addAttribute("nombre", nombre);
        return "saludo";
    }
}
