package xyz.leezoom.grain.ui;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.Mark;
import xyz.leezoom.grain.ui.view.BigCharView;

/**
 * @Author lee
 * @Time 9/5/17.
 */

public class MarkAdapter extends RecyclerView.Adapter<MarkAdapter.ViewHolder>{

    private List<Mark> mMarks;
    private Context mContext;

    public MarkAdapter(Context context, List<Mark> marks) {
        this.mContext = context;
        this.mMarks = marks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.mark_list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Mark mark = mMarks.get(position);
        holder.mClassName.setText(mark.getName());
        holder.mTeacher.setText(mark.getTeacherName());
        holder.mScore.setText(mark.getScore());
        holder.mCredit.setText(mark.getCredit());
        holder.mGP.setText(mark.getGp());
        //set first char
        holder.bigCharView.setColor(getImageColor(mark.getScore()));
        holder.bigCharView.setFirstCHar(mark.getName().substring(0,1));
    }

    private int getImageColor(String score){
        int mark = Integer.parseInt(score);
        int color=-1;
        int colors [] ={R.color.pink_700,R.color.brown_700,R.color.blue_700,R.color.teal_700};
        //change color depends on your score;
        if (mark >= 90){
            color = colors[3];
        }else if (mark >= 75){
            color = colors[2];
        }else if(mark >= 60&&mark < 75){
            color = colors[1];
        }else {
            color = colors[0];
        }
        return color;
    }

    @Override
    public int getItemCount() {
        return mMarks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        @BindView(R.id.big_char_view) BigCharView bigCharView;
        @BindView(R.id.mk_name) AppCompatTextView mClassName;
        @BindView(R.id.mk_teacher) AppCompatTextView mTeacher;
        @BindView(R.id.mk_score) AppCompatTextView mScore;
        @BindView(R.id.mk_credit) AppCompatTextView mCredit;
        @BindView(R.id.mk_gp) AppCompatTextView mGP;
        public ViewHolder(View view){
            super(view);
            mView = view;
            ButterKnife.bind(this,view);
        }
    }

}
