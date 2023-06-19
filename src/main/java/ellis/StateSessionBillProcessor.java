package ellis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateSessionBillProcessor {

    private HashMap<Integer, HashMap<Integer, List<Bill>>> stateSessionBillsMap = new HashMap<>();

    public void processFiles(List<File> jsonFiles) {
        for (File file : jsonFiles) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                JSONObject jsonObject = new JSONObject(content);

                // Parse session
                JSONObject sessionObject = jsonObject.getJSONObject("masterlist").getJSONObject("session");
                Session session = new Session();
                session.setSessionId(sessionObject.getInt("session_id"));
                session.setStateId(sessionObject.getInt("state_id"));
                session.setYearStart(sessionObject.getInt("year_start"));
                session.setYearEnd(sessionObject.getInt("year_end"));
                session.setPrefile(sessionObject.getInt("prefile"));
                session.setSineDie(sessionObject.getInt("sine_die"));
                session.setPrior(sessionObject.getInt("prior"));
                session.setSpecial(sessionObject.getInt("special"));
                session.setSessionTag(sessionObject.getString("session_tag"));
                session.setSessionName(sessionObject.getString("session_title"));

                // Parse bills
                List<Bill> bills = new ArrayList<>();
                JSONObject masterlist = jsonObject.getJSONObject("masterlist");
                masterlist.remove("session"); // remove the session object from masterlist
                for (String key : masterlist.keySet()) {
                    JSONObject billObject = masterlist.getJSONObject(key);
                    Bill bill = new Bill();
                    bill.setBillId(billObject.getInt("bill_id"));
                    bill.setNumber(billObject.getString("number"));
                    bill.setChangeHash(billObject.getString("change_hash"));
                    bill.setUrl(billObject.getString("url"));

                    String statusDate = billObject.has("status_date") ? billObject.getString("status_date") : null;
                    bill.setStatusDate(statusDate);

                    int status = billObject.has("status") ? billObject.getInt("status") : null;
                    bill.setStatus(status);

                    String lastActionDate = billObject.has("last_action_date") ? billObject.getString("last_action_date") : null;
                    bill.setLastActionDate(lastActionDate);

                    String lastAction = billObject.has("last_action") ? billObject.getString("last_action") : null;
                    bill.setLastAction(lastAction);

                    String title = billObject.has("title") ? billObject.getString("title") : null;
                    bill.setTitle(title);

                    String description = billObject.has("description") ? billObject.getString("description") : null;
                    bill.setDescription(description);


                    bills.add(bill);
                }

                // Get the inner map for the current state. If it doesn't exist, create it.
                HashMap<Integer, List<Bill>> sessionBillsMap = stateSessionBillsMap.getOrDefault(session.getStateId(), new HashMap<>());

                // Add the current session and its bills to the inner map.
                sessionBillsMap.put(session.getSessionId(), bills);

                // Put the inner map back into the outer map.
                stateSessionBillsMap.put(session.getStateId(), sessionBillsMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<Integer, HashMap<Integer, List<Bill>>> getStateSessionBillsMap() {
        return stateSessionBillsMap;
    }


    public void saveToFileSerialized(String filePath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this.stateSessionBillsMap);
            objectOut.close();
            System.out.println("The state session data was succesfully written to a file");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveToFile(String filePath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this.stateSessionBillsMap);
            objectOut.close();
            System.out.println("The state session data was succesfully written to a file");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public HashMap<Integer, HashMap<Integer, List<Bill>>> loadFromFile(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            HashMap<Integer, HashMap<Integer, List<Bill>>> loadedMap =
                    (HashMap<Integer, HashMap<Integer, List<Bill>>>) objectInputStream.readObject();
            objectInputStream.close();
            return loadedMap;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}

