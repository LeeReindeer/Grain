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
import xyz.leezoom.grain.module.Book;

/**
 * @Author lee
 * @Time 9/8/17.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private List<Book> mBooks;
    private Context mContext;

    public BookAdapter(Context context, List<Book> books) {
        this.mContext = context;
        this.mBooks = books;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.book_list_item,parent,false);
        BookAdapter.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book= mBooks.get(position);
        holder.name.setText(book.getName());
        holder.author.setText(book.getAuthor());
        holder.param1.setText(book.getParam1());
        holder.param2.setText(book.getParam2());
        holder.place.setText(book.getPlace());

        holder.firstChar.setText(book.getName().substring(0,1));
        holder.colorImage.setImageResource(R.color.brown_500);
        holder.textView1.setText("Borrow: ");
        holder.textView2.setText("Back: ");
        holder.textView3.setText("Place: ");
    }

    @Override
    public int getItemCount() {
        if (mBooks != null){
        return mBooks.size();
        }else {
            return 0;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        @BindView(R.id.bk_logo) CircleImageView colorImage;
        @BindView(R.id.bk_first_char) TextView firstChar;
        @BindView(R.id.bk_name) AppCompatTextView name;
        @BindView(R.id.bk_author) AppCompatTextView author;
        @BindView(R.id.bk_param1) AppCompatTextView param1;
        @BindView(R.id.bk_param2) AppCompatTextView param2;
        @BindView(R.id.bk_place) AppCompatTextView place;
        @BindView(R.id.gr_bk_param1) TextView textView1;
        @BindView(R.id.gr_bk_param2) TextView textView2;
        @BindView(R.id.gr_bk_param3) TextView textView3;
        public ViewHolder(View view){
            super(view);
            mView = view;
            ButterKnife.bind(this,view);
        }
    }
}
