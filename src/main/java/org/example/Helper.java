package org.example;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.List;

public class Helper {
    private String user;
    private static final int movieDurability = 3;
    private long days;
    private static Helper helper;
    private List<Pair> movies;
    private HashMap<String,String> map;
    private JFrame frame;
    private JTextArea textArea;
public static Helper getInstance(){
    if(helper==null){
        helper = new Helper();
        return helper;
    }
    return helper;
}
public void setWindow(JFrame frame){
    this.frame = frame;
}

    private Helper(){
    Calendar temp = Calendar.getInstance();
    movies = new ArrayList<>();
    File file  = new File("movieSchedule.txt");
    if(!file.exists())
        file  = new File("src/main/java/org/example/movieSchedule.txt");
    BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String[] arr = line.split(" ");
                StringBuilder name = new StringBuilder();
                for(int i =0;;i++){
                    name.append(arr[i]);
                    if(name.charAt(name.length()-1)=='\"')
                        break;
                    name.append(" ");
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.parseInt(arr[arr.length-5]), Integer.parseInt(arr[arr.length-4])-1, Integer.parseInt(arr[arr.length-3]),
                        Integer.parseInt(arr[arr.length-2]), Integer.parseInt(arr[arr.length-1]), 0);
                movies.add(new Pair(name.toString(),calendar));
                line = reader.readLine();
                if(calendar.getTime().after(temp.getTime()))
                    temp = (Calendar) calendar.clone();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        days = (temp.getTime().getTime() - Calendar.getInstance().getTime().getTime())/86400000;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }
    public void createPanel(){

        JPanel panel2 = new JPanel();
        JLabel label2 = new JLabel("Виберіть календар");
        final JComboBox<String> cb = new JComboBox<String>();
        for(Map.Entry e: map.entrySet())
            cb.addItem((String) e.getKey());
        JButton chose = new JButton("Chose");

        panel2.add(label2);
        panel2.add(cb);
        panel2.add(chose);
        panel2.setVisible(true);
        panel2.setBounds(0,60,1000,40);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBounds(5,90,995,600);
        frame.getContentPane().add(panel2);
        frame.getContentPane().add(textArea);
        frame.revalidate();

        chose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    textArea.selectAll();
                    textArea.replaceSelection("\nФільми на які ви можете піти");
                    giveMovies(map.get((String)cb.getSelectedItem()));

                } catch (GeneralSecurityException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }
    public void setUser(String text) {
    this.user = text;
    }
    public void giveMovies(String s) throws GeneralSecurityException, IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        for(int i =0; i<days;i++){
            List<Event> list = CalendarQuickstart.getEventsDay(user,s,calendar);
            if (list.isEmpty()) {
                addMovieByCalendar(calendar);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                continue;
                } else {
                for (int j = 0;j< list.size();j++) {
                    DateTime start = list.get(j).getStart().getDateTime();
                    DateTime end = list.get(j).getEnd().getDateTime();
                    Calendar fStart;
                    Calendar fEnd;
                    if (start == null) {
                       continue;
                    }
                    if(j==0){
                        fStart = (Calendar) calendar.clone();
                        fEnd = toCalendar(start.toString());
                        addMovieByTwoCalendars(fStart, fEnd);
                    }
                    if(j == list.size()-1){
                        Calendar temp;
                        Event event =  CalendarQuickstart.getFirstEvent(user,s,calendar);
                        if(event!=null){
                            if(event.getStart().getDateTime()!=null)
                                temp = toCalendar(event.getStart().getDateTime().toString());
                            else {
                                temp = (Calendar) calendar.clone();
                                temp.add(Calendar.HOUR_OF_DAY, 28);
                            }
                        }else {
                            temp = (Calendar) calendar.clone();
                            temp.add(Calendar.HOUR_OF_DAY, 28);
                        }
                        fStart = toCalendar(end.toString());
                        addMovieByTwoCalendars(fStart, temp);
                        continue;
                    }

                    DateTime startNext = list.get(j+1).getStart().getDateTime();
                    addMovieByTwoCalendars(toCalendar(start.toString()),toCalendar(startNext.toString()));

                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
    private Calendar toCalendar(String s){
        String[] arr = s.split("[T\\-.:]");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt( arr[0]),Integer.parseInt( arr[1])-1,Integer.parseInt( arr[2]), Integer.parseInt( arr[3]),
                Integer.parseInt( arr[4]),Integer.parseInt( arr[5]));
        return calendar;
    }
    private void addMovieByCalendar(Calendar calendar){
        for (Pair p:
             movies) {
            if(p.getValue().get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
                    && p.getValue().get(Calendar.MONTH) == calendar.get(Calendar.MONTH)){
                textArea.append("\n"+p.getKey()+" "+ p.getValue().getTime());
            }
        }
    }
    private void addMovieByTwoCalendars(Calendar start,Calendar end){
        for (Pair p:
                movies) {
            Calendar c1 = (Calendar) start.clone();
            Calendar c2 = (Calendar) end.clone();
            c1.add(Calendar.HOUR_OF_DAY, movieDurability);
            if(c1.getTime().before(c2.getTime()) && start.get(Calendar.DAY_OF_MONTH)==p.getValue().get(Calendar.DAY_OF_MONTH)
                    && start.get(Calendar.MONTH)==p.getValue().get(Calendar.MONTH)
                    && p.getValue().getTime().after(start.getTime())){
                Calendar temp = (Calendar) p.getValue().clone();
                temp.add(Calendar.HOUR_OF_DAY, movieDurability);
                if(temp.getTime().before(end.getTime())) {
                    textArea.append("\n"+p.getKey() + " " + p.getValue().getTime());
                }
            }
        }
    }
}
