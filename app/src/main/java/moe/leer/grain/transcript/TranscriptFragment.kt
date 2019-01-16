package moe.leer.grain.transcript


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_transcript.view.*
import moe.leer.grain.App
import moe.leer.grain.R
import moe.leer.grain.model.Transcript
import moe.leer.grain.toast


class TranscriptFragment : androidx.fragment.app.Fragment() {

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
                transcriptViewModel.filterSemester(pos)
            }


        }
        view.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                transcriptViewModel.filterYear(pos)
            }

        }

        view.transcriptRefresh.isRefreshing = true
        transcriptViewModel = ViewModelProviders.of(this).get(TranscriptViewModel::class.java)
        transcriptViewModel.getTranscript().observe(this, Observer<MutableList<Transcript>> { transcript ->
            adapter.transcriptList = transcript
            view.transcriptRefresh.isRefreshing = false
        })

        view.transcriptRefresh.setOnRefreshListener {
            if (!App.getApplication(this.requireContext().applicationContext).isLogin) {
                toast("请先登录")
                view.transcriptRefresh.isRefreshing = false
                return@setOnRefreshListener
            }
            transcriptViewModel.refresh()
            view.transcriptRefresh.isRefreshing = false
        }
    }
}
