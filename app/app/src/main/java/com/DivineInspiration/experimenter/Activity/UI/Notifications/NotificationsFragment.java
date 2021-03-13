<<<<<<< HEAD:app/app/src/main/java/com/DivineInspiration/experimenter/Activity/UI/Notifications/NotificationsFragment.java
package com.DivineInspiration.experimenter.Activity.UI.Notifications;
=======
package com.DivineInspiration.experimenter.Activity.ui.explore;
>>>>>>> Aniket:app/app/src/main/java/com/DivineInspiration/experimenter/Activity/ui/explore/ExploreFragment.java

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.DivineInspiration.experimenter.R;

public class ExploreFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_explore, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}