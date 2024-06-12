package com.MeghaElectronicals.adapter;

import static com.MeghaElectronicals.common.MyFunctions.convertDate;
import static com.MeghaElectronicals.common.MyFunctions.nullCheck;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.MeghaElectronicals.databinding.TaskListItemBinding;
import com.MeghaElectronicals.modal.TasksListModal;
import com.MeghaElectronicals.views.MainActivity;
import com.MeghaElectronicals.views.UpdateTaskDialogFragment;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder> {

    private List<TasksListModal> tasksList;
    private final Context context;
    private final MainActivity activity;

    public TaskListAdapter(List<TasksListModal> tasksList, MainActivity mainActivity) {
        this.tasksList = tasksList;
        this.context = mainActivity.getApplicationContext();
        this.activity = mainActivity;
    }

    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskListViewHolder(TaskListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder h, int position) {
        TasksListModal modal = tasksList.get(position);

        h.ui.employeeName.setText(nullCheck(modal.EmployeName()));
        h.ui.progress.setText(nullCheck(modal.Status()));
        h.ui.taskName.setText(nullCheck(modal.TaskName()));
        h.ui.taskDesc.setText(nullCheck(modal.Description()));
        h.ui.startDate.setText(convertDate(nullCheck(modal.StartDate())));
        h.ui.endDate.setText(convertDate(nullCheck(modal.EndDate())));

        setColor(h.ui, modal.ColorsName());

        h.ui.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                activity.openBottomSheet();
                UpdateTaskDialogFragment updateTaskDialog = UpdateTaskDialogFragment.newInstance(modal.TaskName(), modal.TaskId());
                updateTaskDialog.show(activity.getSupportFragmentManager(), "UpdateTaskDialogFragment");
            }
        });

//        try {
//            float progress = getProgress(modal);
//            h.ui.progressBar.setProgressCompat((int) progress, true);
//            h.ui.progressText.setText(String.valueOf(progress).concat("%"));
//        } catch (Exception e) {
//            e.fillInStackTrace();
//        }

    }
//
//    private float getProgress(TasksListModal modal) throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
//        Date startDate = sdf.parse(modal.StartDate());
//        Date endDate = sdf.parse(modal.EndDate());
//        long currentInMilli = Calendar.getInstance().getTimeInMillis();
//        long startInMilli = startDate.getTime();
//        long endInMilli = endDate.getTime();
//        long diffDate = endInMilli - startInMilli;
//        long diffCurrent = currentInMilli - startInMilli;
//        Log.d("PROGRESS", String.format("currentInMilli: %s, startInMilli: %s, endInMilli: %s, diffDate: %s, diffCurrent: %s, progress %s",
//                currentInMilli, startInMilli, endInMilli, diffDate, diffCurrent, (diffCurrent / diffDate * 100) / 10));
//        return (float) (diffCurrent / diffDate * 100) / 10;
//    }

    private void setColor(TaskListItemBinding ui, String s) {
        ui.view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(s)));
        ui.employeeName.setTextColor(Color.parseColor(s));
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public class TaskListViewHolder extends RecyclerView.ViewHolder {
        TaskListItemBinding ui;

        public TaskListViewHolder(@NonNull TaskListItemBinding ui) {
            super(ui.getRoot());
            this.ui = ui;
        }
    }
}
