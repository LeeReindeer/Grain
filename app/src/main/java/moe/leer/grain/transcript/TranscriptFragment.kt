package moe.leer.grain.transcript


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_transcript.*
import moe.leer.grain.R
import moe.leer.grain.base.BaseFragment
import moe.leer.grain.model.Transcript
import moe.leer.grain.toast


class TranscriptFragment : BaseFragment() {

    override var layoutId: Int
        get() = R.layout.fragment_transcript
        set(value) {}

    private lateinit var transcriptViewModel: TranscriptViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TranscriptAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.adapter = TranscriptAdapter(this@TranscriptFragment.requireContext())
        recyclerView = transcriptRV
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        transcriptViewModel = ViewModelProviders.of(this).get(TranscriptViewModel::class.java)
        transcriptRefresh.isRefreshing = true
        transcriptViewModel.getTranscript().observe(this, Observer<MutableList<Transcript>?> { transcript ->
            Log.d(TAG, "observe: transcript size :${transcript?.size}")
            showEmptyList(transcript == null || transcript.isEmpty())

            Log.d(TAG, "onViewCreated: errorPage: ${errorPage.visibility == View.VISIBLE}")
            adapter.transcriptList = transcript
            transcriptRefresh.isRefreshing = false
        })
        initView()

//        Handler().postDelayed({
//            transcriptViewModel.refresh()
//        }, 500)
    }

    private fun showEmptyList(show: Boolean) {
        Glide.with(this)
            .load(R.mipmap.witch)
            .into(errorImage)
        if (show) {
            errorPage.visibility = View.VISIBLE
        } else {
            errorPage.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initView() {
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
        yearSpinner.adapter = yearSpinnerAdapter
        semesterSpinner.adapter = semesterSpinnerAdapter

        semesterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                (parent?.getChildAt(0) as TextView?)?.setTextColor(Color.WHITE)
                transcriptViewModel.filterSemester(pos)
            }


        }

        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                (parent?.getChildAt(0) as TextView?)?.setTextColor(Color.WHITE)
                transcriptViewModel.filterYear(pos)
            }

        }
        transcriptRefresh.setOnRefreshListener {
            refresh()
            transcriptRefresh.isRefreshing = false
        }
        errorPage.setOnClickListener {
            refresh()
        }
    }

    private fun refresh() {
        yearSpinner.setSelection(0, true)
        semesterSpinner.setSelection(0, true)
        transcriptViewModel.refresh {
            //onError
            toast(R.string.hint_check_network)
        }
    }

}
