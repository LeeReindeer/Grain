package moe.leer.grain.transcript


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_transcript.view.*
import moe.leer.grain.R
import moe.leer.grain.model.Transcript


class TranscriptFragment : androidx.fragment.app.Fragment() {

    private val TAG = "TranscriptFragment"

    private lateinit var transcriptViewModel: TranscriptViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TranscriptAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transcript, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.adapter = TranscriptAdapter(this@TranscriptFragment.requireContext())
        recyclerView = view.transcriptRV
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())


        transcriptViewModel = ViewModelProviders.of(this).get(TranscriptViewModel::class.java)
        view.transcriptRefresh.isRefreshing = true
        transcriptViewModel.getTranscript().observe(this, Observer<MutableList<Transcript>?> { transcript ->
            Log.d(TAG, "observe: transcript size :${transcript?.size}")
            if (transcript == null || transcript.isEmpty()) {
                view.errorPage.visibility = View.VISIBLE
            } else {
                view.errorPage.visibility = View.GONE
            }

            Log.d(TAG, "onViewCreated: errorPage: ${view.errorPage.visibility == View.VISIBLE}")
            adapter.transcriptList = transcript
            view.transcriptRefresh.isRefreshing = false
        })
        initView(view)

//        Handler().postDelayed({
//            transcriptViewModel.refresh()
//        }, 500)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initView(view: View) {
        val yearSpinnerAdapter = ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.spinner_year,
            android.R.layout.simple_spinner_item
        )
        val semesterSpinnerAdapter = ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.spinner_semester,
            android.R.layout.simple_spinner_item
        )
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        semesterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.yearSpinner.adapter = yearSpinnerAdapter
        view.semesterSpinner.adapter = semesterSpinnerAdapter

        view.semesterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                (parent?.getChildAt(0) as TextView?)?.setTextColor(Color.WHITE)
                transcriptViewModel.filterSemester(pos)
            }


        }

        view.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                (parent?.getChildAt(0) as TextView?)?.setTextColor(Color.WHITE)
                transcriptViewModel.filterYear(pos)
            }

        }
        view.transcriptRefresh.setOnRefreshListener {
            transcriptViewModel.refresh()
            view.transcriptRefresh.isRefreshing = false
        }
    }
}
