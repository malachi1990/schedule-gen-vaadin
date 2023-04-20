package com.example.application.data;

import org.dbzl.domain.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamData {

    public static List<Team> buildTeamDataSource(){
        List<Team> teams = new ArrayList<>();
        teams.add(buildTeam("Androids"));
        teams.add(buildTeam("Budokai"));
        teams.add(buildTeam("Buu Saga"));
        teams.add(buildTeam("Cinema"));
        teams.add(buildTeam("Cold"));
        teams.add(buildTeam("Derp"));
        teams.add(buildTeam("Earth Defenders"));
        teams.add(buildTeam("GT"));
        teams.add(buildTeam("Hybrids"));
        teams.add(buildTeam("Kaiju"));
        teams.add(buildTeam("Muscle"));
        teams.add(buildTeam("Namek"));
        teams.add(buildTeam("Rugrats"));
        teams.add(buildTeam("Royals"));
        teams.add(buildTeam("Resurrected Warriors"));
        teams.add(buildTeam("Sentai Squad"));
        return teams;

    }

    public List<String> buildTeamCharacterDataSource(String teamName){
        if(teamName.equalsIgnoreCase("Androids")){
            return List.of("Android_16", "Android_17", "Android_18", "19", "Dr. Gero", "Cell (Imperfect)", "Cell (Semi-Perfect)");
        } else{
            return List.of("Ginyu", "Jeice", "Burter", "Guldo", "Recoome", "Jiren", "Saiyaman", "saiyawoman", "Salza");
        }
    }

    private static Team buildTeam(String teamName){
        return new Team(teamName, null);
    }
}
