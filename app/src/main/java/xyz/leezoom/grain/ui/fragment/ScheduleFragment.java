package xyz.leezoom.grain.ui.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.Schedule;
import xyz.leezoom.grain.ui.view.ScheduleView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    private FloatingActionButton fabB;
    private FloatingActionsMenu fabMenu;
    @BindView(R.id.schedule_view)
    ScheduleView scheduleView;
    private EditText editText;
    //course list in a day.
    private Map<List<Schedule>, Integer> courseMap = new HashMap<>();
    private String allCourseInfo;

    public ScheduleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, view);
        fabMenu = getActivity().findViewById(R.id.multiple_actions);
        fabB = getActivity().findViewById(R.id.fab_b);
        fabB.setVisibility(View.VISIBLE);
        fabB.setTitle("Add");
        fabB.setIcon(R.drawable.ic_exit_to_app_black_48dp);
        fabB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 9/23/17\
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                AlertDialog dialog= builder
                        .setTitle("Your course info")
                        .setIcon(R.mipmap.ic_reindeer)
                        .setCancelable(true)
                        .setView(scheduleView.setInputEdit(editText = new EditText(getContext())))
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                allCourseInfo = editText.getText().toString();
                                Log.d("Schedule", allCourseInfo);
                                //scheduleView.invalidate();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                editText.setMaxLines(10);
                dialog.show();
                fabMenu.collapse();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
    }
}
