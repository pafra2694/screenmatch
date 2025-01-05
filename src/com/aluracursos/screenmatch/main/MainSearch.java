package com.aluracursos.screenmatch.main;
import com.aluracursos.screenmatch.modelos.Titulo;
import com.aluracursos.screenmatch.modelos.TituloOmdb;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

public class MainSearch {

    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner sc = new Scanner(System.in);
        List<Titulo> titulos = new ArrayList<>();
        Gson gson =  new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();

        while(true){
            System.out.print("Escriba el nombre de una película: ");
            var busqueda = sc.nextLine();
            if(busqueda.equalsIgnoreCase("salir")){
                break;
            }

            String busquedaCodificada = URLEncoder.encode(busqueda, StandardCharsets.UTF_8.toString());

            String direccion = "https://www.omdbapi.com/?apikey=b7906e10&t=" + busquedaCodificada;


            try {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(direccion))
                        .build();

                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());

                String json = response.body();
                System.out.println(json);

                TituloOmdb miTituloOmdb = gson.fromJson(json, TituloOmdb.class);
                System.out.println(miTituloOmdb);

                Titulo miTitulo = new Titulo(miTituloOmdb);
                System.out.println(miTitulo);

                FileWriter escritura = new FileWriter("peliculas.txt");
                titulos.add(miTitulo);

            } catch (NumberFormatException e) {
                System.out.print("Ocurrio un error: ");
                System.out.println(e.getMessage());
            } catch (IllegalArgumentException e){
                System.out.println("Error en la URI, verifique la dirección");
            } catch (Exception e){
                System.out.println("Ocurrio un error inesperado: " + e.getMessage());
            }
        }

        System.out.println(titulos);
        FileWriter escritura = new FileWriter("titulos.json");
        escritura.write(gson.toJson(titulos));
        escritura.close();
    }
}
