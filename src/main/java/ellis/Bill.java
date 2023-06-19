/**
 * Version 2.0 June-6-2023
 * Author: Carter Langham
 */
package ellis;

import java.io.Serializable;

public class Bill implements Serializable {

    private static final long serialVersionUID = 2L;
    private int billId;
    private String number;
    private String changeHash;
    private String url;
    private String statusDate;
    private int status;
    private String lastActionDate;
    private String lastAction;
    private String title;
    private String description;

    // getters and setters

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getChangeHash() {
        return changeHash;
    }

    public void setChangeHash(String changeHash) {
        this.changeHash = changeHash;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLastActionDate() {
        return lastActionDate;
    }

    public void setLastActionDate(String lastActionDate) {
        this.lastActionDate = lastActionDate;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String toString(){
        StringBuilder billInfo = new StringBuilder();
        billInfo.append("bill_id: " + getBillId());
        billInfo.append("\n");
        billInfo.append("number: " + getNumber());
        billInfo.append("\n");
        billInfo.append("change_hash: " + getChangeHash());
        billInfo.append("\n");
        billInfo.append("url: " + getUrl());
        billInfo.append("\n");
        billInfo.append("status_date: " + getStatusDate());
        billInfo.append("\n");
        billInfo.append("status: " + getStatus());
        billInfo.append("\n");
        billInfo.append("last_action_date: " + getLastActionDate());
        billInfo.append("\n");
        billInfo.append("last_action: " + getLastAction());
        billInfo.append("\n");
        billInfo.append("title: " + getTitle());
        billInfo.append("\n");
        billInfo.append("description: " + getDescription());


        return billInfo.toString();
    }
}
