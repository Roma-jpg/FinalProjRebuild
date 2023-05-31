package ru.romeo558.myprojectrebuild;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetHW {
    private String className;
    List<DayEntry> diaryEntries;
    private String studentName;

    public GetHW(JSONObject jsonResponse) throws JSONException {
        className = jsonResponse.getString("class_name");
        diaryEntries = DayEntry.fromJson(jsonResponse.getJSONArray("diary_entries"));
        studentName = jsonResponse.getString("student_name");
    }

    public static class DayEntry {
        private String dayLabel;
        List<Entry> entries;

        public DayEntry(String dayLabel, List<Entry> entries) {
            this.dayLabel = dayLabel;
            this.entries = entries;
        }

        public static List<DayEntry> fromJson(JSONArray jsonArray) throws JSONException {
            List<DayEntry> dayEntries = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dayEntryJson = jsonArray.getJSONObject(i);
                String dayLabel = dayEntryJson.getString("day_label");
                List<Entry> entries = Entry.fromJson(dayEntryJson.getJSONArray("entries"));
                DayEntry dayEntry = new DayEntry(dayLabel, entries);
                dayEntries.add(dayEntry);
            }
            return dayEntries;
        }
    }

    public static class Entry {
        private String mark;
        private String subject;
        private String task;

        public Entry(String mark, String subject, String task) {
            this.mark = mark;
            this.subject = subject;
            this.task = task;
        }

        public String getMark() {
            return mark;
        }

        public String getSubject() {
            return subject;
        }

        public String getTask() {
            return task;
        }

        public static List<Entry> fromJson(JSONArray jsonArray) throws JSONException {
            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject entryJson = jsonArray.getJSONObject(i);
                String mark = entryJson.optString("mark");
                String subject = entryJson.optString("subject");
                String task = entryJson.optString("task");
                Entry entry = new Entry(mark, subject, task);
                entries.add(entry);
            }
            return entries;
        }
    }

    public static class HomeworkAdapter extends ArrayAdapter<GetHW.Entry> {
        private Context context;
        private List<GetHW.Entry> entries;

        public HomeworkAdapter(Context context, List<GetHW.Entry> entries) {
            super(context, 0, entries);
            this.context = context;
            this.entries = entries;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(context).inflate(R.layout.row_my_list_item, parent, false);
            }

            GetHW.Entry entry = entries.get(position);

            TextView headerTextView = listItemView.findViewById(R.id.headerTextView);
            TextView descriptionTextView = listItemView.findViewById(R.id.descriptionTextView);
            TextView scoreTextView = listItemView.findViewById(R.id.scoreTextView);

            headerTextView.setText(entry.getSubject());
            descriptionTextView.setText(entry.getTask());
            scoreTextView.setText(entry.getMark());

            return listItemView;
        }
    }
}
