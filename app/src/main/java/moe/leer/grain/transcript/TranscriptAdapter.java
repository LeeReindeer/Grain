package moe.leer.grain.transcript;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import moe.leer.grain.R;
import moe.leer.grain.model.Transcript;

import java.util.List;
import java.util.Locale;

/**
 * Created by leer on 1/15/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
public final class TranscriptAdapter extends RecyclerView.Adapter<TranscriptAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private List<Transcript> transcriptList;
    private Context context;

    public TranscriptAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public List<Transcript> getTranscriptList() {
        return transcriptList;
    }

    public void setTranscriptList(List<Transcript> transcriptList) {
        this.transcriptList = transcriptList;
        notifyDataSetChanged();
    }

    public Transcript getTranscriptAt(int pos) {
        return transcriptList.get(pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.transcript_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transcript transcript = transcriptList.get(position);

        int score = getFinalScore(transcript.getScore());
        if (transcript.getReScore() != null) {
            score = getFinalScore(transcript.getReScore());
        }
        holder.scoreText.setText(String.valueOf(score));
        holder.scoreText.setBackgroundColor(ContextCompat.getColor(context, getBackgroundColor(score)));

        String name = transcript.getSubjectName().length() > 10 ?
                transcript.getSubjectName().substring(0, 8) + "..." :
                transcript.getSubjectName();
        if (transcript.getReScore() != null) {
            name += " (重修)";
        }
        holder.nameText.setText(name);

        holder.gradeText.setText(String.format(Locale.CHINESE, "%.1f学分", Float.parseFloat(transcript.getSubjectFullGrade())));
        holder.propertyText.setText(transcript.getSubjectProperty());
        holder.timeText.setText(String.format(Locale.CHINESE, "%s 第%d学期", transcript.getYear(), transcript.getSemester()));
    }

    private int getFinalScore(String scoreStr) {
        int score = 0;
        char firstChar = scoreStr.charAt(0);
        if (firstChar >= '0' && firstChar <= '9') {
            score = Integer.parseInt(scoreStr);
        } else {
            switch (scoreStr) {
                case "优秀":
                    score = 95;
                    break;
                case "良好":
                    score = 85;
                    break;
                case "中等":
                    score = 75;
                    break;
                case "及格":
                    score = 65;
                    break;
                case "不及格":
                    score = 0;
                    break;
                default:
                    score = 0;
                    break;
            }
        }
        return score;
    }

    //todo 可以分过了和没过的
    private @ColorRes
    int getBackgroundColor(int score) {
        if (score >= 90) {
            return R.color.green;
        } else if (score >= 80) {
            return R.color.blue;
        } else if (score >= 70) {
            return R.color.orange;
        } else if (score >= 60) {
            return R.color.grey;
        } else {
            return R.color.pink;
        }
    }

    @Override
    public int getItemCount() {
        return transcriptList == null ? 0 : transcriptList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView scoreText;
        final TextView nameText;
        final TextView gradeText;
        final TextView propertyText;
        final TextView timeText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            scoreText = itemView.findViewById(R.id.scoreText);
            nameText = itemView.findViewById(R.id.subjectNameText);
            gradeText = itemView.findViewById(R.id.subjectGradeText);
            propertyText = itemView.findViewById(R.id.subjectPropertyText);
            timeText = itemView.findViewById(R.id.subjectTimeText);
        }
    }
}
