package org.example;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

/* class to demonstrate use of Calendar events list API */
public class CalendarQuickstart {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * Directory to store authorization tokens for this application.
     */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credit.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */

    private static String user = "user";
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize(user);
        //returns an authorized Credential object.
        return credential;
    }
    public static HashMap<String, String> getList(String user) throws IOException, GeneralSecurityException {
        CalendarQuickstart.user = user;
        HashMap<String, String> map = new HashMap<>();
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        String pageToken = null;
        do {
            CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : items) {
                map.put(calendarListEntry.getSummary(),calendarListEntry.getId());
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        return map;
    }
    public static Events getListOfEvents(String user, String calendarId) throws IOException, GeneralSecurityException {
        CalendarQuickstart.user = user;

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.MONTH, 1);
        DateTime month = new DateTime(calendar.getTime());


        Events events = service.events().list(calendarId)
                .setTimeMax(month)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
        return events;
    }
    public static List<Event> getEventsDay(String user, String calendarId, java.util.Calendar data) throws IOException, GeneralSecurityException{
        CalendarQuickstart.user = user;

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        java.util.Calendar dataNext = (java.util.Calendar) data.clone();
        dataNext.add(java.util.Calendar.DAY_OF_MONTH, 1);
        Events events = service.events().list(calendarId)
                .setTimeMax(new DateTime(dataNext.getTime()))
                .setTimeMin(new DateTime(data.getTime()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        return items;
    }

    public static Event getFirstEvent(String user, String calendarId, java.util.Calendar data) throws IOException, GeneralSecurityException {

        CalendarQuickstart.user = user;

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();

        java.util.Calendar dataNext = (java.util.Calendar) data.clone();
        java.util.Calendar dataNext2 = (java.util.Calendar) data.clone();
        dataNext.add(java.util.Calendar.DAY_OF_MONTH, 1);
        dataNext2.add(java.util.Calendar.DAY_OF_MONTH, 2);
        Events events = service.events().list(calendarId)
                .setTimeMax(new DateTime(dataNext2.getTime()))
                .setTimeMin(new DateTime(dataNext.getTime()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();
        Event e = null;
        if(!items.isEmpty())
            e = items.get(0);
        return e;
    }
}