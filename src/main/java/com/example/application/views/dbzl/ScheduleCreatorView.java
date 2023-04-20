package com.example.application.views.dbzl;

import com.example.application.data.TeamData;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.dnd.GridDragEndEvent;
import com.vaadin.flow.component.grid.dnd.GridDragStartEvent;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.vaadin.flow.server.StreamResource;
import org.dbzl.domain.Division;
import org.dbzl.domain.Match;
import org.dbzl.domain.Team;
import org.dbzl.schedule.ScheduleGenerator;
import org.dbzl.writer.MarkdownWriter;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@PageTitle("DBZL Schedule Creator")
@Route(value = "schedule-creator", layout = MainLayout.class)
public class ScheduleCreatorView extends HorizontalLayout {
    Grid<Team> northKai;
    Grid<Team> eastKai;
    Grid<Team> westKai;
    Grid<Team> southKai;
    Grid<Team> unsortedTeams;

    Button createScheduleButton;
    Button resetButton;
 Anchor downloadLink;
    GridListDataView<Team> unsortedDataView;
    GridListDataView<Team> northKaiDataView;
    GridListDataView<Team> eastKaiDataView;
    GridListDataView<Team> westKaiDataView;
    GridListDataView<Team> southKaiDataView;

    Team draggedItem;

    public ScheduleCreatorView(){

        createDefaultView();

        resetButton = new Button();
        resetButton.setText("Reset Kais");
        resetButton.addClickListener(e -> {
            clearView();
            createDefaultView();
            addAllElementsToView();
        });

        createScheduleButton = new Button();
        createScheduleButton.setText("Create Schedule");


        downloadLink = new Anchor();
        downloadLink.getElement().setAttribute("download", true);
        downloadLink.setHref(createSchedule());

//        Element schedule = new Element("object");
//        schedule.setAttribute("type", "text/plain");

        createScheduleButton.addClickListener(e -> {
            Notification.show("Creating schedule ", 5000, Notification.Position.MIDDLE);
//            schedule.setAttribute("data", createSchedule());
        });

        downloadLink.removeAll();
        downloadLink.add(createScheduleButton);
        addAllElementsToView();
    }

    private StreamResource createSchedule(){
        System.out.println("Building schedule stream resources");
        return new StreamResource("schedule.md", () -> new ByteArrayInputStream(getSeasonSchedule()));
    }

    private Grid<Team> setupGrid(String columnHeader){
        Grid<Team> grid = new Grid<>(Team.class, false);
        grid.addColumn(Team::getName).setHeader(columnHeader);
        grid.setDropMode(GridDropMode.ON_GRID);
        grid.setRowsDraggable(true);
        grid.addDragStartListener(this::handleDragStart);
        grid.addDragEndListener(this::handleDragEnd);
        return grid;
    }

    private void createDefaultView(){
        unsortedTeams = setupGrid("Team Name");
        northKai = setupGrid("North Kai");
        eastKai = setupGrid("East Kai");
        westKai = setupGrid("West Kai");
        southKai = setupGrid("South Kai");

        unsortedDataView= unsortedTeams.setItems(TeamData.buildTeamDataSource());
        northKaiDataView= northKai.setItems(new ArrayList<>());
        eastKaiDataView= eastKai.setItems(new ArrayList<>());
        westKaiDataView = westKai.setItems(new ArrayList<>());
        southKaiDataView= southKai.setItems(new ArrayList<>());

        List<GridListDataView<Team>> dataViewList = List.of(unsortedDataView, northKaiDataView, eastKaiDataView, westKaiDataView, southKaiDataView);

        unsortedTeams.addDropListener(e -> {
           dataViewList.forEach(dataView -> dataView.removeItem(draggedItem));
           unsortedDataView.addItem(draggedItem);
           draggedItem.setDivision(null);
        });
        northKai.addDropListener(e -> {
           dataViewList.forEach(dataView -> {
                   if(dataView.contains(draggedItem)){
                       dataView.removeItem(draggedItem);
                }});
           northKaiDataView.addItem(draggedItem);
           draggedItem.setDivision(Division.NORTH_KAI);
        });
        eastKai.addDropListener(e -> {
           dataViewList.forEach(dataView -> {
                   if(dataView.contains(draggedItem)) {
                       dataView.removeItem(draggedItem);
                   }});
           eastKaiDataView.addItem(draggedItem);
           draggedItem.setDivision(Division.EAST_KAI);
        });
        westKai.addDropListener(e -> {
           dataViewList.forEach(dataView -> {
                   if(dataView.contains(draggedItem)){
                       dataView.removeItem(draggedItem);
                }});
           westKaiDataView.addItem(draggedItem);
           draggedItem.setDivision(Division.WEST_KAI);

        });
        southKai.addDropListener(e -> {
           dataViewList.forEach(dataView -> {
                   if(dataView.contains(draggedItem)){
                       dataView.removeItem(draggedItem);
                }});
            draggedItem.setDivision(Division.SOUTH_KAI);
            southKaiDataView.addItem(draggedItem);
        });


    }

    private void clearView(){
        remove(unsortedTeams);
        remove(northKai);
        remove(eastKai);
        remove(westKai);
        remove(southKai);
        remove(downloadLink);
        remove(resetButton);

    }

    private void addAllElementsToView(){
        add(unsortedTeams);
        add(northKai);
        add(eastKai);
        add(westKai);
        add(southKai);
        add(downloadLink);
        add(resetButton);
    }

    private void handleDragStart(GridDragStartEvent<Team> e) {
        draggedItem = e.getDraggedItems().get(0);
    }

    private void handleDragEnd(GridDragEndEvent<Team> e) {
        draggedItem = null;
    }


    public byte[] getSeasonSchedule(){
        System.out.println("Building schedule");
        ScheduleGenerator generator = new ScheduleGenerator();
        List<Team> allTeams = new ArrayList<>();
        allTeams.addAll(northKaiDataView.getItems().toList());
        allTeams.addAll(eastKaiDataView.getItems().toList());
        allTeams.addAll(westKaiDataView.getItems().toList());
        allTeams.addAll(southKaiDataView.getItems().toList());
        System.out.println("all teams size: " + allTeams.size());
        Map<Integer, List<Match>> schedule = generator.buildSchedule(allTeams);
        MarkdownWriter writer = new MarkdownWriter();
        String teamSchedule = writer.buildScheduleByTeam(allTeams);
        String weeklySchedule = writer.buildScheduleByWeek(schedule);
        String combinedSchedule = teamSchedule + "\n\n\n" + weeklySchedule;
        allTeams.forEach(team -> team.clearSchedule()); //required to clear out before calling the generator a 2nd time
        return combinedSchedule.getBytes();

    }
}
