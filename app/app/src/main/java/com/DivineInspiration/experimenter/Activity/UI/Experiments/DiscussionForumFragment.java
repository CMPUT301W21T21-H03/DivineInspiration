package com.DivineInspiration.experimenter.Activity.UI.Experiments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.DivineInspiration.experimenter.Controller.CommentManager;
import com.DivineInspiration.experimenter.Model.Comment.Comment;
import com.DivineInspiration.experimenter.R;

import java.util.ArrayList;
import java.util.List;

public class DiscussionForumFragment extends Fragment implements CommentManager.OnCommentsReadyListener, CreateCommentDialogFragment.OnCommentCreatedListener {

    private List<Comment> commentList;
    private CommentListAdapter adapter;
    private String experiment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        experiment = bundle.getString("experimentID");
        if (experiment == null) { throw new NullPointerException(); }

        this.commentList = new ArrayList<>();
        this.adapter = new CommentListAdapter(getContext(), getActivity().getSupportFragmentManager() , experiment);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.comments_tab_fragment, container, false);

        CommentManager.getInstance().getExperimentComments(experiment, this);

        // Initialize list
        RecyclerView recyclerView = root.findViewById(R.id.comment_list_forum);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        return root;
    }

    @Override
    public void onCommentsReady(List<Comment> comments) {
        Log.d("COMMENT FRAG", "COMMENTS READY");
        Log.d("COMMENT FRAG", comments.toString());
        commentList = comments;
        adapter.setComments(comments);
    }

    @Override
    public void onCommentAdded(Comment comment) {
        commentList.add(0, comment);
        adapter.setComments(commentList);
        adapter.notifyDataSetChanged();
    }
}
