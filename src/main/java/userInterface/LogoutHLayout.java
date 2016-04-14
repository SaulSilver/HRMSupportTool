package userInterface;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import userInterface.DefaultUI.LogoutSubWindow;

/**
 * Created by Abeer on 4/13/2016.
 */
public class LogoutHLayout extends HorizontalLayout {
    private String currentUser ;
    @Autowired
    public LogoutHLayout( String currentUser){

        this.currentUser= currentUser;
        Label CurrentUserLabel = new Label("you are logged in as "+currentUser);
        Button logoutButton = new Button("Logout", new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                UI.getCurrent().addWindow(new LogoutSubWindow());

            }
        });
        addComponents(CurrentUserLabel, logoutButton);
        setSpacing(true);

    }


}