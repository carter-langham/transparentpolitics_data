package ellis;

import java.io.File;
import java.util.ArrayList;
import java.util.*;

import static ellis.Main.*;
import static ellis.Main.stateSessionBillsMap;

public class AutoAuditing {

    private static int state_id;

    private static Scanner scnr = new Scanner(System.in);

    public static final String[] STATES_ABBREV = {"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL",
            "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM",
            "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI",
            "WY", "DC", "US"};

    public static final String[] STATES = {"Alabama", "Alaska", "Arizona", "Arkansas", "California",
            "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana",
            "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi",
            "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York",
            "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island",
            "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
            "West Virginia", "Wisconsin", "Wyoming", "District of Columbia", "United States"};

    private static final String[] STATUS_PROGRESS = {"Pre-filed or pre-introduction", "Introduced", "Engrossed",
            "Enrolled", "Passed", "Vetoed", "Failed", "Override", "Chaptered", "Refer", "Report Pass", "Report DNP",
            "Draft"};

    private static int caseNum = 0;
    private static ArrayList<String> possibleCases = new ArrayList<>();
    private static ArrayList<Bill> possibleBills = new ArrayList<>();

    private static List<Bill> bills = null;

    private static Integer sessionId = null;
    public AutoAuditing(){
        System.out.println("Loading files...");
        loadFile();
        System.out.println("Finished loading");

        //findCorrectBill("HB111", 43);
        //System.out.println(stateSessionBillsMap.get(1).size());
        //System.out.println(stateSessionBillsMap.get(1).get(29).get(0).getNumber().toString());
    }

    public static void startAuditing(String billName, int state){
        Bill currentBill = findCorrectBill(billName,state);


    }

    public static int stateToStateID(String stateAbrev){

        for(int i = 0; i < STATES_ABBREV.length; i ++){
            if(STATES_ABBREV[i].toLowerCase().equals(stateAbrev.toLowerCase())){
                return (1 + i);

            }
        }
        return -1;
    }

    public static Bill findCorrectBill(String billName, int state){
        HashMap<Integer, List<Bill>> sessionMap = stateSessionBillsMap.get(state);
        char userChoice = 0;
        //ArrayList<Integer> possibleCases = new ArrayList<>();
        if(sessionMap != null){
            Iterator<Map.Entry<Integer, List<Bill>>> iterator = sessionMap.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<Integer, List<Bill>> entry = iterator.next();
                sessionId = entry.getKey();
                bills = entry.getValue();
                List<Bill> billsToRemove = new ArrayList<>();
                for(int i = 0; i < bills.size(); i++){
                    Bill bill = bills.get(i);
                    if(billName.equalsIgnoreCase(bill.getNumber())){
                        possibleCases.add(bill.getNumber() + "_" + bill.getBillId() + "_" + state + "_" + sessionId +
                                "_" + bill.getLastActionDate());
                        possibleBills.add(bill);
                    }else{
                        billsToRemove.add(bill);
                    }
                }

                bills.removeAll(billsToRemove);
                if(bills.isEmpty()){
                    iterator.remove(); // remove the current entry from sessionMap
                }
            }
        }

        int possibleBillsIndex = 0;  // add this line to keep track of possibleBills index separately

        if(sessionMap != null){
            for(Map.Entry<Integer, List<Bill>> entry : sessionMap.entrySet()){
                sessionId = entry.getKey();
                List<Bill> bills = entry.getValue();
                for(int i = 0; i < bills.size(); i++){
                    Bill bill = bills.get(i);
                    System.out.println(bills.get(i).toString());

                    System.out.println();
                    System.out.println();
                    System.out.println("Is this the correct case? (y/n)");
                    userChoice = scnr.nextLine().charAt(0);
                    if(Character.toLowerCase(userChoice) == 'y'){
                        caseNum = i;
                        return possibleBills.get(possibleBillsIndex);
                    }else{
                        System.out.println("Noted. Case will be removed. Cases Remaining - " + possibleCases.size());
                        possibleCases.remove(i);
                        possibleBills.remove(possibleBillsIndex);
                        System.out.println();
                        System.out.println();
                    }
                    possibleBillsIndex++;  // increment possibleBillsIndex
                }
            }
        }
        return null;
    }




    public  void setStateID(int state_id){
        this.state_id = state_id;
    }

    public static int getStateID(){
        return state_id;
    }

    public static void addNewEntries(Bill activeBill, String currentBillName, String currentState, int currentCell, int currentArray){
        if(activeBill != null){
            switch (currentCell) {
                case 0:    //date last checked
                    allData.get(currentArray)[currentCell] = getDate();
                    System.out.println("Date changed to " + getDate());
                    break;
                case 1:    //status
                    allData.get(currentArray)[currentCell] = convertStatus(activeBill.getStatus());
                    break;
                case 2:    //case id
                    allData.get(currentArray)[currentCell] = (activeBill.getLastActionDate().substring(0, 4) + "_" + currentBillName
                            + "_" + currentState);
                    break;
                case 3:    //issue
                    break;
                case 4:    //bill name
                    allData.get(currentArray)[currentCell] = activeBill.getNumber() + "_" + currentState;
                    break;
                case 5:    //State
                    break;
                case 6:    //bill id
                    allData.get(currentArray)[currentCell] = String.valueOf(activeBill.getBillId());
                    break;
                case 7:    //session id
                    allData.get(currentArray)[currentCell] = String.valueOf(sessionId);
                    break;
                default:
                    allData.get(currentArray)[currentCell] = "**EMPTY**";
                    break;
            }
        }
    }

    public static Boolean autoAudit(){

        int userNum = 0;
        String date = null;

        String currentState = null;
        String currentBillName = null;
        boolean doRun = false;
        for(int i = 0; i < allData.size(); i++){
            Bill activeBill = null;
            ArrayList<Integer> emptyCells = new ArrayList<>();
            for (int j = 0; j < allData.get(i).length; j++) {
                if (allData.get(i)[j].equals("**EMPTY**")) {
                    //System.out.println(allData.get(i)[j]);
                    emptyCells.add(j);
                    doRun = true;
                }
                saveData();
            }
            if((emptyCells != null) && (doRun)) {
                currentBillName = allData.get(i)[4].substring(0, allData.get(i)[4].length() - 3);  //bill name
                currentState = allData.get(i)[4].substring(allData.get(i)[4].length() - 2); //state
                activeBill = findCorrectBill(currentBillName, stateToStateID(currentState));
                for (int k = 0; k < emptyCells.size(); k++) {
                    addNewEntries(activeBill, currentBillName, currentState, emptyCells.get(k), i);
                }
                saveData();
            }
            doRun = false;
        }
        return false;
    }

    public static String convertStatus(int status_id){
        if(status_id >= 0){
            return STATUS_PROGRESS[status_id];
        }else{
            return "UNKNOWN";
        }
    }
    public static void loadFile(){

        File folder = new File("billFiles");
        File[] listOfFiles = folder.listFiles();
        List<File> jsonFiles = new ArrayList<>();
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                //System.out.println(file);
                jsonFiles.add(file);
            }
        }
        StateSessionBillProcessor processor = new StateSessionBillProcessor();
        processor.processFiles(jsonFiles);
        stateSessionBillsMap = processor.loadFromFile("otherBillFiles/fullMapData2.ser");

    }

}
