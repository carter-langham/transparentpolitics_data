package ellis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONObject;
import tech.tablesaw.api.StringColumn;
import org.json.*;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.*;
import tech.tablesaw.io.csv.CsvWriteOptions;

import javax.swing.plaf.nimbus.State;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main
{


    public static final String[] POLITICAL_PARTIES = {"Democrat", "Republican", "Independent", "Green Party",
            "Libertarian", "Nonpartisan"};

    public static final String[] HEADERS = {"Date Last Checked", "Bill Status", "Case ID",	"Issue", "Bill Name",
            "Location", "Bill ID", "Session ID"};

    private static final String[] BILL_STATUS = {"Introduced", "In Committee", "Crossed Over", "Passed",
            "Signed/Enacted", "Vetoed", "Veto Overridden", "Dead", "Engrossed", "Unknown"};

    private static final String[] ISSUES_LIST = {"Accurate IDs","Civil Rights", "Free Speech & Expression", "Healthcare",
            "Public Accommodations", "Schools & Education", "Other"};


    private static final String[] STATES = {"Alabama", " Alaska", "Arizona", "Arkansas", "California", "Colorado",
            "Connecticut", "Delaware", "Florida" , "Georgia", "Hawaii", "Idaho", "Illinois" , "Indiana", "Iowa",
            "Kansas", "Kentucky" , "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
            "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
            "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island",
            "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
            "West Virginia", "Wisconsin", "Wyoming"};



    private static final String[] STATES_ABBREV = {"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI",
            "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH",
            "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA",
            "WV", "WI", "WY"};



    private static Scanner scnr = new Scanner(System.in);

    public static List<String[]> allData = new ArrayList<>();

    public static HashMap<Integer, HashMap<Integer, List<Bill>>> stateSessionBillsMap = new HashMap<>();

    private static ArrayList<String> emptyCases = new ArrayList<>();

    public static final String FILE_DELIMITER = "~~~~";

    public static void main( String[] args ) throws IOException, CsvValidationException {
        AutoAuditing auditTest = new AutoAuditing();


        String testing = "04/05/2023~~~~Dead~~~~2023_SB2883_MS~~~~Healthcare~~~~SB2883_MS~~~~Mississippi~~~~test~~~~test";
        String[] testArray = testing.split(FILE_DELIMITER);

        Legiscan test;
        //Scanner scnr = new Scanner(System.in);

        ArrayList<String> userBillList = null;
        String billName = null;
        int count = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("caseStorage.txt"));
            String line;
            //System.out.println(reader.readLine().toString());
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(FILE_DELIMITER);
                //System.out.println(parts.length);
                for(int i = 0; i < parts.length; i++){
                    if(parts[i].equals("") || parts[i].equals("*&*&*")){
                        parts[i] = "**EMPTY**";
                    }
                }
                allData.add(parts);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred while trying to read the file.");
            e.printStackTrace();
        }
        saveData();
        auditTest.autoAudit();

        char userChoice = 0;
        System.out.println("What would you like to do?");
            System.out.println("A - add a bill");
            System.out.println("B - add a list of bills");
            System.out.println("C - find a case");
            System.out.println("D - audit cases");
            System.out.println("E - Legiscan stuff");
            System.out.println("F - json stuff");
            System.out.println("G - hashmap stuff");
            userChoice = Character.toLowerCase(scnr.next().charAt(0));
            switch (userChoice) {
                case 'a':
                    addBill();
                    break;
                case 'b':
                    System.out.println("Which bills would you like to add (separate by a comma no space)?");
                    break;
                case 'c':
                    String userBillName = getBillInfo();
                    findBill(userBillName);
                    break;
                case 'd':
                    auditStorage();
                    break;
                case 'e':
                    String state = "";
                    String currentID = "";
                    billName = "HB111";
                    int currentLength;
//                        for(int i = 0; i < allData.size(); i++){
//                            currentLength = allData.get(i)[4].length();
//                            currentID = allData.get(i)[4];
//                            state = currentID.substring(currentLength - 2);
//                            billName = currentID.substring(0, currentLength - 3);
//
//                            System.out.println(billName + " " + state);
//                            test = new Legiscan(billName, state);
//                            test.API();
                        //}


                    break;
                case 'f':
                    String content;
                    Table table = null;
                    test = new Legiscan();
                    ArrayList<String> fileID = new ArrayList<>();
                    try {
                        content = new String(Files.readAllBytes(Paths.get("billFiles/allSessions.json")));
                        JSONArray jsonArray = new JSONArray(content);

                        List<JSONObject> jsonObjects = new ArrayList<>();
                        for(int i=0; i<jsonArray.length(); i++) {
                            jsonObjects.add(jsonArray.getJSONObject(i));
                        }

                        // Assuming all JSON objects have the same keys
                        // Define a list of expected keys
                        List<String> jsonKeys = Arrays.asList(
                                "session_id", "state_id", "year_start", "year_end",
                                "prefile", "sine_die", "prior", "special",
                                "session_tag", "session_title", "session_name",
                                "dataset_hash", "session_hash", "name");

                        table = Table.create("State Sessions");

                        for(String key : jsonKeys) {
                            StringColumn column = StringColumn.create(key);
                            for(JSONObject obj : jsonObjects) {
                                Object value = obj.opt(key);
                                column.append(value != null ? value.toString() : null);
                            }
                            table.addColumns(column);
                        }
                        for (Row row : table) {
                            //System.out.println(row);
                            String session_id = row.getString("session_id");
                            String state_id = row.getString("state_id");
                            //test = new Legiscan(session_id, state_id);
                            test.setSessionID(session_id);
                            test.setStateID(state_id);
                            System.out.println("state id = " + state_id + " session id = " + session_id);
                            test.API();
                        }
                        CsvWriteOptions writeOptions = CsvWriteOptions.builder("billFiles/sessionsOutput.csv").build();
                        table.write().csv(writeOptions);
                        System.out.println(table.print());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //System.out.println(table.print());

                    break;
                case 'g':

                    File folder = new File("billFiles");
                    File[] listOfFiles = folder.listFiles();

                    // We only want to process files (not directories) and only files that end with .json
                    List<File> jsonFiles = new ArrayList<>();
                    for (File file : listOfFiles) {
                        if (file.isFile() && file.getName().endsWith(".json")) {
                            //System.out.println(file);
                            jsonFiles.add(file);
                        }
                    }
                    StateSessionBillProcessor processor = new StateSessionBillProcessor();
                    processor.processFiles(jsonFiles);
                    processor.saveToFile("otherBillFiles/data.json");
                    processor.saveToFileSerialized("otherBillFiles/fullMapData2.ser");
                    stateSessionBillsMap = processor.loadFromFile("otherBillFiles/fullMapData2.ser");

                    //System.out.println(stateSessionBillsMap.get(1).get(29).get(0).getNumber().toString());
//                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                    String json = gson.toJson(stateSessionBillsMap.get(1));
//                    System.out.println(json);


                    break;
                default:
                    System.out.println("Not a correct option");
                    break;

            }
    }

    public static boolean addBill(){
        boolean isValid = false;
        char userChar = 0;
        String userBill;
        while(!isValid) {
            userBill = getBillInfo();
            if(!findBill(userBill)){
                System.out.println("Bill cannot be added because bill already exists in data base.");
            }else{
                String[] info = new String[6];
                String billStatus = getBillInfo();
                info[0] = getDate();
                info[1] = getBillStatus();
                info[2] = billStatus;
                info[3] = getBillIssue();
                info[4] = billStatus.substring(0,5);
                info[5] = getBillState();

                allData.add(info);
                saveData();
            }

            System.out.println("Would you like to add another bill (y/n)?");
            userChar = Character.toLowerCase(scnr.nextLine().charAt(0));

            if(userChar == 'y'){
                isValid = true;
            }else if(userChar == 'n'){
                isValid = false;
            }

        }

        return false;
    }

    public static void getEmptyCases(){
        for(int i = 0; i < allData.size(); i++){
            for(int j = 0; j < allData.get(i).length; j++){

            }
        }
    }
    public static boolean findBill(String billName){
        //String billName = getBillInfo();
        int lineNum;
        for(int i = 0; i < allData.size(); i++){
            if(allData.get(i)[2].equals(billName)){
                lineNum = i;
                System.out.println(billName + " was found on line " + (lineNum + 1));
                return true;
            }
        }
        System.out.println("Bill could not be found.");
        return false;
    }

    public static String getDate(){
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = today.format(formatter);
        return formattedDate;
    }

    public static String getBillStatus(){
        String billStatus = null;
        boolean isValid = false;
        int userNum = 0;
        while(!isValid) {
            System.out.println("What is the status of this bill?");
            System.out.println(" 1 - Introduced");
            System.out.println(" 2 - In Committee");
            System.out.println(" 3 - Crossed Over");
            System.out.println(" 4 - Passed");
            System.out.println(" 5 - Signed/Enacted");
            System.out.println(" 6 - Vetoed");
            System.out.println(" 7 - Veto Overridden");
            System.out.println(" 8 - Dead");
            System.out.println(" 9 - Engrossed");
            System.out.println(" 10 - Unknown");
            userNum = scnr.nextInt();

            if(userNum < 1 || userNum > 10){
                isValid = false;
                System.out.println("Not a valid number.");
            }else{
                isValid = true;
            }
        }
        billStatus = BILL_STATUS[userNum - 1];
        return billStatus;
    }

    public static String getBillInfo(){
        String billName;
        String billState = null;
        String billYear = null;
        boolean isFound = false;
        boolean isValid = false;
        int currentYear = LocalDate.now().getYear();
        int billYearAsInt = 0;
        int line = 0;

        System.out.println("Which bill would you like to find?");
        billName = scnr.nextLine().toUpperCase();

        billState = getBillState();
        isValid = false;
        while(!isValid) {
            System.out.println("What year is the bill?");
            billYearAsInt = scnr.nextInt();

            if((billYearAsInt < 2000) || (billYearAsInt > currentYear)){
                isValid = false;
                System.out.println("The year " + billYearAsInt + " was not between the years 2000 " + currentYear
                        + ".");
            }else{
                isValid = true;
                billYear = Integer.toString(billYearAsInt);
            }
        }
        billName = billYear + "_" + billName + "_" + billState;
        return billName;
    }

    public static String getBillIssue(){
        String issue = null;
        boolean isValid = false;
        int userNum = 0;
        while(!isValid){
            System.out.println("What issue does this bill fall under?");
            System.out.println(" 1 - Accurate IDs");
            System.out.println(" 2 - Civil Rights");
            System.out.println(" 3 - Free Speech & Expression");
            System.out.println(" 4 - Healthcare");
            System.out.println(" 5 - Public Accommodations");
            System.out.println(" 6 - Schools & Education");
            System.out.println(" 7 - Other");
            userNum = scnr.nextInt();

            if(userNum < 1 || userNum > 7){
                isValid = false;
                System.out.println("Choice is not within the correct range");
            }else{
                issue = ISSUES_LIST[userNum -1];
                isValid = true;
            }
        }
        return issue;
    }

    public static String getBillState(){
        boolean isValid = false;
        String billState = null;
        boolean isFound = false;
        int line = 0;

        while(!isValid){
            System.out.println("Which state is this bill in?");
            billState = scnr.nextLine().toLowerCase();
            for (int i = 0; i < STATES.length; i++) {
                String current = STATES[i].toLowerCase();
                if (current.equals(billState)) {
                    isFound = true;
                    line = i;
                }
            }

            if(isFound){
                isValid = true;
                billState = STATES_ABBREV[line];
            }else{
                isValid = false;
                System.out.println("State " + billState + " not found. Make sure you spelled it correctly.");
            }
        }

        return billState;
    }

    public static void saveData(){
        try {
            PrintWriter writer = new PrintWriter("caseStorage.txt");
            for(int i = 0; i < allData.size(); i++){
                //System.out.println(allData.get(i)[2]);
                //System.out.println(i);
                for(int j = 0; j < allData.get(i).length; j++){
                    writer.print(allData.get(i)[j]);

                    if(j < allData.get(i).length -1){
                        writer.print(FILE_DELIMITER);

                    }
                }
                writer.println();
//                writer.println(allData.get(i)[0] + FILE_DELIMITER + allData.get(i)[1] + FILE_DELIMITER +
//                        allData.get(i)[2] + FILE_DELIMITER + allData.get(i)[3] + FILE_DELIMITER + allData.get(i)[4] +
//                        FILE_DELIMITER + allData.get(i)[5] + FILE_DELIMITER + allData.get(i)[6] + FILE_DELIMITER
//                        + allData.get(i)[7]);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while trying to write to the file.");
            e.printStackTrace();
        }
    }

    public static Boolean auditStorage(){
        int count = 0;
        int userNum = 0;
        String date = null;
        String userValue = "";
        System.out.println("got here");
        for(int i = 0; i < allData.size(); i++){
            for(int j = 0; j < allData.get(i).length; j++){
                if(allData.get(i)[j].equals("**EMPTY**")){
                    System.out.println("The " + HEADERS[j] + " for bill " + allData.get(i)[4] + " on line " + i +
                            " is empty. What would you like to replace it with?");

                    if(j == 3){
                        String issue = getBillIssue();
                        allData.get(i)[j] = issue;
                        System.out.println(allData.get(i)[j]);
                    }else if(j == 2){
                        System.out.println("What year is the bill?");
                        userNum = scnr.nextInt();
                        allData.get(i)[j] = userNum + "_" + allData.get(i)[4];

                        System.out.println(allData.get(i)[j]);
                    }else if(j == 1){
                        allData.get(i)[j] = getBillStatus();
                        System.out.println(allData.get(i)[j]);
                    }else if(j == 0){
                        date = getDate();
                        System.out.println("Date Last Checked automatically changed to " + date);
                        allData.get(i)[j] = date;
                    }else{
                        scnr.nextLine();
                        userValue = scnr.nextLine();
                        allData.get(i)[j] = userValue;
                    }

                }
            }
            saveData();
        }
        return false;
    }
}
