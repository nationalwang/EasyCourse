package com.zhaoweihao.architechturesample.timeline;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zhaoweihao.architechturesample.R;
import com.zhaoweihao.architechturesample.data.course.QuerySelect;
import com.zhaoweihao.architechturesample.database.User;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

public class DoubanMomentFragment extends Fragment implements DoubanMomentContract.View{

    public static final String TAG = "DoubanMomentFragment";

    private RecyclerView rv_query_select_course_list;
    private SwipeRefreshLayout query_select_refresh;
    private LinearLayout query_select_empty_view;
    private DoubanMomentContract.Presenter presenter;
    private DoubanMomentAdapter adapter;
    private String url;
    private Boolean checkTecOrStu;

    public DoubanMomentFragment() {

    }

    public static DoubanMomentFragment newInstance() {
        return new DoubanMomentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline_page, container, false);

        initViews(view);

        checkTecOrStu = presenter.checkTecOrStu();
        String suffix = "course/querySelectByStuId";
        User user3 = DataSupport.findLast(User.class);
        if (user3.getStudentId() == null && !(user3.getTeacherId() == null)) {
            Toast.makeText(getActivity(), "您不是学生！", Toast.LENGTH_SHORT).show();
        } else if (!(user3.getStudentId() == null) && user3.getTeacherId() == null) {
            url = suffix+"?"+"stuId="+user3.getUserId();
            presenter.querySelect(url);
            query_select_refresh.setOnRefreshListener(() -> {
                presenter.querySelect(url);
                adapter.notifyDataSetChanged();
                stopLoading();
            });
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setPresenter(DoubanMomentContract.Presenter presenter) {
        if (presenter != null) {
            this.presenter = presenter;
        }
    }


    @Override
    public void initViews(View view) {
        rv_query_select_course_list= view.findViewById(R.id.recycler_view);
        rv_query_select_course_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        query_select_refresh = view.findViewById(R.id.refresh_layout);
        query_select_empty_view= view.findViewById(R.id.empty_view);
    }

    @Override
    public void showResult(ArrayList<QuerySelect> queryArrayList) {
        getActivity().runOnUiThread(() -> {
            if (adapter == null) {
                adapter = new DoubanMomentAdapter(getActivity(), queryArrayList,false);
                rv_query_select_course_list.setAdapter(adapter);
            }
            else {
                adapter.notifyDataSetChanged();
            }
            adapter.setItemClickListener((v, position) -> {
                ArrayList<QuerySelect> queries = presenter.getQueryList();
                QuerySelect query = queries.get(position);

            });
            adapter.setItemLongClickListener((view, position) -> {
                // 处理长按行为
            });

            query_select_empty_view.setVisibility(queryArrayList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        });
    }

    @Override
    public void startLoading() {
        query_select_refresh.post(() -> query_select_refresh.setRefreshing(true));
    }

    @Override
    public void stopLoading() {
        if (query_select_refresh.isRefreshing()){
            query_select_refresh.post(() -> query_select_refresh.setRefreshing(false));
        }
    }

    @Override
    public void showLoadError(String error) {
        Snackbar.make(rv_query_select_course_list, error, Snackbar.LENGTH_SHORT)
                .setAction("重试", view -> presenter.querySelect(url)).show();
    }

    @Override
    public void showSelectSuccess(Boolean status) {

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }
}
