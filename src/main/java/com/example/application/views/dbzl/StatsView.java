package com.example.application.views.dbzl;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("DBZL Stats")
@Route(value = "stats", layout = MainLayout.class)
public class StatsView extends HorizontalLayout {

    private ComboBox<String> teamSelector;
    private ComboBox<String> characterSelector;
    private Image characterView;
    private Map<String, Image> characterImages = new HashMap<>();

    public StatsView() {
        addClassName("d-bzl-view");
        teamSelector = new ComboBox<>(16);
        teamSelector.setItems(buildTeamDataSource());
        teamSelector.setLabel("Select your team");
        teamSelector.addValueChangeListener(e ->{
            characterSelector.setItems(buildTeamCharacterDataSource(e.getValue()));
        });
        add(teamSelector);
        characterSelector = new ComboBox<>(30);
        characterSelector.setLabel("Select a character");

        initializeImages();
        characterSelector.addValueChangeListener(e -> {
            remove(characterView);
           characterView = characterImages.get(e.getValue());
           if(null == characterView){
               characterView = characterImages.get("empty-plant");
           }
           add(characterView);
        });
        add(characterSelector);
        characterView = characterImages.get("empty-plant");
        add(characterView);


    }

    private List<String> buildTeamDataSource(){
        return List.of("Androids", "Budokai", "Buu Saga", "Cinema", "Cold", "Derp", "Earth Defenders", "GT", "Hybrids", "Kaiju", "Muscle", "Namek", "Rugrats", "Royals", "Resurrected Warriors", "Sentai Squad");
    }

    private List<String> buildTeamCharacterDataSource(String teamName){
        if(teamName.equalsIgnoreCase("Androids")){
            return List.of("Android_16", "Android_17", "Android_18", "19", "Dr. Gero", "Cell (Imperfect)", "Cell (Semi-Perfect)");
        } else{
            return List.of("Ginyu", "Jeice", "Burter", "Guldo", "Recoome", "Jiren", "Saiyaman", "saiyawoman", "Salza");
        }
    }

    private void initializeImages() {
        File imageDirectory = Paths.get("src", "main", "resources", "images").toFile();
        System.out.println("Images directory: " + imageDirectory.getAbsolutePath());
        File[] images = imageDirectory.listFiles();
        System.out.println("building image cache");
        for (File image : images) {
            String fileName = image.getName().split("\\.")[0];
            System.out.println(fileName);
            Image characterView = new Image(new StreamResource(fileName, () -> {
                try {
                    return new FileInputStream(image);
                } catch (FileNotFoundException e) {
                    // file not found
                    e.printStackTrace();
                }
                return null;

            }), image.getName());
            characterImages.put(fileName, characterView);
        }
    }
}
