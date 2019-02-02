package makecodework.roadscheck;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InfoRecyclerAdapter extends RecyclerView.Adapter<InfoRecyclerAdapter.ViewHolder>{

    public InfoRecyclerAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView infoText;
        TextView infoPerform;
        TextView priceView;
        TextView dateView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            infoText = itemView.findViewById(R.id.info_title);
            infoPerform = itemView.findViewById(R.id.info_performer);
            priceView = itemView.findViewById(R.id.price_view);
            dateView = itemView.findViewById(R.id.date_view);
        }
    }
}
