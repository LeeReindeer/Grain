package moe.leer.grain.transcript


//import kotlinx.android.synthetic.main.fragment_transcript.*
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import moe.leer.grain.App
import moe.leer.grain.R
import moe.leer.grain.base.BaseFragment
import moe.leer.grain.model.Transcript
import moe.leer.grain.toast


class TranscriptFragment : BaseFragment() {

    override var layoutId: Int
        get() = R.layout.fragment_transcript
        set(value) {}

    private lateinit var transcriptViewModel: TranscriptViewModel
    private lateinit var adapter: TranscriptAdapter

    private lateinit var transcriptRefresh: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var errorTextView: TextView
    private lateinit var errorImageView: AppCompatImageView
    private lateinit var errorLayout: RelativeLayout

    private lateinit var yearSpinner: AppCompatSpinner
    private lateinit var semesterSpinner: AppCompatSpinner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.transcriptRV)
        transcriptRefresh = view.findViewById(R.id.transcriptRefresh)
        errorTextView = view.findViewById(R.id.errorText)
        errorImageView = view.findViewById(R.id.errorImage)
        errorLayout = view.findViewById(R.id.errorPage)
        yearSpinner = view.findViewById(R.id.yearSpinner)
        semesterSpinner = view.findViewById(R.id.semesterSpinner)

        this.adapter = TranscriptAdapter(this@TranscriptFragment.requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        val controller = AnimationUtils.loadLayoutAnimation(activity, R.anim.anim_layout_fall_down)
        recyclerView.layoutAnimation = controller

        transcriptViewModel = ViewModelProviders.of(this).get(TranscriptViewModel::class.java)
        transcriptRefresh.isRefreshing = true
        transcriptViewModel.getTranscript {
            showEmptyList(true, R.string.hint_check_network)
        }.observe(this, Observer<MutableList<Transcript>?> { transcript ->
            Log.d(TAG, "observe: transcript size :${transcript?.size}")
            if (App.getApplication(requireContext().applicationContext).isLogin) {
                adapter.transcriptList = transcript
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    Handler().postDelayed({
                        showEmptyList(transcript == null || transcript.isEmpty(), R.string.hint_empty)
                        Log.d(TAG, "onViewCreated: errorPage: ${errorLayout.visibility == View.VISIBLE}")
                        transcriptRefresh.isRefreshing = false
                    }, 500)
                }
            } else {
                showEmptyList(true, R.string.hint_login)
            }
        })
        initView()

//        Handler().postDelayed({
//            transcriptViewModel.refresh()
//        }, 500)
    }

    private fun showEmptyList(show: Boolean, @StringRes errorMsgId: Int) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            Glide.with(this)
                .load(R.mipmap.witch)
                .into(errorImageView)
            errorTextView.setText(errorMsgId)
            if (show) {
                errorLayout.visibility = View.VISIBLE
            } else {
                errorLayout.visibility = View.GONE
            }
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
        errorLayout.setOnClickListener {
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
