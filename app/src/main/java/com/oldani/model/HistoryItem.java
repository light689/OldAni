package com.oldani.model;

public class HistoryItem {
    public int episodeId;
    public long positionMillis;
    public int subjectId;
    public String subjectName;
    public String subjectImageUrl;
    public String episodeName;
    public long durationMillis;
    public long updatedAtMillis;

    public int progressPercent() {
        if (durationMillis <= 0) return 0;
        return (int) (positionMillis * 100 / durationMillis);
    }
}
