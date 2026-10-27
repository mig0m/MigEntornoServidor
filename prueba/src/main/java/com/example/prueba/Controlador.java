package com.example.prueba;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Controlador {
    @GetMapping("/recibir")
    @ResponseBody
    public String recibirGet(@RequestParam(defaultValue = "") String texto) {
        return mostrarResultado("GET", texto);
    }

    @PostMapping("/recibir")
    @ResponseBody
    public String recibirPost(@RequestParam(defaultValue = "") String texto) {
        return mostrarResultado("POST", texto);
    }

    private String mostrarResultado(String metodo, String texto) {
        String codificado = URLEncoder.encode(texto, StandardCharsets.UTF_8);
        String enlace = "/recibir?texto=" + codificado;
        String textoSeguro = texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
        return "<!DOCTYPE html>" + "<html lang='es'>" + "<head><meta charset='UTF-8'><title>Resultado</title></head>" + "<body>" + "<h1>Resultado de " + metodo + "</h1>" + "<p>Texto recibido: " + textoSeguro + "</p>" + "<p><a href='" + enlace + "'>" + "Volver a enviar el texto mediante GET</a></p>" + "<p><a href='/'>Volver al formulario</a></p>" + "</body></html>";
    }
}