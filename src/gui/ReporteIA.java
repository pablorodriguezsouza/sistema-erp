package gui;

import dominio.*;
import javax.swing.*;
import java.awt.Insets;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ReporteIA extends JDialog {        // JDialog en lugar de JFrame para poder usar setModal(true), permite bloquear la ventana menú mientras esta ventana está abierta
    private JTextArea txtResultado;
    private Sistema sistema;
    private JComboBox<Empleado> comboEmpleado;
    private JComboBox<Area> comboDestino;
    private JLabel labelEstado;

    public ReporteIA(Sistema sistema, JFrame padre) {
        this.sistema = sistema;
        
        setModal(true);
        setTitle("Consultar IA (Gemini)");
        setSize(935, 650);
        setLayout(null);

        comboEmpleado = new JComboBox<>(sistema.getListaEmpleados().toArray(new Empleado[0]));
        comboEmpleado.setBounds(20, 20, 200, 25); add(comboEmpleado);

        comboDestino = new JComboBox<>(sistema.getListaAreas().toArray(new Area[0]));
        comboDestino.setBounds(230, 20, 200, 25); add(comboDestino);

        JButton btnConsultar = new JButton("Consultar Ventajas/Desventajas");
        btnConsultar.setBounds(20, 60, 250, 30);
        btnConsultar.addActionListener(e -> consultaAsync());
        Utilidades.estiloBotonPrincipal(btnConsultar);
        add(btnConsultar);
        
        labelEstado = new JLabel(""); 
        labelEstado.setBounds(280, 60, 100, 30); add(labelEstado);

        txtResultado = new JTextArea();
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);        // corta las líneas en palabras completas
        txtResultado.setFont(UIManager.getFont("Label.font").deriveFont(14f));      // la letra de la app, no la de máquina de escribir
        txtResultado.setMargin(new Insets(10, 12, 10, 12));
        JScrollPane scroll = new JScrollPane(txtResultado);
        scroll.setBounds(20, 100, 880, 480); add(scroll);
        Utilidades.agregarEncabezado(this, "Reporte inteligente (IA)", "Ventajas y desventajas de mover a un empleado de área, según su CV (Google Gemini)");
        setLocationRelativeTo(padre); // centrar respecto al padre
        Utilidades.establecerIcono(this);
    }

    private void consultaAsync() {
        labelEstado.setText("Consultando...");
        
        new Thread(() -> {
            try {
                Empleado emp = (Empleado) comboEmpleado.getSelectedItem();
                Area area = (Area) comboDestino.getSelectedItem();
                String prompt = "Analiza el movimiento del empleado " + emp.getNombre() + 
                                " con CV: " + emp.getCvTexto() + 
                                " hacia el área " + area.getNombre() + 
                                " (" + area.getDescripcion() + "). Lista 3 ventajas y 3 desventajas." +
                                "Además, genera una breve recomendación para la empresa";
                
                String respuesta = llamarGeminiAPI(prompt);
                
                // Actualizar UI
                SwingUtilities.invokeLater(() -> {
                    txtResultado.setText(respuesta);
                    labelEstado.setText("Listo");
                });
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    txtResultado.setText("Error: " + ex.getMessage());
                    labelEstado.setText("Error");
                });
            }
        }).start();
    }

   private String llamarGeminiAPI(String prompt) throws Exception {
        String apiKey = System.getenv("ERP_API_KEY");
        if (apiKey == null) return "Falta Variable de Entorno ERP_API_KEY";
           
        // Usamos el modelo 2.5 que es el que pide la letra
        String urlStr = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;
        URL url = new URL(urlStr);
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);

        JsonArray partsArray = new JsonArray();
        partsArray.add(textPart);

        JsonObject contentObj = new JsonObject();
        contentObj.add("parts", partsArray);

        JsonArray contentsArray = new JsonArray();
        contentsArray.add(contentObj);

        JsonObject rootObj = new JsonObject();
        rootObj.add("contents", contentsArray);

        String jsonInputString = rootObj.toString();
        // --------------------------------------------------

        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }


        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return "Error de la API (" + responseCode + "): " + sb.toString();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        
        return parsearRespuestaGSON(response.toString());
    }
    
    private String parsearRespuestaGSON(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

            JsonArray candidates = jsonObject.getAsJsonArray("candidates");

            if (candidates != null && candidates.size() > 0) {
                JsonObject primerCandidato = candidates.get(0).getAsJsonObject();
                JsonObject content = primerCandidato.getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");

                if (parts != null && parts.size() > 0) {
                    String textoFinal = parts.get(0).getAsJsonObject().get("text").getAsString();

                    textoFinal = textoFinal.replace("**", "").replace("##", "");

                    return textoFinal;
                }
            }
            return "La IA no devolvió texto válido.";

        } catch (Exception ex) {
            return "Error parseando GSON: " + ex.getMessage();
        }
    }
}