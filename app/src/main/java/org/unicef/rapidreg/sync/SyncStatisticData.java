package org.unicef.rapidreg.sync;

public class SyncStatisticData {
    private String lastSyncData;
    private int syncedNumber;
    private int notSyncedNumber;

    public SyncStatisticData(String lastSyncData, int syncedNumber, int notSyncedNumber) {
        this.lastSyncData = lastSyncData;
        this.syncedNumber = syncedNumber;
        this.notSyncedNumber = notSyncedNumber;
    }

    public SyncStatisticData() {
    }

    public String getLastSyncData() {
        return lastSyncData;
    }

    public void setLastSyncData(String lastSyncData) {
        this.lastSyncData = lastSyncData;
    }

    public String getSyncedNumberAsString() {
        return String.valueOf(syncedNumber);
    }

    public void setSyncedNumber(int syncedNumber) {
        this.syncedNumber = syncedNumber;
    }

    public String getNotSyncedNumberAsString() {
        return String.valueOf(notSyncedNumber);
    }

    public void setNotSyncedNumber(int notSyncedNumber) {
        this.notSyncedNumber = notSyncedNumber;
    }

    @Override
    public String toString() {
        return "SyncStatisticData{" +
                "lastSyncData='" + lastSyncData + '\'' +
                ", syncedNumber='" + syncedNumber + '\'' +
                ", notSyncedNumber='" + notSyncedNumber + '\'' +
                '}';
    }
}
