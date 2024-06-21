package com.MeghaElectronicals.adapter;

import static com.MeghaElectronicals.common.MyFunctions.convertDate;
import static com.MeghaElectronicals.common.MyFunctions.nullCheck;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.MeghaElectronicals.R;
import com.MeghaElectronicals.common.MySharedPreference;
import com.MeghaElectronicals.databinding.TaskListItemBinding;
import com.MeghaElectronicals.modal.TasksListModal;
import com.MeghaElectronicals.views.MainActivity;
import com.MeghaElectronicals.views.UpdateTaskDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder> {

    private final List<TasksListModal> tasksList = new ArrayList<>();
    private final Context context;
    private final MainActivity activity;

    public TaskListAdapter(MainActivity mainActivity) {
        this.context = mainActivity.getApplicationContext();
        this.activity = mainActivity;
    }

    public void addTasksList(List<TasksListModal> newTasksList) {
        Log.d("TaskListAdapterTaskListAdapter", "addTasksList");
        // Doesn't work like this
//        DiffUtil.Callback diffCallback = new DiffUtil.Callback() {
//
//            @Override
//            public int getOldListSize() {
//                return oldList.size();
//            }
//
//            @Override
//            public int getNewListSize() {
//                return newList.size();
//            }
//
//            @Override
//            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//                // Compare data
//                return oldList.get(oldItemPosition).toString().equals(newList.get(newItemPosition).toString());
//            }
//
//            @Override
//            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//                // Compare data
//                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
//            }
//        };
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtil(tasksList, newTasksList));
        tasksList.clear();
        tasksList.addAll(newTasksList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskListViewHolder(TaskListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder h, int position) {

        String EmpId = new MySharedPreference(context).fetchLogin().EmpId();
        String Role = new MySharedPreference(context).fetchLogin().Role();

        TasksListModal modal = tasksList.get(position);

        h.ui.employeeName.setText(nullCheck(modal.EmployeName()));
        h.ui.departmentName.setText(nullCheck(modal.Department()));
        h.ui.progress.setText(nullCheck(modal.Status()));
        h.ui.assignedBy.setText(setColoredText("Assigned by: ", nullCheck(modal.CreatedName()).equalsIgnoreCase(Role) ? "You" : modal.CreatedName(), true));
        h.ui.taskName.setText(nullCheck(modal.TaskName()));
        h.ui.taskDesc.setText(nullCheck(modal.Description()));
        h.ui.startDate.setText(setColoredText("Start\n", convertDate(nullCheck(modal.StartDate())), false));
        h.ui.endDate.setText(setColoredText("End\n", convertDate(nullCheck(modal.EndDate())), false));

        if (!modal.Status().equalsIgnoreCase(context.getString(R.string.inprogress))) {
            h.ui.completionDesc.setVisibility(View.VISIBLE);
            h.ui.completionDate.setVisibility(View.VISIBLE);
            h.ui.completionDesc.setText(setColoredText("Completion: ", modal.CompletionDescription(), true));
            h.ui.completionDate.setText(setColoredText("Completion\n", convertDate(modal.CompletionDate()), false));
        }

        setStyleAsStatus(h.ui, modal.ColorsName());

//        Log.d("TAG", "onBindViewHolder: " + modal.AssignedToId() + " --- " + EmpId);
//        Log.d("TAG", "onBindViewHolder: " + modal.CreatedBy());

        boolean isClickable = (EmpId.equals(modal.CreatedBy()) || EmpId.equals(modal.AssignedToId()))
                && modal.Status().equalsIgnoreCase(context.getString(R.string.inprogress));

        // TODO: see if DiffCallback is working and set material card not clickable
        if (isClickable) {
            h.ui.updateTaskBtn.setVisibility(View.VISIBLE);
            h.ui.taskItemClick.setOnClickListener(v -> {
//                activity.openBottomSheet();
                if (!UpdateTaskDialogFragment.isBottomSheetUp) {
                    UpdateTaskDialogFragment updateTaskDialog = UpdateTaskDialogFragment.newInstance(modal.TaskName(), modal.TaskId());
                    updateTaskDialog.show(activity.getSupportFragmentManager(), "UpdateTaskDialogFragment");
                }
            });
            h.ui.taskItemClick.setClickable(true);
            h.ui.taskItemClick.setFocusable(true);
        } else {
            h.ui.updateTaskBtn.setVisibility(View.GONE);
            h.ui.taskItemClick.setOnClickListener(null);
            h.ui.taskItemClick.setClickable(false);
            h.ui.taskItemClick.setFocusable(false);
        }

//        try {
//            float progress = getProgress(modal);
//            h.h.ui.progressBar.setProgressCompat((int) progress, true);
//            h.h.ui.progressText.setText(String.valueOf(progress).concat("%"));
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

    private void setStyleAsStatus(TaskListItemBinding ui, String s) {
        int color = switch (s) {
            case "Red", "RED" -> {
                ui.statusImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hot));
                yield ContextCompat.getColor(context, R.color.RED);
            }
            case "Blue" -> {
                ui.statusImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cold));
                yield ContextCompat.getColor(context, R.color.Blue);
            }
            case "Yellow" -> {
                ui.statusImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.warm));
                yield ContextCompat.getColor(context, R.color.Yellow);
            }
            case "Green" -> {
                ui.statusImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.chilled));
                yield ContextCompat.getColor(context, R.color.Green);
            }
            default -> {
                ui.statusImg.setVisibility(View.GONE);
                yield ContextCompat.getColor(context, R.color.White);
            }
        };

        ui.materialCardView.setStrokeColor(color);
        ui.progressCard.setCardBackgroundColor(color);
//        h.ui.view.setBackgroundTintList(ColorStateList.valueOf(color));
//        h.ui.employeeName.setTextColor(Color.parseColor(s));
//        h.ui.departmentName.setTextColor(Color.parseColor(s));
    }

    public SpannableString setColoredText(String prefix, String content, boolean setItalic) {
        if (nullCheck(content).equals("-")) return new SpannableString("-");
        String fullText = prefix + content;
        SpannableString spannableString = new SpannableString(fullText);
        int color = ContextCompat.getColor(context, R.color.tertiary);
        spannableString.setSpan(new ForegroundColorSpan(color), 0, prefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (setItalic)
            spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, prefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public static class TaskListViewHolder extends RecyclerView.ViewHolder {

        protected TaskListItemBinding ui;

        public TaskListViewHolder(@NonNull TaskListItemBinding ui) {
            super(ui.getRoot());
            this.ui = ui;
        }
    }

    private static class MyDiffUtil extends DiffUtil.Callback {

        private final List<TasksListModal> oldList;
        private final List<TasksListModal> newList;

        public MyDiffUtil(List<TasksListModal> oldList, List<TasksListModal> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            // Compare data
            return oldList.get(oldItemPosition).toString().equals(newList.get(newItemPosition).toString());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            // Compare data
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}
