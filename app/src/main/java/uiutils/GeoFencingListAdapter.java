package uiutils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medapptest.R;
import com.example.medapptest.model.GeoFenceData;
import java.util.List;

public class GeoFencingListAdapter extends
        RecyclerView.Adapter<GeoFencingListAdapter.MyViewHolder> {
    private List<GeoFenceData> geoFenceDataList;

    public GeoFencingListAdapter(List<GeoFenceData> geoFenceDataList) {
        this.geoFenceDataList = geoFenceDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.geofencing_listitem, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GeoFenceData geoFenceData = geoFenceDataList.get(position);
        holder.nameTextView.setText(geoFenceData.getName());
        String info ="Lat:" + geoFenceData.getLat() +", Lat:" + geoFenceData.getLat() +", Accuracy:" + geoFenceData.getAccuracy();
        holder.locDetailsTextView.setText(info);
    }

    @Override
    public int getItemCount() {
        return geoFenceDataList.size();
    }

     class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, locDetailsTextView;

        public MyViewHolder(View view) {
            super(view);
            nameTextView =  view.findViewById(R.id.locName);
            locDetailsTextView = view.findViewById(R.id.locInfo);
        }
    }
}
