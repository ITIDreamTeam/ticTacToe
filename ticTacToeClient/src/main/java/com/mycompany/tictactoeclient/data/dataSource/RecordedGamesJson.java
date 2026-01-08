/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.data.dataSource;

import com.mycompany.tictactoeclient.data.models.RecordedGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Nadin
 */
public class RecordedGamesJson {

    private static final String FILE_PATH = "recorded_games.json";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context)
                    -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, typeOfT, context)
                    -> LocalDateTime.parse(json.getAsString()))
            .setPrettyPrinting()
            .create();

    private RecordedGamesJson() {
    }

    public static List<RecordedGame> loadGames() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<RecordedGame>>() {
            }.getType();
            List<RecordedGame> games = gson.fromJson(reader, listType);

            return games != null ? games : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveGame(RecordedGame game) {
        List<RecordedGame> games = loadGames();
        games.add(game);

        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(games, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
