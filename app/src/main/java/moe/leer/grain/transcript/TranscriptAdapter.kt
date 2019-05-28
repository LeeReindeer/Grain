package moe.leer.grain.transcript

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import moe.leer.grain.Constant
import moe.leer.grain.R
import moe.leer.grain.getSP
import moe.leer.grain.model.Transcript
import java.util.*

/**
 * Created by leer on 1/15/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
class TranscriptAdapter(private val context: Context) : RecyclerView.Adapter<TranscriptAdapter.ViewHolder>() {

    private val inflater: LayoutInflater

    var transcriptList: List<Transcript>? = null
        set(transcriptList) {
            field = transcriptList
            notifyDataSetChanged()
        }

    init {
        inflater = LayoutInflater.from(context)
    }

    fun getTranscriptAt(pos: Int): Transcript {
        return this.transcriptList!![pos]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.transcript_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transcript = this.transcriptList!![position]
        with(transcript) {
            with(holder) {
                var showScore = getFinalScore(score)
                if (reScore != null) {
                    showScore = getFinalScore(reScore)
                }
                scoreText.text = showScore.toString()
                scoreText.setBackgroundColor(ContextCompat.getColor(context, getBackgroundColor(showScore)))
                usualGradeText.text = usualGrade ?: context.getString(R.string.test_na)
                examGradeText.text = examGrade ?: context.getString(R.string.test_na)

                var name = if (subjectName.length > 10)
                    subjectName.substring(0, 8) + "..."
                else
                    subjectName
                if (reScore != null) {
                    name += " (重修)"
                }
                nameText.text = name

                gradeText.text =
                    String.format(Locale.CHINESE, "学分:%.1f", java.lang.Float.parseFloat(subjectFullGrade))
                propertyText.text = subjectProperty
                timeText.text = String.format(Locale.CHINESE, "%s-%d", year, semester)
            }

        }

    }

    private fun getFinalScore(scoreStr: String): Int {
        var score = 0
        val firstChar = scoreStr[0]
        if (firstChar in '0'..'9') {
            score = Integer.parseInt(scoreStr)
        } else {
            when (scoreStr) {
                "优秀" -> score = 95
                "良好" -> score = 85
                "中等" -> score = 75
                "及格" -> score = 65
                "不及格" -> score = 0
                else -> score = 0
            }
        }
        return score
    }

    @ColorRes
    private fun getBackgroundColor(score: Int): Int {
        if (context.getSP(Constant.SP_SETTING_NAME).getString("key_transcript", "0") == "1") {
            return if (score >= 60) {
                R.color.green
            } else {
                R.color.pink
            }
        }
        return if (score >= 90) {
            R.color.green
        } else if (score >= 80) {
            R.color.blue
        } else if (score >= 70) {
            R.color.orange
        } else if (score >= 60) {
            R.color.grey
        } else {
            R.color.pink
        }
    }

    override fun getItemCount(): Int {
        return if (this.transcriptList == null) 0 else this.transcriptList!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val scoreText: TextView = itemView.findViewById(R.id.finalGradeText)
        val usualGradeText: TextView = itemView.findViewById(R.id.usualGradeText)
        val examGradeText: TextView = itemView.findViewById(R.id.examGradeText)
        val nameText: TextView = itemView.findViewById(R.id.subjectNameText)
        val gradeText: TextView = itemView.findViewById(R.id.subjectGradeText)
        val propertyText: TextView = itemView.findViewById(R.id.subjectPropertyText)
        val timeText: TextView = itemView.findViewById(R.id.subjectTimeText)

    }
}
